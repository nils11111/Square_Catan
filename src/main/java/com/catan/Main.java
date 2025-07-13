package com.catan;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.catan.ui.GameWindow;

/**
 * Main class for the Square Catan game application.
 * Launches the JavaFX application with the game window.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameWindow gameWindow = new GameWindow();
        Scene scene = new Scene(gameWindow, 1200, 800);
        
        primaryStage.setTitle("Square Catan - The Settlers of Catan");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 