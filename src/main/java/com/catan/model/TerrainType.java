package com.catan.model;

/**
 * Enum representing the different terrain types in Catan.
 * Each terrain type produces a specific resource when the corresponding number is rolled.
 */
public enum TerrainType {
    FOREST("Forest", ResourceType.WOOD, "🌲"),
    HILLS("Hills", ResourceType.BRICK, "🏔️"),
    MOUNTAINS("Mountains", ResourceType.ORE, "⛰️"),
    FIELDS("Fields", ResourceType.GRAIN, "🌾"),
    PASTURE("Pasture", ResourceType.WOOL, "🐑"),
    DESERT("Desert", null, "🏜️");

    private final String displayName;
    private final ResourceType resourceType;
    private final String emoji;

    TerrainType(String displayName, ResourceType resourceType, String emoji) {
        this.displayName = displayName;
        this.resourceType = resourceType;
        this.emoji = emoji;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public String getEmoji() {
        return emoji;
    }

    public boolean producesResource() {
        return resourceType != null;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 