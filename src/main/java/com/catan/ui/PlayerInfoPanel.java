package com.catan.ui;

import com.catan.model.*;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Displays player information including resources and victory points.
 */
public class PlayerInfoPanel extends VBox {
    private final GameState gameState;
    private final Label[] playerLabels;
    private final Label[][] resourceLabels;

    public PlayerInfoPanel(GameState gameState) {
        this.gameState = gameState;
        this.playerLabels = new Label[gameState.getPlayers().size()];
        this.resourceLabels = new Label[gameState.getPlayers().size()][ResourceType.values().length];
        
        setupPanel();
        updateDisplay();
    }

    private void setupPanel() {
        setPadding(new Insets(10));
        setSpacing(10);
        setPrefWidth(250);
        setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #ccc;");

        // Title
        Label titleLabel = new Label("Spieler Information");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setPadding(new Insets(0, 0, 10, 0));
        getChildren().add(titleLabel);

        // Create player sections
        for (int i = 0; i < gameState.getPlayers().size(); i++) {
            Player player = gameState.getPlayers().get(i);
            VBox playerSection = createPlayerSection(player, i);
            getChildren().add(playerSection);
        }

        // Make it scrollable
        ScrollPane scrollPane = new ScrollPane(this);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(600);
    }

    private VBox createPlayerSection(Player player, int playerIndex) {
        VBox section = new VBox(5);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

        // Player name and victory points
        playerLabels[playerIndex] = new Label();
        playerLabels[playerIndex].setFont(Font.font("Arial", FontWeight.BOLD, 14));
        section.getChildren().add(playerLabels[playerIndex]);

        // Resources
        Label resourcesTitle = new Label("Ressourcen:");
        resourcesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        section.getChildren().add(resourcesTitle);

        VBox resourcesBox = new VBox(2);
        for (int i = 0; i < ResourceType.values().length; i++) {
            ResourceType resource = ResourceType.values()[i];
            resourceLabels[playerIndex][i] = new Label();
            resourceLabels[playerIndex][i].setFont(Font.font("Arial", 11));
            resourcesBox.getChildren().add(resourceLabels[playerIndex][i]);
        }
        section.getChildren().add(resourcesBox);

        // Buildings
        Label buildingsTitle = new Label("Gebäude:");
        buildingsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        section.getChildren().add(buildingsTitle);

        Label buildingsLabel = new Label();
        buildingsLabel.setFont(Font.font("Arial", 11));
        buildingsLabel.setUserData(playerIndex); // Store player index for updates
        section.getChildren().add(buildingsLabel);

        return section;
    }

    public void updateDisplay() {
        for (int i = 0; i < gameState.getPlayers().size(); i++) {
            Player player = gameState.getPlayers().get(i);
            
            // Update player label
            String playerText = player.getName();
            if (player == gameState.getCurrentPlayer()) {
                playerText += " (Aktiv)";
                playerLabels[i].setTextFill(Color.BLUE);
            } else {
                playerLabels[i].setTextFill(Color.BLACK);
            }
            playerText += " - " + player.getVictoryPoints() + " Siegpunkte";
            playerLabels[i].setText(playerText);

            // Update resource labels
            for (int j = 0; j < ResourceType.values().length; j++) {
                ResourceType resource = ResourceType.values()[j];
                int count = player.getResourceCount(resource);
                resourceLabels[i][j].setText(resource.getEmoji() + " " + 
                                           resource.getGermanName() + ": " + count);
            }

            // Update buildings label
            String buildingsText = "Siedlungen: " + player.getSettlements() + 
                                 " | Städte: " + player.getCities() + 
                                 " | Straßen: " + player.getRoads();
            
            // Find the buildings label in the player section
            VBox playerSection = (VBox) getChildren().get(i + 1); // +1 for title
            for (javafx.scene.Node node : playerSection.getChildren()) {
                if (node instanceof Label && node.getUserData() != null && 
                    (Integer) node.getUserData() == i) {
                    ((Label) node).setText(buildingsText);
                    break;
                }
            }
        }
    }
} 