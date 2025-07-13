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
        this.diceLabel = new Label("Würfel: -");
        this.currentPlayerLabel = new Label();
        
        setupPanel();
        updateDisplay();
    }

    private void setupPanel() {
        setPadding(new Insets(10));
        setSpacing(10);
        setStyle("-fx-background-color: #e8e8e8; -fx-border-color: #ccc;");

        // Title
        Label titleLabel = new Label("Spielsteuerung");
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

        // Anleitung unten einfügen
        Label anleitung = new Label(
            "Anleitung:\n" +
            "Um etwas zu bauen, klicke auf:\n" +
            "- eine Kreuzung (Kreis) für Siedlung/Stadt\n" +
            "- eine Straße (dicke Linie) für Straße\n" +
            "Ressourcen werden automatisch geprüft."
        );
        anleitung.setFont(Font.font("Arial", 12));
        anleitung.setWrapText(true);
        anleitung.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #ccc; -fx-padding: 8; margin-top: 10px;");
        getChildren().add(anleitung);
    }

    private VBox createDiceSection() {
        VBox section = new VBox(5);
        section.setPadding(new Insets(5));
        section.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");

        Label title = new Label("Würfel");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        section.getChildren().add(title);

        diceLabel.setFont(Font.font("Arial", 14));
        section.getChildren().add(diceLabel);

        // Neuer Würfel-Button - immer verfügbar
        Button newRollButton = new Button("🎲 WÜRFELN 🎲");
        newRollButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        newRollButton.setOnAction(e -> {
            if (gameState.getCurrentPhase() == GameState.GamePhase.PLAY) {
                gameState.rollDice();
                updateDisplay();
                notifyAction();
                showInfo("Würfel geworfen!", "Würfel: " + gameState.getDiceRoll());
            } else {
                showError("Nicht verfügbar", "Würfeln ist nur in der Spielphase möglich!");
            }
        });
        section.getChildren().add(newRollButton);

        return section;
    }

    private VBox createBuildingSection() {
        VBox section = new VBox(5);
        section.setPadding(new Insets(5));
        section.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");

        Label title = new Label("Gebäude bauen");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        section.getChildren().add(title);

        HBox buildingButtons = new HBox(5);
        
        Button settlementButton = new Button("Siedlung");
        settlementButton.setOnAction(e -> buildSettlement());
        buildingButtons.getChildren().add(settlementButton);

        Button cityButton = new Button("Stadt");
        cityButton.setOnAction(e -> buildCity());
        buildingButtons.getChildren().add(cityButton);

        Button roadButton = new Button("Straße");
        roadButton.setOnAction(e -> buildRoad());
        buildingButtons.getChildren().add(roadButton);

        section.getChildren().add(buildingButtons);
        return section;
    }

    private VBox createTurnSection() {
        VBox section = new VBox(5);
        section.setPadding(new Insets(5));
        section.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");

        Label title = new Label("Zugverwaltung");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        section.getChildren().add(title);

        // Entfernt: Button nextTurnButton = new Button("Nächster Zug");
        // section.getChildren().add(nextTurnButton);

        // Handels-Button
        Button tradeButton = new Button("Handel");
        tradeButton.setOnAction(e -> openTradeDialog());
        section.getChildren().add(tradeButton);

        return section;
    }

    private VBox createSetupSection() {
        VBox section = new VBox(5);
        section.setPadding(new Insets(5));
        section.setStyle("-fx-background-color: #fff3cd; -fx-border-color: #ffeaa7;");

        Label title = new Label("Aufbauphase");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        section.getChildren().add(title);

        String setupInfo = switch (gameState.getSetupPhase()) {
            case FORWARD -> "Vorwärtsrunde: Spieler 1 → 2 → 3 → 4";
            case BACKWARD -> "Rückwärtsrunde: Spieler 4 → 3 → 2 → 1";
        };
        
        Label info = new Label(setupInfo);
        info.setFont(Font.font("Arial", 10));
        info.setWrapText(true);
        section.getChildren().add(info);

        Label instructions = new Label("Jeder Spieler baut: 1. Siedlung, 2. Straße → automatischer Wechsel");
        instructions.setFont(Font.font("Arial", 9));
        instructions.setWrapText(true);
        section.getChildren().add(instructions);

        // Show current player's status
        Player currentPlayer = gameState.getCurrentPlayer();
        String status = "";
        if (!gameState.isSettlementBuilt()) {
            status = currentPlayer.getName() + " muss eine Siedlung bauen";
        } else if (!gameState.isRoadBuilt()) {
            status = currentPlayer.getName() + " muss eine Straße bauen";
        } else {
            status = currentPlayer.getName() + " ist fertig";
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

        Label title = new Label("Spielphase");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        section.getChildren().add(title);

        Label info = new Label("Würfeln Sie, um Ressourcen zu erhalten!");
        info.setFont(Font.font("Arial", 10));
        info.setWrapText(true);
        section.getChildren().add(info);

        return section;
    }



    private void buildSettlement() {
        Player currentPlayer = gameState.getCurrentPlayer();
        if (gameState.buildSettlement(currentPlayer)) {
            showInfo("Siedlung gebaut!", "Du hast erfolgreich eine Siedlung gebaut.");
            updateDisplay();
            notifyAction();
        } else {
            String cost = BuildingCosts.getCostString(BuildingCosts.BuildingType.SETTLEMENT);
            showError("Fehler", "Du kannst keine Siedlung bauen.\nKosten: " + cost + "\nÜberprüfe deine Ressourcen.");
        }
    }

    private void buildCity() {
        Player currentPlayer = gameState.getCurrentPlayer();
        if (gameState.buildCity(currentPlayer)) {
            showInfo("Stadt gebaut!", "Du hast erfolgreich eine Stadt gebaut.");
            updateDisplay();
            notifyAction();
        } else {
            String cost = BuildingCosts.getCostString(BuildingCosts.BuildingType.CITY);
            showError("Fehler", "Du kannst keine Stadt bauen.\nKosten: " + cost + "\nÜberprüfe deine Ressourcen.");
        }
    }

    private void buildRoad() {
        Player currentPlayer = gameState.getCurrentPlayer();
        if (gameState.buildRoad(currentPlayer)) {
            showInfo("Straße gebaut!", "Du hast erfolgreich eine Straße gebaut.");
            updateDisplay();
            notifyAction();
        } else {
            String cost = BuildingCosts.getCostString(BuildingCosts.BuildingType.ROAD);
            showError("Fehler", "Du kannst keine Straße bauen.\nKosten: " + cost + "\nÜberprüfe deine Ressourcen.");
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
        diceLabel.setText("Würfel: " + (diceRoll > 0 ? String.valueOf(diceRoll) : "-"));

        // Update current player label
        Player currentPlayer = gameState.getCurrentPlayer();
        currentPlayerLabel.setText("Aktueller Spieler: " + currentPlayer.getName() + 
                                 " (Siegpunkte: " + currentPlayer.getVictoryPoints() + ")");
        
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
            showError("Handel nicht möglich", "Es gibt keine anderen Spieler.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Handel anbieten");
        dialog.setHeaderText("Wähle einen Mitspieler und Ressourcen für den Handel aus.");

        // Auswahlfelder
        ComboBox<Player> playerBox = new ComboBox<>();
        playerBox.getItems().addAll(otherPlayers);
        playerBox.getSelectionModel().selectFirst();

        ComboBox<ResourceType> giveBox = new ComboBox<>();
        giveBox.getItems().addAll(ResourceType.values());
        giveBox.getSelectionModel().selectFirst();

        ComboBox<ResourceType> getBox = new ComboBox<>();
        getBox.getItems().addAll(ResourceType.values());
        getBox.getSelectionModel().selectFirst();

        // Mengen-Spinners
        Spinner<Integer> giveAmount = new Spinner<>(1, 99, 1);
        Spinner<Integer> getAmount = new Spinner<>(1, 99, 1);
        giveAmount.setEditable(true);
        getAmount.setEditable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Mitspieler:"), 0, 0);
        grid.add(playerBox, 1, 0);
        grid.add(new Label("Ich gebe:"), 0, 1);
        grid.add(giveBox, 1, 1);
        grid.add(giveAmount, 2, 1);
        grid.add(new Label("Ich bekomme:"), 0, 2);
        grid.add(getBox, 1, 2);
        grid.add(getAmount, 2, 2);

        dialog.getDialogPane().setContent(grid);
        ButtonType offerButtonType = new ButtonType("Handel anbieten", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(offerButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == offerButtonType) {
                Player partner = playerBox.getValue();
                ResourceType give = giveBox.getValue();
                ResourceType get = getBox.getValue();
                int giveQty = giveAmount.getValue();
                int getQty = getAmount.getValue();
                if (partner == null || give == null || get == null) {
                    showError("Ungültige Auswahl", "Bitte wähle alles aus.");
                    return null;
                }
                if (currentPlayer.getResourceCount(give) < giveQty) {
                    showError("Nicht genug Ressourcen", "Du hast nicht genug von dieser Ressource.");
                    return null;
                }
                if (partner.getResourceCount(get) < getQty) {
                    showError("Nicht genug Ressourcen", "Der Handelspartner hat nicht genug von der gewünschten Ressource.");
                    return null;
                }
                // Bestätigung beim Partner einholen
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Handelsangebot");
                confirm.setHeaderText(partner.getName() + ", möchtest du diesen Handel annehmen?");
                confirm.setContentText(
                    currentPlayer.getName() + " bietet: " + giveQty + "x " + give.getGermanName() +
                    " gegen " + getQty + "x " + get.getGermanName()
                );
                java.util.Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Handel durchführen (mehrfache Übergabe)
                    for (int i = 0; i < giveQty; i++) {
                        gameState.tradeResources(currentPlayer, partner, give, get);
                    }
                    for (int i = 1; i < getQty; i++) {
                        gameState.tradeResources(currentPlayer, partner, give, get);
                    }
                    showInfo("Handel erfolgreich", "Der Handel wurde durchgeführt.");
                    updateDisplay();
                    notifyAction();
                } else {
                    showInfo("Handel abgelehnt", partner.getName() + " hat den Handel abgelehnt.");
                }
            }
            return null;
        });
        dialog.showAndWait();
    }
} 