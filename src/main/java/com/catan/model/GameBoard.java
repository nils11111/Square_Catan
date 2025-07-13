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
            }
        }

        // Initialize vertical edges
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col <= cols; col++) {
                verticalEdges[row][col] = new Edge(row, col, false);
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