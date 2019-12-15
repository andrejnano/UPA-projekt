package controllers;

import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import model.DatabaseManager;

import java.net.URL;
import java.util.ResourceBundle;

/*
|--------------------------------------------------------------------------
| Main Controller
|  - hooks all underlying components to View
|--------------------------------------------------------------------------
*/
public class MainController implements Initializable {
    @FXML
    public AnchorPane canvasView;
    @FXML
    public Pane offersView;
    @FXML
    public Tab createTab;
    @FXML
    public Tab searchTab;
    @FXML
    public Tab manageMapTab;
    @FXML
    public Tab settingsTab;
    @FXML
    Label databaseConnectionStatus;
    @FXML
    Region positiveConnectionIcon;
    @FXML
    Region negativeConnectionIcon;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        databaseConnectionStatus.setText("[DB CONNECTION STATUS]");
        databaseConnectionStatus.textProperty().bind(DatabaseManager.getInstance().connectionStatusString);
        positiveConnectionIcon.visibleProperty().bind(DatabaseManager.getInstance().isConnectedStatus);
        negativeConnectionIcon.visibleProperty().bind(DatabaseManager.getInstance().isConnectedStatus.not());
        positiveConnectionIcon.managedProperty().bind(DatabaseManager.getInstance().isConnectedStatus);
        negativeConnectionIcon.managedProperty().bind(DatabaseManager.getInstance().isConnectedStatus.not());
    }

    @FXML
    private void dbUpdate(Event event) {
        System.out.println("getting shapes from DB");
        if (CanvasController.getInstance() != null) {
            CanvasController.getInstance().loadShapesFromDb();
        }
    }
}
