package controllers;

import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.Offer;
import model.OffersDBO;
import model.OffersHandler;

import javax.swing.text.DefaultEditorKit;
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
    TextField streetField;

    @FXML
    ComboBox propertyType;

    @FXML
    ComboBox transactionType;

    @FXML
    CheckBox closeToCenter;

    @FXML
    CheckBox closeToLake;

    @FXML
    HBox results;

    Offer queryOffer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        propertyType.getSelectionModel().selectFirst();
        transactionType.getSelectionModel().selectFirst();

        results.setPadding(new Insets(5, 5, 5,5));
        results.setSpacing(10.0);
    }

    @FXML
    public void searchSubmit() {
        results.getChildren().removeAll();
        System.out.println("Search called");
        // 1. collect data from form
        String nameString = nameField.getText();
        String propertyTypeString = propertyType.getSelectionModel().getSelectedItem().toString();
        String transactionTypeString = transactionType.getSelectionModel().getSelectedItem().toString();
        String streetString = streetField.getText();
        Boolean isCloseToCenter = closeToCenter.isSelected();
        Boolean isCloseToLake = closeToLake.isSelected();

        System.out.println("name: " + nameString);
        System.out.println("property type: " + propertyTypeString);
        System.out.println("transaction type: " + transactionTypeString);
        System.out.println("street: " + streetString);
        System.out.println("close to center: " + isCloseToCenter.toString());
        System.out.println("close to lake: " + isCloseToLake.toString());

        // 2. dispatch query
        List<Integer> offersIds = OffersHandler.getInstance().getOffers(propertyTypeString, nameString);
        System.out.println("Got these offers ids: " + offersIds.toString());
        List<OffersDBO> offersDBOs = OffersHandler.getInstance().loadOffers(offersIds);

        // 3. display results
        for (OffersDBO offer: offersDBOs) {

            // configure a single result
            VBox singleResult = new VBox();
            singleResult.setPadding(new Insets(5, 10, 5, 10));
            singleResult.getStyleClass().add("resultBox");

            // name
            Label name = new Label("Name: " + offer.getName());
            singleResult.getChildren().add(name);

            // id
            Label id = new Label("ID: " + Integer.toString(offer.getId()));
            singleResult.getChildren().add(new Label("ID: " + Integer.toString(offer.getId())));

            // price
            Label price = new Label("Price: " + Integer.toString(offer.getPrice()));
            singleResult.getChildren().add(price);

            // description
            Label description = new Label("Description: " + offer.getDescription());
            description.setWrapText(true);
            description.setPrefWidth(300);
            description.setMinWidth(300);
            singleResult.getChildren().add(description);

            // type
            Label type = new Label("Type: " + offer.getType());
            singleResult.getChildren().add(type);

            // transaction
            Label transaction = new Label("Transaction: " + offer.getTransaction());
            singleResult.getChildren().add(transaction);

            // Add selection click  handler
            singleResult.setOnMousePressed(mouseEvent -> {
                System.out.println("mouse clicked on result " + id);
                // remove "selectedResult" style from all results
                for (Node result: results.getChildren()
                     ) {
                    result.getStyleClass().remove("selectedResult");
                }
                // add "selectedResult" for this one
                singleResult.getStyleClass().add("selectedResult");
            });

            // add the whole result to HBOX of results
            results.getChildren().add(singleResult);
        }
    }

    public void updateCanvas(List<Offer> offers) {

        // for each offer...
//        pane.getChildren().add();
    }
}
