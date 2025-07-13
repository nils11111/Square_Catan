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

    public Edge(int row, int col, boolean isHorizontal) {
        this.row = row;
        this.col = col;
        this.isHorizontal = isHorizontal;
        this.owner = null;
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

    public boolean canBuildRoad(Player player) {
        return !isOccupied() && player.getRoads() > 0;
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