package com.catan.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a player in the Catan game.
 * Manages player's resources, buildings, and victory points.
 */
public class Player {
    private final String name;
    private final Map<ResourceType, Integer> resources;
    private int settlements;
    private int cities;
    private int roads;
    private int victoryPoints;

    public Player(String name) {
        this.name = name;
        this.resources = new HashMap<>();
        this.settlements = 5; // Starting settlements
        this.cities = 4;      // Starting cities
        this.roads = 15;      // Starting roads
        this.victoryPoints = 0;
        
        // Initialize resources to 0
        for (ResourceType resource : ResourceType.values()) {
            resources.put(resource, 0);
        }
    }

    public String getName() {
        return name;
    }

    public Map<ResourceType, Integer> getResources() {
        return new HashMap<>(resources);
    }

    public int getResourceCount(ResourceType resource) {
        return resources.getOrDefault(resource, 0);
    }

    public void addResource(ResourceType resource, int amount) {
        resources.put(resource, resources.getOrDefault(resource, 0) + amount);
    }

    public boolean removeResource(ResourceType resource, int amount) {
        int current = resources.getOrDefault(resource, 0);
        if (current >= amount) {
            resources.put(resource, current - amount);
            return true;
        }
        return false;
    }

    public int getSettlements() {
        return settlements;
    }

    public void useSettlement() {
        if (settlements > 0) {
            settlements--;
        }
    }

    public int getCities() {
        return cities;
    }

    public void useCity() {
        if (cities > 0) {
            cities--;
        }
    }

    public int getRoads() {
        return roads;
    }

    public void useRoad() {
        if (roads > 0) {
            roads--;
        }
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public void addVictoryPoints(int points) {
        this.victoryPoints += points;
    }

    public boolean hasWon() {
        return victoryPoints >= 10;
    }

    public int getTotalResourceCount() {
        return resources.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public String toString() {
        return name;
    }
} 