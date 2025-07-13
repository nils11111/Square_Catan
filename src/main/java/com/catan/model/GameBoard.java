package com.catan.model;

import java.util.*;

/**
 * Represents the game board for Square Catan.
 * 
 * <p>This class manages the complete game board structure including tiles, vertices (corners), 
 * and edges (roads). The board uses a 6x6 grid system with square tiles instead of the 
 * traditional hexagonal tiles. The board contains 36 total tiles, with 19 playable tiles 
 * following the original Catan distribution and 17 additional tiles to fill the square grid.</p>
 * 
 * <p>The board coordinates are organized as follows:</p>
 * <ul>
 *   <li>Tiles: 6x6 grid (rows 0-5, columns 0-5)</li>
 *   <li>Vertices: 7x7 grid (rows 0-6, columns 0-6) representing corners between tiles</li>
 *   <li>Horizontal Edges: 7x6 grid connecting vertices horizontally</li>
 *   <li>Vertical Edges: 6x7 grid connecting vertices vertically</li>
 * </ul>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Automatic terrain and number distribution based on original Catan rules</li>
 *   <li>Adjacency calculations for tiles, vertices, and edges</li>
 *   <li>Road network validation for building placement</li>
 *   <li>Resource production coordination</li>
 * </ul>
 * 
 * <p>This class is thread-safe for read operations but should be accessed from a single 
 * thread for modifications during gameplay.</p>
 * 
 * @author Development Team
 * @version 1.0
 * @since 1.0
 */
public class GameBoard {
    /** The 2D array of tiles representing the game board */
    private final Tile[][] tiles;
    
    /** The 2D array of vertices (corners) where settlements and cities can be built */
    private final Vertex[][] vertices;
    
    /** The 2D array of horizontal edges where roads can be built */
    private final Edge[][] horizontalEdges;
    
    /** The 2D array of vertical edges where roads can be built */
    private final Edge[][] verticalEdges;
    
    /** Number of rows in the board */
    private final int rows;
    
    /** Number of columns in the board */
    private final int cols;
    
    /** Random number generator for board initialization */
    private final Random random;

    // Coordinates of the empty field (e.g. center-ish)
    private static final int EMPTY_ROW = 2;
    private static final int EMPTY_COL = 2;

    /**
     * Constructs a new game board with the standard 6x6 layout.
     * 
     * <p>This constructor initializes the board with:</p>
     * <ul>
     *   <li>6x6 tile grid with terrain and number distribution</li>
     *   <li>7x7 vertex grid for building placement</li>
     *   <li>7x6 horizontal edges and 6x7 vertical edges for roads</li>
     * </ul>
     * 
     * <p>The board is automatically initialized with proper terrain distribution 
     * and shuffled number tokens following original Catan rules.</p>
     */
    public GameBoard() {
        this.rows = 6;
        this.cols = 6;
        this.tiles = new Tile[rows][cols];
        this.vertices = new Vertex[rows + 1][cols + 1]; // +1 for corners
        this.horizontalEdges = new Edge[rows + 1][cols]; // horizontal edges between tiles
        this.verticalEdges = new Edge[rows][cols + 1];   // vertical edges between tiles
        this.random = new Random();
        initializeBoard();
        initializeVerticesAndEdges();
    }

