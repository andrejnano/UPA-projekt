import controllers.canvasShapes.Point;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

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
        BorderPane root =  FXMLLoader.load(getClass().getResource("views/mainView.fxml"));

        // assign it to the scene
        Scene scene = new Scene(root);
        primaryStage.setTitle("UPA 2019 - Estate manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}