package com.catan.model;

import java.util.*;

/**
 * Manages the overall state of the Catan game.
 * Handles turns, game phases, and player management.
 */
public class GameState {
    private final List<Player> players;
    private final GameBoard gameBoard;
    private int currentPlayerIndex;
    private GamePhase currentPhase;
    private SetupPhase setupPhase;
    private int diceRoll;
    private boolean gameEnded;
    private Player winner;
    
    // Setup phase tracking - much simpler
    private boolean settlementBuilt; // Has current player built settlement this turn?
    private boolean roadBuilt;       // Has current player built road this turn?

    public enum GamePhase {
        SETUP("Aufbau"),
        PLAY("Spiel"),
        GAME_OVER("Spielende");

        private final String germanName;

        GamePhase(String germanName) {
            this.germanName = germanName;
        }

        public String getGermanName() {
            return germanName;
        }
    }

    public enum SetupPhase {
        FORWARD("Vorwärts"),
        BACKWARD("Rückwärts");

        private final String germanName;

        SetupPhase(String germanName) {
            this.germanName = germanName;
        }

        public String getGermanName() {
            return germanName;
        }
    }

    public GameState(int playerCount) {
        if (playerCount < 2 || playerCount > 4) {
            throw new IllegalArgumentException("Player count must be between 2 and 4");
        }

        this.players = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            players.add(new Player("Spieler " + i));
        }

        // Create a 5x4 board with 19 tiles
        this.gameBoard = new GameBoard();
        this.currentPlayerIndex = 0;
        this.currentPhase = GamePhase.SETUP;
        this.setupPhase = SetupPhase.FORWARD;
        this.diceRoll = 0;
        this.gameEnded = false;
        this.winner = null;
        
        // Initialize setup tracking
        this.settlementBuilt = false;
        this.roadBuilt = false;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public SetupPhase getSetupPhase() {
        return setupPhase;
    }

