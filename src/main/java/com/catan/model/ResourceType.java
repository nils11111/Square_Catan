package com.catan.model;

/**
 * Enum representing the different resource types in Catan.
 * Based on the original 1995 game rules.
 */
public enum ResourceType {
    WOOD("Holz", "🌲"),
    BRICK("Lehm", "🧱"),
    ORE("Erz", "⛏️"),
    GRAIN("Getreide", "🌾"),
    WOOL("Wolle", "🐑");

    private final String germanName;
    private final String emoji;

    ResourceType(String germanName, String emoji) {
        this.germanName = germanName;
        this.emoji = emoji;
    }

    public String getGermanName() {
        return germanName;
    }

    public String getEmoji() {
        return emoji;
    }

    @Override
    public String toString() {
        return germanName;
    }
} 