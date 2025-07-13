package com.catan.ui;

import com.catan.model.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.GridPane;

/**
 * Provides game controls for dice rolling, building, and turn management.
 */
public class ControlPanel extends VBox {
    private final GameState gameState;
    private final Label diceLabel;
    private final Label currentPlayerLabel;
    private Runnable onAction;

    public ControlPanel(GameState gameState) {
        this.gameState = gameState;
        this.diceLabel = new Label("Dice: -");
        this.currentPlayerLabel = new Label();
        
        setupPanel();
        updateDisplay();
    }

    private void setupPanel() {
        setPadding(new Insets(10));
        setSpacing(10);
        setStyle("-fx-background-color: #e8e8e8; -fx-border-color: #ccc;");

        // Title
        Label titleLabel = new Label("Game Controls");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        getChildren().add(titleLabel);

        // Current player info
        currentPlayerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        getChildren().add(currentPlayerLabel);

        // Dice section
        VBox diceSection = createDiceSection();
        getChildren().add(diceSection);

        // Turn management
        VBox turnSection = createTurnSection();
        getChildren().add(turnSection);

        // Game phase management
        if (gameState.getCurrentPhase() == GameState.GamePhase.SETUP) {
            VBox setupSection = createSetupSection();
            getChildren().add(setupSection);
        } else if (gameState.getCurrentPhase() == GameState.GamePhase.PLAY) {
            // Show play phase info
            VBox playSection = createPlaySection();
            getChildren().add(playSection);
        }

        // Instructions at the bottom
        Label instructions = new Label(
            "Instructions:\n" +
            "To build something, click on:\n" +
            "- a corner (circle) for settlement/city\n" +
            "- a road (thick line) for road\n" +
            "Resources are automatically checked."
        );
        instructions.setFont(Font.font("Arial", 12));
        instructions.setWrapText(true);
        instructions.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #ccc; -fx-padding: 8; margin-top: 10px;");
        getChildren().add(instructions);
    }

    private VBox createDiceSection() {
        VBox section = new VBox(5);
        section.setPadding(new Insets(5));
        section.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");

        Label title = new Label("Dice");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        section.getChildren().add(title);

        diceLabel.setFont(Font.font("Arial", 14));
        section.getChildren().add(diceLabel);

        // New dice button - always available
        Button newRollButton = new Button("🎲 ROLL DICE 🎲");
        newRollButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        newRollButton.setOnAction(e -> {
            if (gameState.getCurrentPhase() == GameState.GamePhase.PLAY) {
                gameState.rollDice();
                updateDisplay();
                notifyAction();
                showInfo("Dice rolled!", "Dice: " + gameState.getDiceRoll());
            } else {
                showError("Not available", "Dice rolling is only possible in the play phase!");
            }
        });
        section.getChildren().add(newRollButton);

        return section;
    }

