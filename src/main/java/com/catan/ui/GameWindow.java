package com.catan.ui;

import com.catan.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Main game window containing the game board and UI elements.
 */
public class GameWindow extends BorderPane {
    private final GameState gameState;
    private final GameBoardView gameBoardView;
    private final PlayerInfoPanel playerInfoPanel;
    private ControlPanel controlPanel;
    private final Label statusLabel;

    public GameWindow() {
        // Initialize game state with 4 players
        this.gameState = new GameState(4);
        
        // Create UI components
        this.gameBoardView = new GameBoardView(gameState);
        this.playerInfoPanel = new PlayerInfoPanel(gameState);
        this.controlPanel = new ControlPanel(gameState);
        
        // Create status label
        this.statusLabel = new Label(gameState.getGameStatus());
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        statusLabel.setPadding(new Insets(10));
        statusLabel.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc;");
        
        // Setup layout
        setupLayout();
        
        // Add event listeners for updates
        setupEventListeners();
    }

    private void setupLayout() {
        // Center: Game board
        setCenter(gameBoardView);
        
        // Right: Player information
        setRight(playerInfoPanel);
        
        // Left: Control panel (instead of bottom)
        setLeft(controlPanel);
        
        // Top: Status bar
        setTop(statusLabel);
        
        // Set padding
        setPadding(new Insets(10));
    }

    private void setupEventListeners() {
        // Update status when game state changes
        gameState.getCurrentPlayer().addVictoryPoints(0); // Trigger update
        
        // Listen for game state changes
        controlPanel.setOnAction(() -> updateStatus());
        gameBoardView.setOnAction(() -> updateStatus());
        
        // Also update when control panel changes (for phase transitions)
        controlPanel.setOnAction(() -> {
            updateStatus();
            // Recreate control panel if phase changed
            if (gameState.getCurrentPhase() == GameState.GamePhase.PLAY) {
                recreateControlPanel();
            }
        });
    }

    private void updateStatus() {
        statusLabel.setText(gameState.getGameStatus());
        playerInfoPanel.updateDisplay();
        
        if (gameState.isGameEnded()) {
            showGameOverDialog();
        }
    }
    
    private void recreateControlPanel() {
        // Remove old control panel
        setLeft(null);
        
        // Create new control panel
        ControlPanel newControlPanel = new ControlPanel(gameState);
        newControlPanel.setOnAction(() -> {
            updateStatus();
            // Recreate control panel if phase changed
            if (gameState.getCurrentPhase() == GameState.GamePhase.PLAY) {
                recreateControlPanel();
            }
        });
        
        // Set new control panel
        setLeft(newControlPanel);
        this.controlPanel = newControlPanel;
    }

    private void showGameOverDialog() {
        // Winner animation: large, colorful window with congratulations
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("🎉 Game Over - We Have a Winner! 🎉");
        alert.setHeaderText("Congratulations!");
        alert.setContentText(gameState.getWinner().getName() + " has won with " + 
                           gameState.getWinner().getVictoryPoints() + " victory points!\n\n🎊🎊🎊");
        // Animation: color change (simple, since JavaFX Alert is limited)
        DialogPane pane = alert.getDialogPane();
        pane.setStyle("-fx-background-color: linear-gradient(to bottom, #fff700, #ff7f00, #ff007f, #7f00ff, #007fff, #00ff7f);" +
                      "-fx-font-size: 20px; -fx-font-weight: bold;");
        // Confetti emoji as decoration
        Label confetti = new Label("🎉🎊🎉");
        confetti.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 48));
        pane.setGraphic(confetti);
        ButtonType newGameButton = new ButtonType("New Game");
        ButtonType exitButton = new ButtonType("Exit");
        alert.getButtonTypes().setAll(newGameButton, exitButton);
        alert.showAndWait().ifPresent(response -> {
            if (response == newGameButton) {
                // TODO: Implement new game functionality
                System.out.println("Starting new game...");
            } else {
                System.exit(0);
            }
        });
    }
} 