    public int getDiceRoll() {
        return diceRoll;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean isSettlementBuilt() {
        return settlementBuilt;
    }

    public boolean isRoadBuilt() {
        return roadBuilt;
    }

    private void nextPlayer() {
        if (currentPhase == GamePhase.SETUP) {
            if (setupPhase == SetupPhase.FORWARD) {
                // Forward: 0 -> 1 -> 2 -> 3
                if (currentPlayerIndex < players.size() - 1) {
                    currentPlayerIndex++;
                } else {
                    // Switch to backward setup
                    setupPhase = SetupPhase.BACKWARD;
                    currentPlayerIndex = players.size() - 1; // Start with last player
                }
            } else {
                // Backward: 3 -> 2 -> 1 -> 0
                if (currentPlayerIndex > 0) {
                    currentPlayerIndex--;
                } else {
                    // Setup complete, start normal play
                    endSetupPhase();
                    return;
                }
            }
        } else {
            // Normal play phase
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
        
        // Reset building flags for new player
        settlementBuilt = false;
        roadBuilt = false;
    }

    public void rollDice() {
        if (currentPhase == GamePhase.PLAY) {
            diceRoll = (int) (Math.random() * 6) + (int) (Math.random() * 6) + 2; // 2-12
            System.out.println("Würfel: " + diceRoll);
            produceResources();
            
            // Nach dem Würfeln zum nächsten Spieler wechseln
            nextPlayer();
        }
    }

    private void produceResources() {
        // Check if any player has settlements/cities on tiles with the rolled number
        for (int row = 0; row < gameBoard.getRows(); row++) {
            for (int col = 0; col < gameBoard.getCols(); col++) {
                GameBoard.Tile tile = gameBoard.getTile(row, col);
                if (tile != null && tile.getNumber() != null && tile.getNumber() == diceRoll && tile.getTerrainType().producesResource()) {
                    // Find vertices adjacent to this tile
                    List<Vertex> adjacentVertices = gameBoard.getAdjacentVertices(row, col);
                    for (Vertex vertex : adjacentVertices) {
                        if (vertex.isOccupied()) {
                            Player owner = vertex.getOwner();
                            ResourceType resource = tile.getTerrainType().getResourceType();
                            int amount = vertex.getBuildingType() == Vertex.BuildingType.CITY ? 2 : 1;
                            owner.addResource(resource, amount);
                            System.out.println(owner.getName() + " erhält " + amount + "x " + resource.getGermanName());
                        }
                    }
                }
            }
        }
    }

    public void endSetupPhase() {
        if (currentPhase == GamePhase.SETUP) {
            currentPhase = GamePhase.PLAY;
            currentPlayerIndex = 0; // Start with first player
            // Reset dice roll for new phase
            diceRoll = 0;
        }
    }

    // Building methods for vertices (settlements and cities)
    public boolean buildSettlementAtVertex(int vertexRow, int vertexCol, Player player) {
        if (player != getCurrentPlayer()) {
            return false; // Only current player can build
        }
        
        if (currentPhase == GamePhase.SETUP) {
            if (settlementBuilt) {
                return false; // Already built settlement this turn
            }
        } else if (currentPhase == GamePhase.PLAY) {
            // In play phase, check resource costs
            if (!BuildingCosts.canAfford(player, BuildingCosts.BuildingType.SETTLEMENT)) {
                return false;
            }
        }
        
        Vertex vertex = gameBoard.getVertex(vertexRow, vertexCol);
        if (vertex != null && vertex.canBuildSettlement(player)) {
            if (currentPhase == GamePhase.PLAY) {
                // Pay resources in play phase
                if (!BuildingCosts.payCost(player, BuildingCosts.BuildingType.SETTLEMENT)) {
                    return false;
                }
            }
            
            if (vertex.buildSettlement(player)) {
                if (currentPhase == GamePhase.SETUP) {
                    settlementBuilt = true;
                }
                checkForWinner(player);
                return true;
            }
        }
        return false;
    }

    public boolean buildCityAtVertex(int vertexRow, int vertexCol, Player player) {
        // Cities cannot be built in setup phase
        if (currentPhase == GamePhase.SETUP) {
            return false;
        }
        
        Vertex vertex = gameBoard.getVertex(vertexRow, vertexCol);
        if (vertex != null && vertex.canBuildCity(player)) {
            if (BuildingCosts.canAfford(player, BuildingCosts.BuildingType.CITY)) {
                if (BuildingCosts.payCost(player, BuildingCosts.BuildingType.CITY) && 
                    vertex.buildCity(player)) {
                    checkForWinner(player);
                    return true;
                }
            }
        }
        return false;
    }

    // Building methods for edges (roads)
    public boolean buildRoadAtHorizontalEdge(int edgeRow, int edgeCol, Player player) {
        if (currentPhase == GamePhase.SETUP) {
            if (player != getCurrentPlayer()) {
                return false; // Only current player can build in setup
            }
            
            if (!settlementBuilt) {
                return false; // Must build settlement first
            }
            
            if (roadBuilt) {
                return false; // Already built road this turn
            }
            
            Edge edge = gameBoard.getHorizontalEdge(edgeRow, edgeCol);
            if (edge != null && edge.canBuildRoad(player)) {
                if (edge.buildRoad(player)) {
                    roadBuilt = true;
                    // Both settlement and road built, move to next player
                    nextPlayer();
                    return true;
                }
            }
            return false;
        } else {
            // In play phase, check resource costs
            Edge edge = gameBoard.getHorizontalEdge(edgeRow, edgeCol);
            if (edge != null && edge.canBuildRoad(player)) {
                if (BuildingCosts.canAfford(player, BuildingCosts.BuildingType.ROAD)) {
                    if (BuildingCosts.payCost(player, BuildingCosts.BuildingType.ROAD)) {
                        return edge.buildRoad(player);
                    }
                }
            }
            return false;
        }
    }

    public boolean buildRoadAtVerticalEdge(int edgeRow, int edgeCol, Player player) {
        if (currentPhase == GamePhase.SETUP) {
            if (player != getCurrentPlayer()) {
                return false; // Only current player can build in setup
            }
            
            if (!settlementBuilt) {
                return false; // Must build settlement first
            }
            
            if (roadBuilt) {
                return false; // Already built road this turn
            }
            
            Edge edge = gameBoard.getVerticalEdge(edgeRow, edgeCol);
            if (edge != null && edge.canBuildRoad(player)) {
                if (edge.buildRoad(player)) {
                    roadBuilt = true;
                    // Both settlement and road built, move to next player
                    nextPlayer();
                    return true;
                }
            }
            return false;
        } else {
            // In play phase, check resource costs
            Edge edge = gameBoard.getVerticalEdge(edgeRow, edgeCol);
            if (edge != null && edge.canBuildRoad(player)) {
                if (BuildingCosts.canAfford(player, BuildingCosts.BuildingType.ROAD)) {
                    if (BuildingCosts.payCost(player, BuildingCosts.BuildingType.ROAD)) {
                        return edge.buildRoad(player);
                    }
                }
            }
            return false;
        }
    }

    // Legacy methods for backward compatibility
    public boolean canBuildSettlement(Player player) {
        return player.getSettlements() > 0;
    }

    public boolean canBuildCity(Player player) {
        return player.getCities() > 0;
    }

    public boolean canBuildRoad(Player player) {
        return player.getRoads() > 0;
    }

    public boolean buildSettlement(Player player) {
        // This method is now deprecated - use buildSettlementAtVertex instead
        return false;
    }

    public boolean buildCity(Player player) {
        // This method is now deprecated - use buildCityAtVertex instead
        return false;
    }

    public boolean buildRoad(Player player) {
        // This method is now deprecated - use buildRoadAtEdge instead
        return false;
    }

    private void checkForWinner(Player player) {
        if (player.hasWon()) {
            gameEnded = true;
            winner = player;
            currentPhase = GamePhase.GAME_OVER;
        }
    }

    public void tradeResources(Player from, Player to, ResourceType fromResource, ResourceType toResource) {
        if (from.removeResource(fromResource, 1) && to.removeResource(toResource, 1)) {
            from.addResource(toResource, 1);
            to.addResource(fromResource, 1);
        }
    }

    public String getGameStatus() {
        if (gameEnded) {
            return "Spielende! " + winner.getName() + " hat gewonnen mit " + winner.getVictoryPoints() + " Siegpunkten!";
        }
        
        Player current = getCurrentPlayer();
        String phaseInfo = currentPhase == GamePhase.SETUP ? 
            "Phase: " + currentPhase.getGermanName() + " (" + setupPhase.getGermanName() + ")" :
            "Phase: " + currentPhase.getGermanName();
            
        String setupInfo = "";
        if (currentPhase == GamePhase.SETUP) {
            String buildingStatus = "";
            if (!settlementBuilt) {
                buildingStatus = " - Baue zuerst eine Siedlung";
            } else if (!roadBuilt) {
                buildingStatus = " - Baue jetzt eine Straße";
            }
            setupInfo = " | " + current.getName() + buildingStatus;
        }
            
        return phaseInfo + 
               " | Aktueller Spieler: " + current.getName() + 
               " | Siegpunkte: " + current.getVictoryPoints() +
               setupInfo +
               " | Würfel: " + (diceRoll > 0 ? diceRoll : "-");
    }
} 