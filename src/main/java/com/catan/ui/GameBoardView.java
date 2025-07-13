package com.catan.ui;

import com.catan.model.*;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Displays the game board with square tiles, vertices (corners), and edges.
 */
public class GameBoardView extends GridPane {
    private final GameState gameState;
    private final GameBoard gameBoard;
    private Runnable onAction;
    private Vertex selectedVertex;
    private Edge selectedEdge;

    public GameBoardView(GameState gameState) {
        this.gameState = gameState;
        this.gameBoard = gameState.getGameBoard();
        
        setupBoard();
    }

    private void setupBoard() {
        setHgap(2);
        setVgap(2);
        setPadding(new Insets(10));
        
        // Create a larger grid to accommodate vertices and edges
        // Each tile is 2x2 grid cells, with vertices at intersections
        for (int row = 0; row < gameBoard.getRows() * 2; row++) {
            for (int col = 0; col < gameBoard.getCols() * 2; col++) {
                if (row % 2 == 0 && col % 2 == 0) {
                    // Vertex position
                    int vertexRow = row / 2;
                    int vertexCol = col / 2;
                    VertexView vertexView = new VertexView(vertexRow, vertexCol);
                    add(vertexView, col, row);
                } else if (row % 2 == 0 && col % 2 == 1) {
                    // Horizontal edge position
                    int edgeRow = row / 2;
                    int edgeCol = col / 2;
                    EdgeView edgeView = new EdgeView(edgeRow, edgeCol, true);
                    add(edgeView, col, row);
                } else if (row % 2 == 1 && col % 2 == 0) {
                    // Vertical edge position
                    int edgeRow = row / 2;
                    int edgeCol = col / 2;
                    EdgeView edgeView = new EdgeView(edgeRow, edgeCol, false);
                    add(edgeView, col, row);
                } else {
                    // Tile position
                    int tileRow = row / 2;
                    int tileCol = col / 2;
                    GameBoard.Tile tile = gameBoard.getTile(tileRow, tileCol);
                    TileView tileView = new TileView(tile, tileRow, tileCol);
                    add(tileView, col, row);
                }
            }
        }
    }

    public void setOnAction(Runnable onAction) {
        this.onAction = onAction;
    }

    private void notifyAction() {
        if (onAction != null) {
            onAction.run();
        }
    }

    /**
     * Individual tile view component.
     */
    private class TileView extends StackPane {
        private final GameBoard.Tile tile;
        private final int row;
        private final int col;
        private final Rectangle background;
        private final Label numberLabel;
        private final Label terrainLabel;

        public TileView(GameBoard.Tile tile, int row, int col) {
            this.tile = tile;
            this.row = row;
            this.col = col;
            
            // Create background rectangle
            this.background = new Rectangle(80, 80);
            background.setStroke(Color.BLACK);
            background.setStrokeWidth(2);
            
            if (tile == null) {
                // Empty field: just gray background, no labels, no click
                this.terrainLabel = null;
                this.numberLabel = null;
                background.setFill(Color.LIGHTGRAY);
                getChildren().add(background);
                setDisable(true);
                return;
            }
            // Create terrain label
            this.terrainLabel = new Label(tile.getTerrainType().getEmoji());
            terrainLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            
            // Create number label
            this.numberLabel = new Label();
            if (tile.getNumber() != null) {
                numberLabel.setText(String.valueOf(tile.getNumber()));
                numberLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                numberLabel.setTextFill(getNumberColor(tile.getNumber()));
            }
            
            // Setup layout
            getChildren().addAll(background, terrainLabel, numberLabel);
            setBackgroundColor();
            
            // Add click handler
            setOnMouseClicked(e -> handleTileClick());
        }

        private void setBackgroundColor() {
            Color color = getTerrainColor(tile.getTerrainType());
            background.setFill(color);
        }

        private Color getTerrainColor(TerrainType terrainType) {
            return switch (terrainType) {
                case FOREST -> Color.FORESTGREEN;
                case HILLS -> Color.SADDLEBROWN;
                case MOUNTAINS -> Color.GRAY;
                case FIELDS -> Color.GOLD;
                case PASTURE -> Color.LIGHTGREEN;
                case DESERT -> Color.SANDYBROWN;
            };
        }

        private Color getNumberColor(int number) {
            if (number == 6 || number == 8) {
                return Color.RED; // Robber numbers
            }
            return Color.BLACK;
        }

        private void handleTileClick() {
            // Handle tile selection for building placement
            System.out.println("Tile clicked: " + tile.getTerrainType().getGermanName() + 
                             " (Row: " + row + ", Col: " + col + ")");
            notifyAction();
        }
    }

