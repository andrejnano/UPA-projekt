import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

// This is the initial starting point for the whole application
public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // load root FXML VIEW
        Parent root = FXMLLoader.load(getClass().getResource("views/mainView.fxml"));

        // assign it to the scene, set window size
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("UPA 2019");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}