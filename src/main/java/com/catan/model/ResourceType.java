package com.catan.model;

/**
 * Enum representing the different resource types in Catan.
 * Based on the original 1995 game rules.
 */
public enum ResourceType {
    WOOD("Wood", "🌲"),
    BRICK("Brick", "🧱"),
    ORE("Ore", "⛏️"),
    GRAIN("Grain", "🌾"),
    WOOL("Wool", "🐑");

    private final String displayName;
    private final String emoji;

    ResourceType(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmoji() {
        return emoji;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 