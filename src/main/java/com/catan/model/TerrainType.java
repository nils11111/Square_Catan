package com.catan.model;

/**
 * Enum representing the different terrain types in Catan.
 * Each terrain type produces a specific resource when the corresponding number is rolled.
 */
public enum TerrainType {
    FOREST("Wald", ResourceType.WOOD, "🌲"),
    HILLS("Hügel", ResourceType.BRICK, "🏔️"),
    MOUNTAINS("Berge", ResourceType.ORE, "⛰️"),
    FIELDS("Felder", ResourceType.GRAIN, "🌾"),
    PASTURE("Weiden", ResourceType.WOOL, "🐑"),
    DESERT("Wüste", null, "🏜️");

    private final String germanName;
    private final ResourceType resourceType;
    private final String emoji;

    TerrainType(String germanName, ResourceType resourceType, String emoji) {
        this.germanName = germanName;
        this.resourceType = resourceType;
        this.emoji = emoji;
    }

    public String getGermanName() {
        return germanName;
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
        return germanName;
    }
} 