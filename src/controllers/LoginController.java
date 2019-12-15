package controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.DatabaseManager;

import java.io.File;
import java.sql.SQLException;

public class LoginController {

    String initScript = "";
    DatabaseManager dbm = null;
    Boolean connected = false;

    @FXML
    TextField ipField;
    @FXML
    TextField portField;
    @FXML
    TextField tagField;
    @FXML
    TextField userField;
    @FXML
    PasswordField pwdField;
    @FXML
    TextField pathField;
    @FXML
    Button connectBtn;
    @FXML
    Button insertBtn;
    @FXML
    Button selectBtn;

    @FXML
    public void openChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select init script");
        File temp = chooser.showOpenDialog(new Stage());
        this.initScript = temp.getAbsolutePath();
        pathField.setText(initScript);
    }

    @FXML
    public void connectToDb() {
        if (!connected) {
            DatabaseManager dbm = DatabaseManager.getInstance();
            this.dbm = dbm;
            try {
                dbm.setup(ipField.getText(), portField.getText(), tagField.getText(), userField.getText(), pwdField.getText());
            } catch (SQLException e) {
                // todo: inform on gui
                System.err.println("SQLException: " + e.getMessage());
            }
            dbm.connect();
            connectBtn.setText("Disconnect");
            connected = true;
        }
        else {
            dbm.disconnect();
            connectBtn.setText("Connect");
            connected = false;
        }
    }

    public void commitFile() {
        dbm.loadDbFromFile(initScript);
    }
}
