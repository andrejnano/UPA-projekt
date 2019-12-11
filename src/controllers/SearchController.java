package controllers;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import model.Offer;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SearchController implements Initializable {

    @FXML
    ScrollPane scrollPane;

    @FXML
    Pane pane;

    @FXML
    TextField nameField;

    @FXML
    ComboBox propertyType;

    @FXML
    ComboBox transactionType;

    Offer queryOffer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void searchSubmit() {
        System.out.println("Search called");
        // 1. collect data from form
        // 2. dispatch query
        // 3. display results
    }

    public void updateCanvas(List<Offer> offers) {

        // for each offer...

//        pane.getChildren().add();
    }
}