    /**
     * Initializes the game board with terrain types and number tokens.
     * 
     * <p>This method creates the terrain distribution optimized for a 6x6 grid:</p>
     * <ul>
     *   <li>8 Forest tiles (Wood) - 22.2%</li>
     *   <li>7 Hills tiles (Brick) - 19.4%</li>
     *   <li>7 Mountains tiles (Ore) - 19.4%</li>
     *   <li>7 Fields tiles (Grain) - 19.4%</li>
     *   <li>6 Pasture tiles (Wool) - 16.7%</li>
     *   <li>1 Desert tile (no resource) - 2.8%</li>
     * </ul>
     * 
     * <p>The number tokens are distributed as follows:</p>
     * <ul>
     *   <li>Numbers 2 and 12: 4 tokens each</li>
     *   <li>Numbers 3, 4, 5, 6, 8, 9, 10, 11: 4 tokens each</li>
     *   <li>Number 7: Not used (robber)</li>
     * </ul>
     * 
     * <p>Both terrain types and numbers are shuffled randomly for each game.</p>
     */
    private void initializeBoard() {
        // Create terrain distribution optimized for 6x6 grid
        List<TerrainType> terrainTypes = createTerrainDistribution();
        List<Integer> numbers = createNumberDistribution();
        
        // Verify we have exactly 36 terrain types and 35 numbers
        int totalTiles = rows * cols;
        if (terrainTypes.size() != totalTiles) {
            throw new IllegalStateException("Terrain distribution must contain exactly " + totalTiles + " tiles");
        }
        if (numbers.size() != 35) {
            throw new IllegalStateException("Number distribution must contain exactly 35 numbers");
        }
        
        // Shuffle both lists
        Collections.shuffle(terrainTypes, random);
        Collections.shuffle(numbers, random);
        
        int terrainIndex = 0;
        int numberIndex = 0;
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                TerrainType terrain = terrainTypes.get(terrainIndex++);
                Integer number = terrain == TerrainType.DESERT ? null : numbers.get(numberIndex++);
                tiles[row][col] = new Tile(terrain, number);
            }
        }
    }

    /**
     * Initializes all vertices and edges on the game board.
     * 
     * <p>This method creates:</p>
     * <ul>
     *   <li>All vertices (corners) where settlements and cities can be built</li>
     *   <li>All horizontal edges where roads can be built</li>
     *   <li>All vertical edges where roads can be built</li>
     * </ul>
     * 
     * <p>Each edge is connected to the game board for validation purposes.</p>
     */
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

    /**
     * Creates the terrain distribution for the game board.
     * 
     * <p>This method creates a list of terrain types optimized for a 6x6 grid (36 tiles).
     * The distribution is balanced to provide fair gameplay:</p>
     * <ul>
     *   <li>8 Forest tiles (Wood resource) - 22.2%</li>
     *   <li>7 Hills tiles (Brick resource) - 19.4%</li>
     *   <li>7 Mountains tiles (Ore resource) - 19.4%</li>
     *   <li>7 Fields tiles (Grain resource) - 19.4%</li>
     *   <li>6 Pasture tiles (Wool resource) - 16.7%</li>
     *   <li>1 Desert tile (no resource) - 2.8%</li>
     * </ul>
     * 
     * <p>This distribution ensures balanced resource availability with minimal
     * desert tiles for strategic robber placement.</p>
     * 
     * @return A list of terrain types ready for shuffling and placement
     */
    private List<TerrainType> createTerrainDistribution() {
        List<TerrainType> terrain = new ArrayList<>();
        
        // Optimized distribution for 36 tiles (6x6 grid) with 1 desert
        terrain.addAll(Collections.nCopies(8, TerrainType.FOREST));    // 8 forests (22.2%)
        terrain.addAll(Collections.nCopies(7, TerrainType.HILLS));     // 7 hills (19.4%)
        terrain.addAll(Collections.nCopies(7, TerrainType.MOUNTAINS)); // 7 mountains (19.4%)
        terrain.addAll(Collections.nCopies(7, TerrainType.FIELDS));    // 7 fields (19.4%)
        terrain.addAll(Collections.nCopies(6, TerrainType.PASTURE));   // 6 pastures (16.7%)
        terrain.add(TerrainType.DESERT);                               // 1 desert (2.8%)
        
        return terrain;
    }

    /**
     * Creates the number token distribution for the game board.
     * 
     * <p>This method creates a list of number tokens optimized for 35 productive tiles
     * (36 total tiles minus 1 desert tile). The distribution is:</p>
     * <ul>
     *   <li>Numbers 2 and 12: 4 tokens each</li>
     *   <li>Numbers 3, 4, 5, 6, 8, 9, 10, 11: 4 tokens each</li>
     *   <li>Number 7: Not included (robber)</li>
     * </ul>
     * 
     * <p>This creates a total of 35 tokens for 35 productive tiles, ensuring
     * every productive tile gets a number token.</p>
     * 
     * @return A list of number tokens ready for shuffling and placement
     */
    private List<Integer> createNumberDistribution() {
        List<Integer> numbers = new ArrayList<>();
        // For 35 productive fields (36 total - 1 desert): distribute 35 numbers
        // Distribution: 2, 12 each 4x; 3, 4, 5, 6, 8, 9, 10, 11 each 4x; plus one extra 8
        numbers.addAll(Collections.nCopies(4, 2));
        numbers.addAll(Collections.nCopies(4, 12));
        numbers.addAll(Collections.nCopies(4, 3));
        numbers.addAll(Collections.nCopies(4, 4));
        numbers.addAll(Collections.nCopies(4, 5));
        numbers.addAll(Collections.nCopies(4, 6));
        numbers.addAll(Collections.nCopies(5, 8));  // 5 tokens for 8 (4+1 extra)
        numbers.addAll(Collections.nCopies(4, 9));
        numbers.addAll(Collections.nCopies(4, 10));
        numbers.addAll(Collections.nCopies(4, 11));
        // Results in 35 numbers for 35 productive tiles
        return numbers;
    }

    /**
     * Gets the tile at the specified coordinates.
     * 
     * @param row The row coordinate (0-5)
     * @param col The column coordinate (0-5)
     * @return The tile at the specified coordinates, or null if coordinates are invalid
     */
    public Tile getTile(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            return tiles[row][col];
        }
        return null;
    }

    /**
     * Gets the vertex at the specified coordinates.
     * 
     * @param row The row coordinate (0-6)
     * @param col The column coordinate (0-6)
     * @return The vertex at the specified coordinates, or null if coordinates are invalid
     */
    public Vertex getVertex(int row, int col) {
        if (row >= 0 && row <= rows && col >= 0 && col <= cols) {
            return vertices[row][col];
        }
        return null;
    }

    /**
     * Gets the horizontal edge at the specified coordinates.
     * 
     * @param row The row coordinate (0-6)
     * @param col The column coordinate (0-5)
     * @return The horizontal edge at the specified coordinates, or null if coordinates are invalid
     */
    public Edge getHorizontalEdge(int row, int col) {
        if (row >= 0 && row <= rows && col >= 0 && col < cols) {
            return horizontalEdges[row][col];
        }
        return null;
    }

    /**
     * Gets the vertical edge at the specified coordinates.
     * 
     * @param row The row coordinate (0-5)
     * @param col The column coordinate (0-6)
     * @return The vertical edge at the specified coordinates, or null if coordinates are invalid
     */
    public Edge getVerticalEdge(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col <= cols) {
            return verticalEdges[row][col];
        }
        return null;
    }

    /**
     * Gets the number of rows in the board.
     * 
     * @return The number of rows (6)
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gets the number of columns in the board.
     * 
     * @return The number of columns (6)
     */
    public int getCols() {
        return cols;
    }

    /**
     * Gets all tiles adjacent to the specified tile.
     * 
     * <p>This method returns the four orthogonally adjacent tiles (up, down, left, right).
     * Diagonal adjacency is not considered.</p>
     * 
     * @param row The row coordinate of the center tile
     * @param col The column coordinate of the center tile
     * @return A list of adjacent tiles (may be empty if no valid adjacent tiles exist)
     */
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

    /**
     * Gets all vertices adjacent to the specified tile.
     * 
     * <p>This method returns the four corner vertices of the specified tile.
     * These are the vertices where settlements and cities can be built.</p>
     * 
     * @param row The row coordinate of the tile
     * @param col The column coordinate of the tile
     * @return A list of adjacent vertices (typically 4 vertices)
     */
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

    /**
     * Gets all edges adjacent to the specified tile.
     * 
     * <p>This method returns the four edges surrounding the specified tile.
     * These are the edges where roads can be built.</p>
     * 
     * @param row The row coordinate of the tile
     * @param col The column coordinate of the tile
     * @return A list of adjacent edges (typically 4 edges)
     */
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
     * Gets the two vertices connected by a horizontal edge.
     * 
     * @param edgeRow The row coordinate of the edge
     * @param edgeCol The column coordinate of the edge
     * @return A list containing the two vertices connected by this edge
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
     * Gets the two vertices connected by a vertical edge.
     * 
     * @param edgeRow The row coordinate of the edge
     * @param edgeCol The column coordinate of the edge
     * @return A list containing the two vertices connected by this edge
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
     * Checks if a road can be built at the specified edge by connecting to existing buildings.
     * 
     * <p>This method validates that:</p>
     * <ul>
     *   <li>The edge is not already occupied</li>
     *   <li>The player has roads available</li>
     *   <li>The edge connects to the player's existing buildings or road network</li>
     * </ul>
     * 
     * @param edgeRow The row coordinate of the edge
     * @param edgeCol The column coordinate of the edge
     * @param isHorizontal True if this is a horizontal edge, false if vertical
     * @param player The player attempting to build the road
     * @return True if a road can be built, false otherwise
     */
    public boolean canBuildRoadAtEdge(int edgeRow, int edgeCol, boolean isHorizontal, Player player) {
        return canBuildRoadAtEdge(edgeRow, edgeCol, isHorizontal, player, false);
    }

    /**
     * Checks if a road can be built at the specified edge by connecting to existing buildings.
     * 
     * <p>This method validates road placement with additional flexibility for setup phase.
     * During setup, roads can be built without connecting to existing buildings.</p>
     * 
     * @param edgeRow The row coordinate of the edge
     * @param edgeCol The column coordinate of the edge
     * @param isHorizontal True if this is a horizontal edge, false if vertical
     * @param player The player attempting to build the road
     * @param allowWithoutConnection If true, allows building roads without building connections (for setup phase)
     * @return True if a road can be built, false otherwise
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
     * Checks if there's a road network connection to this edge.
     * 
     * <p>This method recursively explores the road network to determine if the 
     * specified edge can be reached from the player's existing buildings or roads.</p>
     * 
     * @param edgeRow The row coordinate of the edge
     * @param edgeCol The column coordinate of the edge
     * @param isHorizontal True if this is a horizontal edge, false if vertical
     * @param player The player attempting to build the road
     * @return True if there's a road network connection, false otherwise
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
     * Recursively checks if there's a road network path to a vertex.
     * 
     * <p>This method uses depth-first search to explore the road network and 
     * determine if a path exists from the player's buildings to the target vertex.</p>
     * 
     * @param targetVertex The vertex to reach
     * @param player The player whose road network to check
     * @param visited A set of already visited vertices to prevent infinite loops
     * @return True if there's a path to the vertex, false otherwise
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

    /**
     * Triggers resource production for all tiles with the specified dice roll.
     * 
     * <p>This method is called when dice are rolled. It identifies all tiles with 
     * the rolled number and marks them for resource production. The actual resource 
     * distribution is handled by the game logic when settlements and cities are present.</p>
     * 
     * @param diceRoll The dice roll value (2-12)
     */
    public void produceResources(int diceRoll) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Tile tile = tiles[row][col];
                if (tile != null && tile.getNumber() != null && tile.getNumber() == diceRoll && tile.getTerrainType().producesResource()) {
                    // This tile produces resources - will be handled by game logic
                    // when settlements/cities are placed
                }
            }
        }
    }

    /**
     * Represents a single tile on the game board.
     * 
     * <p>Each tile has a terrain type and an optional number token. The terrain type 
     * determines what resource the tile produces, and the number token determines 
     * when resources are produced (when that number is rolled).</p>
     * 
     * <p>Tiles are immutable once created to ensure game consistency.</p>
     * 
     * @author Development Team
     * @version 1.0
     * @since 1.0
     */
    public static class Tile {
        /** The terrain type of this tile */
        private final TerrainType terrainType;
        
        /** The number token on this tile (null for desert) */
        private final Integer number;

        /**
         * Constructs a new tile with the specified terrain type and number.
         * 
         * @param terrainType The terrain type of the tile
         * @param number The number token on the tile (null for desert)
         */
        public Tile(TerrainType terrainType, Integer number) {
            this.terrainType = terrainType;
            this.number = number;
        }

        /**
         * Gets the terrain type of this tile.
         * 
         * @return The terrain type
         */
        public TerrainType getTerrainType() {
            return terrainType;
        }

        /**
         * Gets the number token on this tile.
         * 
         * @return The number token, or null if this tile has no number (desert)
         */
        public Integer getNumber() {
            return number;
        }

        /**
         * Checks if this tile is a desert.
         * 
         * @return True if this tile is a desert, false otherwise
         */
        public boolean isDesert() {
            return terrainType == TerrainType.DESERT;
        }
    }
} 