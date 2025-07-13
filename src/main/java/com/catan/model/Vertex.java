package com.catan.model;

/**
 * Represents a vertex (corner point) where settlements and cities can be built.
 * Each vertex connects to adjacent tiles and can hold one building.
 */
public class Vertex {
    private final int row;
    private final int col;
    private Player owner;
    private BuildingType buildingType;

    public enum BuildingType {
        SETTLEMENT("Settlement"),
        CITY("City");

        private final String displayName;

        BuildingType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Vertex(int row, int col) {
        this.row = row;
        this.col = col;
        this.owner = null;
        this.buildingType = null;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public BuildingType getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(BuildingType buildingType) {
        this.buildingType = buildingType;
    }

    public boolean isOccupied() {
        return buildingType != null;
    }

    public boolean canBuildSettlement(Player player) {
        return !isOccupied() && player.getSettlements() > 0;
    }

    public boolean canBuildCity(Player player) {
        return buildingType == BuildingType.SETTLEMENT && 
               owner == player && 
               player.getCities() > 0;
    }

    public boolean buildSettlement(Player player) {
        if (canBuildSettlement(player)) {
            setOwner(player);
            setBuildingType(BuildingType.SETTLEMENT);
            player.useSettlement();
            player.addVictoryPoints(1);
            return true;
        }
        return false;
    }

    public boolean buildCity(Player player) {
        if (canBuildCity(player)) {
            setBuildingType(BuildingType.CITY);
            player.useCity();
            player.addVictoryPoints(1);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Vertex(" + row + ", " + col + ")";
    }
} 