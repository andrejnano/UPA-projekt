package main.java;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import main.java.model.DatabaseManager;

// This is the initial starting point for the whole application
public class App extends Application {

    /**
     * Application entry function that will execute javaFX application start()
     * @param args optional arguments passed in to the application
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Assign MainView FXML layout to window
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        new DatabaseManager();

        AnchorPane root =  FXMLLoader.load(getClass().getResource("views/mainView.fxml"));

        // assign it to the scene
        Scene scene = new Scene(root, 1060, 825);
        scene.getStylesheets().add("css/main.css");
        primaryStage.setTitle("UPA 2019 - Estate manager");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
                    if (DatabaseManager.getInstance().isConnected()) {
                        System.out.println("Closing database connection");
                        DatabaseManager.getInstance().disconnect();
                    }
                }
        );
    }

}