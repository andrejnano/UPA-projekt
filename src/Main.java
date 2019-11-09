import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class Main extends Application {
    private String title = "UPA 2019";

    private Stage window;
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

        Label label = new Label("Simple project demo");

        VBox layout = new VBox(20);
        layout.getChildren().addAll(label);

        scene = new Scene(layout, 600, 300);

        window.setScene(scene);
        window.setTitle(title);
        window.show();
    }

}