    private VBox createBuildingSection() {
        VBox section = new VBox(5);
        section.setPadding(new Insets(5));
        section.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");

        Label title = new Label("Build Buildings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        section.getChildren().add(title);

        HBox buildingButtons = new HBox(5);
        
        Button settlementButton = new Button("Settlement");
        settlementButton.setOnAction(e -> buildSettlement());
        buildingButtons.getChildren().add(settlementButton);

        Button cityButton = new Button("City");
        cityButton.setOnAction(e -> buildCity());
        buildingButtons.getChildren().add(cityButton);

        Button roadButton = new Button("Road");
        roadButton.setOnAction(e -> buildRoad());
        buildingButtons.getChildren().add(roadButton);

        section.getChildren().add(buildingButtons);
        return section;
    }

    private VBox createTurnSection() {
        VBox section = new VBox(5);
        section.setPadding(new Insets(5));
        section.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");

        Label title = new Label("Turn Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        section.getChildren().add(title);

        // Removed: Button nextTurnButton = new Button("Next Turn");
        // section.getChildren().add(nextTurnButton);

        // Trade button
        Button tradeButton = new Button("Trade");
        tradeButton.setOnAction(e -> openTradeDialog());
        section.getChildren().add(tradeButton);

        return section;
    }

    private VBox createSetupSection() {
        VBox section = new VBox(5);
        section.setPadding(new Insets(5));
        section.setStyle("-fx-background-color: #fff3cd; -fx-border-color: #ffeaa7;");

        Label title = new Label("Setup Phase");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        section.getChildren().add(title);

        String setupInfo = switch (gameState.getSetupPhase()) {
            case FORWARD -> "Forward round: Player 1 → 2 → 3 → 4";
            case BACKWARD -> "Backward round: Player 4 → 3 → 2 → 1";
        };
        
        Label info = new Label(setupInfo);
        info.setFont(Font.font("Arial", 10));
        info.setWrapText(true);
        section.getChildren().add(info);

        Label instructions = new Label("Each player builds: 1. Settlement, 2. Road → automatic switch");
        instructions.setFont(Font.font("Arial", 9));
        instructions.setWrapText(true);
        section.getChildren().add(instructions);

        // Show current player's status
        Player currentPlayer = gameState.getCurrentPlayer();
        String status = "";
        if (!gameState.isSettlementBuilt()) {
            status = currentPlayer.getName() + " must build a settlement";
        } else if (!gameState.isRoadBuilt()) {
            status = currentPlayer.getName() + " must build a road";
        } else {
            status = currentPlayer.getName() + " is finished";
        }
        
        Label progress = new Label(status);
        progress.setFont(Font.font("Arial", 9));
        progress.setStyle("-fx-font-weight: bold;");
        section.getChildren().add(progress);

        return section;
    }

    private VBox createPlaySection() {
        VBox section = new VBox(5);
        section.setPadding(new Insets(5));
        section.setStyle("-fx-background-color: #d4edda; -fx-border-color: #c3e6cb;");

        Label title = new Label("Play Phase");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        section.getChildren().add(title);

        Label info = new Label("Roll the dice to receive resources!");
        info.setFont(Font.font("Arial", 10));
        info.setWrapText(true);
        section.getChildren().add(info);

        return section;
    }

    private void buildSettlement() {
        Player currentPlayer = gameState.getCurrentPlayer();
        if (gameState.buildSettlement(currentPlayer)) {
            showInfo("Settlement built!", "You have successfully built a settlement.");
            updateDisplay();
            notifyAction();
        } else {
            String cost = BuildingCosts.getCostString(BuildingCosts.BuildingType.SETTLEMENT);
            showError("Error", "You cannot build a settlement.\nCost: " + cost + "\nCheck your resources.");
        }
    }

    private void buildCity() {
        Player currentPlayer = gameState.getCurrentPlayer();
        if (gameState.buildCity(currentPlayer)) {
            showInfo("City built!", "You have successfully built a city.");
            updateDisplay();
            notifyAction();
        } else {
            String cost = BuildingCosts.getCostString(BuildingCosts.BuildingType.CITY);
            showError("Error", "You cannot build a city.\nCost: " + cost + "\nCheck your resources.");
        }
    }

    private void buildRoad() {
        Player currentPlayer = gameState.getCurrentPlayer();
        if (gameState.buildRoad(currentPlayer)) {
            showInfo("Road built!", "You have successfully built a road.");
            updateDisplay();
            notifyAction();
        } else {
            String cost = BuildingCosts.getCostString(BuildingCosts.BuildingType.ROAD);
            showError("Error", "You cannot build a road.\nCost: " + cost + "\nCheck your resources.");
        }
    }

    private void nextTurn() {
        // This method is no longer needed as player switching is automatic
        updateDisplay();
        notifyAction();
    }

    private void endSetupPhase() {
        gameState.endSetupPhase();
        updateDisplay();
        notifyAction();
        
        // Recreate the panel to remove setup section and update button states
        getChildren().clear();
        setupPanel();
        updateDisplay(); // Update again after recreating panel
    }

    public void updateDisplay() {
        // Update dice label
        int diceRoll = gameState.getDiceRoll();
        diceLabel.setText("Dice: " + (diceRoll > 0 ? String.valueOf(diceRoll) : "-"));

        // Update current player label
        Player currentPlayer = gameState.getCurrentPlayer();
        currentPlayerLabel.setText("Current Player: " + currentPlayer.getName() + 
                                 " (Victory Points: " + currentPlayer.getVictoryPoints() + ")");
        
        // Roll button state is now handled in the button's action
    }

    public void setOnAction(Runnable onAction) {
        this.onAction = onAction;
    }

    private void notifyAction() {
        if (onAction != null) {
            onAction.run();
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openTradeDialog() {
        Player currentPlayer = gameState.getCurrentPlayer();
        java.util.List<Player> otherPlayers = new java.util.ArrayList<>(gameState.getPlayers());
        otherPlayers.remove(currentPlayer);
        if (otherPlayers.isEmpty()) {
            showError("Trade not possible", "There are no other players.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Offer Trade");
        dialog.setHeaderText("Choose a fellow player and resources for the trade.");

        // Selection fields
        ComboBox<Player> playerBox = new ComboBox<>();
        playerBox.getItems().addAll(otherPlayers);
        playerBox.getSelectionModel().selectFirst();

        ComboBox<ResourceType> giveBox = new ComboBox<>();
        giveBox.getItems().addAll(ResourceType.values());
        giveBox.getSelectionModel().selectFirst();

        ComboBox<ResourceType> getBox = new ComboBox<>();
        getBox.getItems().addAll(ResourceType.values());
        getBox.getSelectionModel().selectFirst();

        // Quantity spinners
        Spinner<Integer> giveAmount = new Spinner<>(1, 99, 1);
        Spinner<Integer> getAmount = new Spinner<>(1, 99, 1);
        giveAmount.setEditable(true);
        getAmount.setEditable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Fellow Player:"), 0, 0);
        grid.add(playerBox, 1, 0);
        grid.add(new Label("I give:"), 0, 1);
        grid.add(giveBox, 1, 1);
        grid.add(giveAmount, 2, 1);
        grid.add(new Label("I get:"), 0, 2);
        grid.add(getBox, 1, 2);
        grid.add(getAmount, 2, 2);

        dialog.getDialogPane().setContent(grid);
        ButtonType offerButtonType = new ButtonType("Offer Trade", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(offerButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == offerButtonType) {
                Player partner = playerBox.getValue();
                ResourceType give = giveBox.getValue();
                ResourceType get = getBox.getValue();
                int giveQty = giveAmount.getValue();
                int getQty = getAmount.getValue();
                if (partner == null || give == null || get == null) {
                    showError("Invalid selection", "Please select everything.");
                    return null;
                }
                if (currentPlayer.getResourceCount(give) < giveQty) {
                    showError("Not enough resources", "You don't have enough of this resource.");
                    return null;
                }
                if (partner.getResourceCount(get) < getQty) {
                    showError("Not enough resources", "The trading partner doesn't have enough of the desired resource.");
                    return null;
                }
                // Get confirmation from partner
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Trade Offer");
                confirm.setHeaderText(partner.getName() + ", do you want to accept this trade?");
                confirm.setContentText(
                    currentPlayer.getName() + " offers: " + giveQty + "x " + give.getDisplayName() +
                    " for " + getQty + "x " + get.getDisplayName()
                );
                java.util.Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Execute trade (multiple transfers)
                    for (int i = 0; i < giveQty; i++) {
                        gameState.tradeResources(currentPlayer, partner, give, get);
                    }
                    for (int i = 1; i < getQty; i++) {
                        gameState.tradeResources(currentPlayer, partner, give, get);
                    }
                    showInfo("Trade successful", "The trade has been executed.");
                    updateDisplay();
                    notifyAction();
                } else {
                    showInfo("Trade declined", partner.getName() + " has declined the trade.");
                }
            }
            return null;
        });
        dialog.showAndWait();
    }
} 