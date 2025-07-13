package com.catan.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the resource costs for different buildings in Catan.
 * Based on the original 1995 game rules.
 */
public class BuildingCosts {
    private static final Map<BuildingType, Map<ResourceType, Integer>> COSTS = new HashMap<>();

    public enum BuildingType {
        SETTLEMENT("Settlement"),
        CITY("City"),
        ROAD("Road");

        private final String displayName;

        BuildingType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    static {
        // Settlement costs: 1 wood, 1 brick, 1 grain, 1 wool
        Map<ResourceType, Integer> settlementCost = new HashMap<>();
        settlementCost.put(ResourceType.WOOD, 1);
        settlementCost.put(ResourceType.BRICK, 1);
        settlementCost.put(ResourceType.GRAIN, 1);
        settlementCost.put(ResourceType.WOOL, 1);
        COSTS.put(BuildingType.SETTLEMENT, settlementCost);

        // City costs: 2 grain, 3 ore
        Map<ResourceType, Integer> cityCost = new HashMap<>();
        cityCost.put(ResourceType.GRAIN, 2);
        cityCost.put(ResourceType.ORE, 3);
        COSTS.put(BuildingType.CITY, cityCost);

        // Road costs: 1 wood, 1 brick
        Map<ResourceType, Integer> roadCost = new HashMap<>();
        roadCost.put(ResourceType.WOOD, 1);
        roadCost.put(ResourceType.BRICK, 1);
        COSTS.put(BuildingType.ROAD, roadCost);
    }

    public static Map<ResourceType, Integer> getCost(BuildingType buildingType) {
        return new HashMap<>(COSTS.get(buildingType));
    }

    public static boolean canAfford(Player player, BuildingType buildingType) {
        Map<ResourceType, Integer> cost = getCost(buildingType);
        
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            ResourceType resource = entry.getKey();
            int required = entry.getValue();
            
            if (player.getResourceCount(resource) < required) {
                return false;
            }
        }
        return true;
    }

    public static boolean payCost(Player player, BuildingType buildingType) {
        if (!canAfford(player, buildingType)) {
            return false;
        }

        Map<ResourceType, Integer> cost = getCost(buildingType);
        
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            ResourceType resource = entry.getKey();
            int amount = entry.getValue();
            
            player.removeResource(resource, amount);
        }
        return true;
    }

    public static String getCostString(BuildingType buildingType) {
        Map<ResourceType, Integer> cost = getCost(buildingType);
        StringBuilder sb = new StringBuilder();
        
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(entry.getValue()).append(" ").append(entry.getKey().getDisplayName());
        }
        
        return sb.toString();
    }
} 