package com.catan.model;

import java.util.*;

/**
 * Represents the game board for Square Catan.
 * Uses a 5x4 grid with one empty field (null) for 19 playable tiles.
 * Includes vertices (corners) and edges for building placement.
 */
public class GameBoard {
    private final Tile[][] tiles;
    private final Vertex[][] vertices;
    private final Edge[][] horizontalEdges;
    private final Edge[][] verticalEdges;
    private final int rows;
    private final int cols;
    private final Random random;

    // Coordinates of the empty field (e.g. center-ish)
    private static final int EMPTY_ROW = 2;
    private static final int EMPTY_COL = 2;

    public GameBoard() {
        this.rows = 4;
        this.cols = 5;
        this.tiles = new Tile[rows][cols];
        this.vertices = new Vertex[rows + 1][cols + 1]; // +1 for corners
        this.horizontalEdges = new Edge[rows + 1][cols]; // horizontal edges between tiles
        this.verticalEdges = new Edge[rows][cols + 1];   // vertical edges between tiles
        this.random = new Random();
        initializeBoard();
        initializeVerticesAndEdges();
    }

    private void initializeBoard() {
        // Create terrain distribution based on original Catan
        List<TerrainType> terrainTypes = createTerrainDistribution();
        List<Integer> numbers = createNumberDistribution();
        
        // Shuffle both lists
        Collections.shuffle(terrainTypes, random);
        Collections.shuffle(numbers, random);
        
        int terrainIndex = 0;
        int numberIndex = 0;
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (row == EMPTY_ROW && col == EMPTY_COL) {
                    tiles[row][col] = null; // empty field
                    continue;
                }
                TerrainType terrain = terrainTypes.get(terrainIndex++);
                Integer number = terrain == TerrainType.DESERT ? null : numbers.get(numberIndex++);
                tiles[row][col] = new Tile(terrain, number);
            }
        }
    }

    private void initializeVerticesAndEdges() {
        // Initialize vertices (corners)
        for (int row = 0; row <= rows; row++) {
            for (int col = 0; col <= cols; col++) {
                vertices[row][col] = new Vertex(row, col);
            }
        }

        // Initialize horizontal edges
        for (int row = 0; row <= rows; row++) {
            for (int col = 0; col < cols; col++) {
                horizontalEdges[row][col] = new Edge(row, col, true);
                horizontalEdges[row][col].setGameBoard(this);
            }
        }

        // Initialize vertical edges
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col <= cols; col++) {
                verticalEdges[row][col] = new Edge(row, col, false);
                verticalEdges[row][col].setGameBoard(this);
            }
        }
    }

    private List<TerrainType> createTerrainDistribution() {
        List<TerrainType> terrain = new ArrayList<>();
        
        // Based on original Catan distribution (19 tiles)
        terrain.addAll(Collections.nCopies(4, TerrainType.FOREST));    // 4 forests
        terrain.addAll(Collections.nCopies(3, TerrainType.HILLS));     // 3 hills
        terrain.addAll(Collections.nCopies(3, TerrainType.MOUNTAINS)); // 3 mountains
        terrain.addAll(Collections.nCopies(4, TerrainType.FIELDS));    // 4 fields
        terrain.addAll(Collections.nCopies(4, TerrainType.PASTURE));   // 4 pastures
        terrain.add(TerrainType.DESERT);                               // 1 desert
        
        return terrain;
    }

    private List<Integer> createNumberDistribution() {
        List<Integer> numbers = new ArrayList<>();
        
        // Based on original Catan number distribution (18 numbers, desert gets none)
        numbers.addAll(Arrays.asList(2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12));
        
        return numbers;
    }

    public Tile getTile(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            return tiles[row][col];
        }
        return null;
    }

    public Vertex getVertex(int row, int col) {
        if (row >= 0 && row <= rows && col >= 0 && col <= cols) {
            return vertices[row][col];
        }
        return null;
    }

    public Edge getHorizontalEdge(int row, int col) {
        if (row >= 0 && row <= rows && col >= 0 && col < cols) {
            return horizontalEdges[row][col];
        }
        return null;
    }

    public Edge getVerticalEdge(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col <= cols) {
            return verticalEdges[row][col];
        }
        return null;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public List<Tile> getAdjacentTiles(int row, int col) {
        List<Tile> adjacent = new ArrayList<>();
        
        // Check all 4 adjacent squares (up, down, left, right)
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            Tile tile = getTile(newRow, newCol);
            if (tile != null) {
                adjacent.add(tile);
            }
        }
        
        return adjacent;
    }

    public List<Vertex> getAdjacentVertices(int row, int col) {
        List<Vertex> adjacent = new ArrayList<>();
        
        // For a tile at (row, col), the adjacent vertices are at the corners
        Vertex[] corners = {
            getVertex(row, col),           // top-left
            getVertex(row, col + 1),       // top-right
            getVertex(row + 1, col),       // bottom-left
            getVertex(row + 1, col + 1)    // bottom-right
        };
        
        for (Vertex vertex : corners) {
            if (vertex != null) {
                adjacent.add(vertex);
            }
        }
        
        return adjacent;
    }

    public List<Edge> getAdjacentEdges(int row, int col) {
        List<Edge> adjacent = new ArrayList<>();
        
        // For a tile at (row, col), the adjacent edges are:
        Edge top = getHorizontalEdge(row, col);
        Edge bottom = getHorizontalEdge(row + 1, col);
        Edge left = getVerticalEdge(row, col);
        Edge right = getVerticalEdge(row, col + 1);
        
        if (top != null) adjacent.add(top);
        if (bottom != null) adjacent.add(bottom);
        if (left != null) adjacent.add(left);
        if (right != null) adjacent.add(right);
        
        return adjacent;
    }

    /**
     * Get the two vertices connected by a horizontal edge.
     */
    public List<Vertex> getVerticesForHorizontalEdge(int edgeRow, int edgeCol) {
        List<Vertex> vertices = new ArrayList<>();
        Vertex left = getVertex(edgeRow, edgeCol);
        Vertex right = getVertex(edgeRow, edgeCol + 1);
        
        if (left != null) vertices.add(left);
        if (right != null) vertices.add(right);
        
        return vertices;
    }

    /**
     * Get the two vertices connected by a vertical edge.
     */
    public List<Vertex> getVerticesForVerticalEdge(int edgeRow, int edgeCol) {
        List<Vertex> vertices = new ArrayList<>();
        Vertex top = getVertex(edgeRow, edgeCol);
        Vertex bottom = getVertex(edgeRow + 1, edgeCol);
        
        if (top != null) vertices.add(top);
        if (bottom != null) vertices.add(bottom);
        
        return vertices;
    }

    /**
     * Check if a road can be built at the specified edge by connecting to existing buildings.
     */
    public boolean canBuildRoadAtEdge(int edgeRow, int edgeCol, boolean isHorizontal, Player player) {
        return canBuildRoadAtEdge(edgeRow, edgeCol, isHorizontal, player, false);
    }

    /**
     * Check if a road can be built at the specified edge by connecting to existing buildings.
     * @param allowWithoutConnection If true, allows building roads without building connections (for setup phase)
     */
    public boolean canBuildRoadAtEdge(int edgeRow, int edgeCol, boolean isHorizontal, Player player, boolean allowWithoutConnection) {
        Edge edge = isHorizontal ? getHorizontalEdge(edgeRow, edgeCol) : getVerticalEdge(edgeRow, edgeCol);
        if (edge == null || edge.isOccupied() || player.getRoads() <= 0) {
            return false;
        }

        // Get connected vertices
        List<Vertex> connectedVertices = isHorizontal ? 
            getVerticesForHorizontalEdge(edgeRow, edgeCol) : 
            getVerticesForVerticalEdge(edgeRow, edgeCol);

        // Check if at least one connected vertex has a building owned by the player
        for (Vertex vertex : connectedVertices) {
            if (vertex != null && vertex.isOccupied() && vertex.getOwner() == player) {
                return true;
            }
        }

        // If setup phase allows building without connections, return true
        if (allowWithoutConnection) {
            return true;
        }

        // Check if there's a road network connection (for play phase)
        return hasRoadNetworkConnection(edgeRow, edgeCol, isHorizontal, player);
    }

    /**
     * Check if there's a road network connection to this edge.
     */
    private boolean hasRoadNetworkConnection(int edgeRow, int edgeCol, boolean isHorizontal, Player player) {
        // Get connected vertices
        List<Vertex> connectedVertices = isHorizontal ? 
            getVerticesForHorizontalEdge(edgeRow, edgeCol) : 
            getVerticesForVerticalEdge(edgeRow, edgeCol);

        // Check if any connected vertex has a road network
        for (Vertex vertex : connectedVertices) {
            if (vertex != null && hasRoadNetworkToVertex(vertex, player, new HashSet<>())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Recursively check if there's a road network path to a vertex.
     */
    private boolean hasRoadNetworkToVertex(Vertex targetVertex, Player player, Set<String> visited) {
        String vertexKey = targetVertex.getRow() + "," + targetVertex.getCol();
        if (visited.contains(vertexKey)) {
            return false;
        }
        visited.add(vertexKey);

        // Check if this vertex has a building owned by the player
        if (targetVertex.isOccupied() && targetVertex.getOwner() == player) {
            return true;
        }

        // Check all edges connected to this vertex
        int row = targetVertex.getRow();
        int col = targetVertex.getCol();

        // Check horizontal edges
        Edge leftEdge = getHorizontalEdge(row, col - 1);
        Edge rightEdge = getHorizontalEdge(row, col);
        
        // Check vertical edges
        Edge topEdge = getVerticalEdge(row - 1, col);
        Edge bottomEdge = getVerticalEdge(row, col);

        // Check if any of these edges have roads owned by the player
        if ((leftEdge != null && leftEdge.isOccupied() && leftEdge.getOwner() == player) ||
            (rightEdge != null && rightEdge.isOccupied() && rightEdge.getOwner() == player) ||
            (topEdge != null && topEdge.isOccupied() && topEdge.getOwner() == player) ||
            (bottomEdge != null && bottomEdge.isOccupied() && bottomEdge.getOwner() == player)) {
            
            // Follow the road network
            if (leftEdge != null && leftEdge.isOccupied() && leftEdge.getOwner() == player) {
                List<Vertex> leftVertices = getVerticesForHorizontalEdge(row, col - 1);
                for (Vertex v : leftVertices) {
                    if (v != targetVertex && hasRoadNetworkToVertex(v, player, visited)) {
                        return true;
                    }
                }
            }
            if (rightEdge != null && rightEdge.isOccupied() && rightEdge.getOwner() == player) {
                List<Vertex> rightVertices = getVerticesForHorizontalEdge(row, col);
                for (Vertex v : rightVertices) {
                    if (v != targetVertex && hasRoadNetworkToVertex(v, player, visited)) {
                        return true;
                    }
                }
            }
            if (topEdge != null && topEdge.isOccupied() && topEdge.getOwner() == player) {
                List<Vertex> topVertices = getVerticesForVerticalEdge(row - 1, col);
                for (Vertex v : topVertices) {
                    if (v != targetVertex && hasRoadNetworkToVertex(v, player, visited)) {
                        return true;
                    }
                }
            }
            if (bottomEdge != null && bottomEdge.isOccupied() && bottomEdge.getOwner() == player) {
                List<Vertex> bottomVertices = getVerticesForVerticalEdge(row, col);
                for (Vertex v : bottomVertices) {
                    if (v != targetVertex && hasRoadNetworkToVertex(v, player, visited)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void produceResources(int diceRoll) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Tile tile = tiles[row][col];
                if (tile != null && tile.getNumber() == diceRoll && tile.getTerrainType().producesResource()) {
                    // This tile produces resources - will be handled by game logic
                    // when settlements/cities are placed
                }
            }
        }
    }

    /**
     * Represents a single tile on the game board.
     */
    public static class Tile {
        private final TerrainType terrainType;
        private final Integer number;

        public Tile(TerrainType terrainType, Integer number) {
            this.terrainType = terrainType;
            this.number = number;
        }

        public TerrainType getTerrainType() {
            return terrainType;
        }

        public Integer getNumber() {
            return number;
        }

        public boolean isDesert() {
            return terrainType == TerrainType.DESERT;
        }
    }
} 