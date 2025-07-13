package com.catan.ui;

import com.catan.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
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
        
        // Create a grid to accommodate all vertices and edges
        // We need (rows+1) * 2 for vertices and edges vertically
        // We need (cols+1) * 2 for vertices and edges horizontally
        int gridRows = (gameBoard.getRows() + 1) * 2;
        int gridCols = (gameBoard.getCols() + 1) * 2;
        
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
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
        
        // Add legend
        addLegend();
    }
    
    private void addLegend() {
        VBox legend = new VBox(5);
        legend.setPadding(new Insets(10));
        legend.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 1;");
        
        Label title = new Label("Resource Legend");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        legend.getChildren().add(title);
        
        // Add legend items
        addLegendItem(legend, "Forest", Color.FORESTGREEN, "Wood");
        addLegendItem(legend, "Hills", Color.SADDLEBROWN, "Brick");
        addLegendItem(legend, "Mountains", Color.GRAY, "Ore");
        addLegendItem(legend, "Fields", Color.GOLD, "Grain");
        addLegendItem(legend, "Pasture", Color.LIGHTGREEN, "Wool");
        addLegendItem(legend, "Desert", Color.SANDYBROWN, "No Resource");
        
        // Add to the right side of the board - account for new grid size
        int gridCols = (gameBoard.getCols() + 1) * 2;
        add(legend, gridCols, 0, 1, (gameBoard.getRows() + 1) * 2);
    }
    
    private void addLegendItem(VBox legend, String terrainName, Color color, String resource) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        
        Rectangle colorBox = new Rectangle(20, 20);
        colorBox.setFill(color);
        colorBox.setStroke(Color.BLACK);
        colorBox.setStrokeWidth(1);
        
        Label label = new Label(terrainName + " → " + resource);
        label.setFont(Font.font("Arial", 12));
        
        item.getChildren().addAll(colorBox, label);
        legend.getChildren().add(item);
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

        public TileView(GameBoard.Tile tile, int row, int col) {
            this.tile = tile;
            this.row = row;
            this.col = col;
            
            // Create background rectangle - larger
            this.background = new Rectangle(120, 120);
            background.setStroke(Color.BLACK);
            background.setStrokeWidth(2);
            
            // Protection against null tile
            if (tile == null) {
                this.numberLabel = new Label();
                background.setFill(Color.LIGHTGRAY);
                getChildren().add(background);
                setDisable(true);
                return;
            }
            // Create number label - larger
            this.numberLabel = new Label();
            if (tile.getNumber() != null) {
                numberLabel.setText(String.valueOf(tile.getNumber()));
                numberLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                numberLabel.setTextFill(getNumberColor(tile.getNumber()));
            }
            
            // Setup layout - only background and number
            getChildren().addAll(background, numberLabel);
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
            System.out.println("Tile clicked: " + tile.getTerrainType().getDisplayName() + 
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
            
            // Create circle for vertex - larger
            this.circle = new Circle(15);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(3);
            circle.setFill(Color.TRANSPARENT);
            
            // Create building label - larger
            this.buildingLabel = new Label();
            buildingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            
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
                case 1 -> Color.rgb(100, 149, 237, 0.7); // Transparent blue
                case 2 -> Color.GREEN;
                case 3 -> Color.YELLOW;
                default -> Color.GRAY;
            };
        }

        private void handleVertexClick() {
            Player currentPlayer = gameState.getCurrentPlayer();
            Vertex vertex = gameBoard.getVertex(row, col);
            
            if (gameState.getCurrentPhase() == GameState.GamePhase.SETUP) {
                // In setup phase, allow building without resource costs
                if (vertex != null && vertex.canBuildSettlement(currentPlayer)) {
                    if (gameState.buildSettlementAtVertex(row, col, currentPlayer)) {
                        updateDisplay();
                        notifyAction();
                        System.out.println("Settlement built at Vertex (" + row + ", " + col + ")");
                    }
                }
            } else if (gameState.getCurrentPhase() == GameState.GamePhase.PLAY) {
                // In play phase, check what can be built
                if (vertex != null) {
                    if (vertex.isOccupied() && vertex.getOwner() == currentPlayer) {
                        // Can upgrade settlement to city
                        if (vertex.getBuildingType() == Vertex.BuildingType.SETTLEMENT) {
                            if (gameState.buildCityAtVertex(row, col, currentPlayer)) {
                                updateDisplay();
                                notifyAction();
                                System.out.println("City built at Vertex (" + row + ", " + col + ")");
                            }
                        }
                    } else if (!vertex.isOccupied()) {
                        // Can build new settlement
                        if (gameState.buildSettlementAtVertex(row, col, currentPlayer)) {
                            updateDisplay();
                            notifyAction();
                            System.out.println("Settlement built at Vertex (" + row + ", " + col + ")");
                        }
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
            
            // Create line for edge - larger
            if (isHorizontal) {
                this.line = new Line(0, 60, 120, 60);
            } else {
                this.line = new Line(60, 0, 60, 120);
            }
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(4);
            
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
                line.setStrokeWidth(5);
            } else {
                line.setStroke(Color.BLACK);
                line.setStrokeWidth(4);
            }
        }

        private Color getPlayerColor(Player player) {
            int playerIndex = gameState.getPlayers().indexOf(player);
            return switch (playerIndex) {
                case 0 -> Color.RED;
                case 1 -> Color.rgb(100, 149, 237, 0.7); // Transparent blue
                case 2 -> Color.GREEN;
                case 3 -> Color.YELLOW;
                default -> Color.GRAY;
            };
        }

        private void handleEdgeClick() {
            Player currentPlayer = gameState.getCurrentPlayer();
            Edge edge = isHorizontal ? 
                gameBoard.getHorizontalEdge(row, col) : 
                gameBoard.getVerticalEdge(row, col);
            
            if (gameState.getCurrentPhase() == GameState.GamePhase.SETUP) {
                // In setup phase, allow building without resource costs
                if (edge != null && edge.canBuildRoad(currentPlayer, true)) { // Allow without connection in setup
                    boolean success = isHorizontal ? 
                        gameState.buildRoadAtHorizontalEdge(row, col, currentPlayer) :
                        gameState.buildRoadAtVerticalEdge(row, col, currentPlayer);
                        
                    if (success) {
                        updateDisplay();
                        notifyAction();
                        System.out.println("Road built at Edge (" + row + ", " + col + ", " + 
                                         (isHorizontal ? "H" : "V") + ")");
                    }
                }
            } else if (gameState.getCurrentPhase() == GameState.GamePhase.PLAY) {
                // In play phase, check resource costs
                if (edge != null && edge.canBuildRoad(currentPlayer, false)) { // Require connection in play phase
                    boolean success = isHorizontal ? 
                        gameState.buildRoadAtHorizontalEdge(row, col, currentPlayer) :
                        gameState.buildRoadAtVerticalEdge(row, col, currentPlayer);
                        
                    if (success) {
                        updateDisplay();
                        notifyAction();
                        System.out.println("Road built at Edge (" + row + ", " + col + ", " + 
                                         (isHorizontal ? "H" : "V") + ")");
                    }
                }
            }
        }
    }
} 