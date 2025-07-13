package com.catan.model;

/**
 * Represents an edge where roads can be built.
 * Each edge connects two vertices and can hold one road.
 */
public class Edge {
    private final int row;
    private final int col;
    private final boolean isHorizontal; // true = horizontal edge, false = vertical edge
    private Player owner;
    private GameBoard gameBoard; // Reference to game board for validation

    public Edge(int row, int col, boolean isHorizontal) {
        this.row = row;
        this.col = col;
        this.isHorizontal = isHorizontal;
        this.owner = null;
        this.gameBoard = null; // Will be set by GameBoard
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public boolean isOccupied() {
        return owner != null;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public boolean canBuildRoad(Player player) {
        return canBuildRoad(player, false);
    }

    public boolean canBuildRoad(Player player, boolean isSetupPhase) {
        if (isOccupied() || player.getRoads() <= 0) {
            return false;
        }
        
        // If we have a game board reference, use the advanced validation
        if (gameBoard != null) {
            return gameBoard.canBuildRoadAtEdge(row, col, isHorizontal, player, isSetupPhase);
        }
        
        // Fallback to basic validation
        return true;
    }

    public boolean buildRoad(Player player) {
        if (canBuildRoad(player)) {
            setOwner(player);
            player.useRoad();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Edge(" + row + ", " + col + ", " + (isHorizontal ? "H" : "V") + ")";
    }
} 