    /**
     * Vertex (corner) view component for settlements and cities.
     */
    private class VertexView extends StackPane {
        private final int row;
        private final int col;
        private final Circle circle;
        private final Label buildingLabel;

        public VertexView(int row, int col) {
            this.row = row;
            this.col = col;
            
            // Create circle for vertex
            this.circle = new Circle(8);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(2);
            circle.setFill(Color.TRANSPARENT);
            
            // Create building label
            this.buildingLabel = new Label();
            buildingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            
            // Setup layout
            getChildren().addAll(circle, buildingLabel);
            updateDisplay();
            
            // Add click handler
            setOnMouseClicked(e -> handleVertexClick());
        }

        private void updateDisplay() {
            Vertex vertex = gameBoard.getVertex(row, col);
            if (vertex != null && vertex.isOccupied()) {
                Player owner = vertex.getOwner();
                Vertex.BuildingType buildingType = vertex.getBuildingType();
                
                // Set color based on owner
                circle.setFill(getPlayerColor(owner));
                
                // Set building symbol
                if (buildingType == Vertex.BuildingType.SETTLEMENT) {
                    buildingLabel.setText("🏠");
                } else if (buildingType == Vertex.BuildingType.CITY) {
                    buildingLabel.setText("🏛️");
                }
            } else {
                circle.setFill(Color.TRANSPARENT);
                buildingLabel.setText("");
            }
        }

        private Color getPlayerColor(Player player) {
            int playerIndex = gameState.getPlayers().indexOf(player);
            return switch (playerIndex) {
                case 0 -> Color.RED;
                case 1 -> Color.BLUE;
                case 2 -> Color.GREEN;
                case 3 -> Color.YELLOW;
                default -> Color.GRAY;
            };
        }

        private void handleVertexClick() {
            if (gameState.getCurrentPhase() == GameState.GamePhase.SETUP) {
                // In setup phase, allow building without resource costs
                Player currentPlayer = gameState.getCurrentPlayer();
                Vertex vertex = gameBoard.getVertex(row, col);
                
                if (vertex != null && vertex.canBuildSettlement(currentPlayer)) {
                    if (gameState.buildSettlementAtVertex(row, col, currentPlayer)) {
                        updateDisplay();
                        notifyAction();
                        System.out.println("Siedlung gebaut an Vertex (" + row + ", " + col + ")");
                    }
                }
            }
        }
    }

    /**
     * Edge view component for roads.
     */
    private class EdgeView extends StackPane {
        private final int row;
        private final int col;
        private final boolean isHorizontal;
        private final Line line;

        public EdgeView(int row, int col, boolean isHorizontal) {
            this.row = row;
            this.col = col;
            this.isHorizontal = isHorizontal;
            
            // Create line for edge
            if (isHorizontal) {
                this.line = new Line(0, 40, 80, 40);
            } else {
                this.line = new Line(40, 0, 40, 80);
            }
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(3);
            
            // Setup layout
            getChildren().add(line);
            updateDisplay();
            
            // Add click handler
            setOnMouseClicked(e -> handleEdgeClick());
        }

        private void updateDisplay() {
            Edge edge = isHorizontal ? 
                gameBoard.getHorizontalEdge(row, col) : 
                gameBoard.getVerticalEdge(row, col);
                
            if (edge != null && edge.isOccupied()) {
                Player owner = edge.getOwner();
                line.setStroke(getPlayerColor(owner));
                line.setStrokeWidth(4);
            } else {
                line.setStroke(Color.BLACK);
                line.setStrokeWidth(3);
            }
        }

        private Color getPlayerColor(Player player) {
            int playerIndex = gameState.getPlayers().indexOf(player);
            return switch (playerIndex) {
                case 0 -> Color.RED;
                case 1 -> Color.BLUE;
                case 2 -> Color.GREEN;
                case 3 -> Color.YELLOW;
                default -> Color.GRAY;
            };
        }

        private void handleEdgeClick() {
            if (gameState.getCurrentPhase() == GameState.GamePhase.SETUP) {
                // In setup phase, allow building without resource costs
                Player currentPlayer = gameState.getCurrentPlayer();
                Edge edge = isHorizontal ? 
                    gameBoard.getHorizontalEdge(row, col) : 
                    gameBoard.getVerticalEdge(row, col);
                
                if (edge != null && edge.canBuildRoad(currentPlayer)) {
                    boolean success = isHorizontal ? 
                        gameState.buildRoadAtHorizontalEdge(row, col, currentPlayer) :
                        gameState.buildRoadAtVerticalEdge(row, col, currentPlayer);
                        
                    if (success) {
                        updateDisplay();
                        notifyAction();
                        System.out.println("Straße gebaut an Edge (" + row + ", " + col + ", " + 
                                         (isHorizontal ? "H" : "V") + ")");
                    }
                }
            }
        }
    }
} 