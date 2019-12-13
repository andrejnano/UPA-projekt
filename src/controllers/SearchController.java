package controllers;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
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
            singleResult.setStyle("-fx-background-color: #6F7480; -fx-border-color: #DEE8FF; -fx-border-width: 2px; -fx-border-radius: 3px;");

            // name
            Label name = new Label("Name: " + offer.getName());
//            name.setStyle("-fx-text-fill: green");
            singleResult.getChildren().add(name);

            // id
            Label id = new Label("ID: " + Integer.toString(offer.getId()));
//            id.setStyle("-fx-text-fill: green");
            singleResult.getChildren().add(new Label("ID: " + Integer.toString(offer.getId())));


            // price
            Label price = new Label("Price: " + Integer.toString(offer.getPrice()));
//            price.setStyle("-fx-text-fill: green");
            singleResult.getChildren().add(price);

            // description
            Label description = new Label("Description: " + offer.getDescription());
//            description.setStyle("-fx-text-fill: green");
            description.setWrapText(true);
            description.setPrefWidth(300);
            description.setMinWidth(300);
            singleResult.getChildren().add(description);

            // type
            Label type = new Label("Type: " + offer.getType());
//            type.setStyle("-fx-text-fill: green");
            singleResult.getChildren().add(type);

            // transaction
            Label transaction = new Label("Transaction: " + offer.getTransaction());
//            transaction.setStyle("-fx-text-fill: green");
            singleResult.getChildren().add(transaction);

            // add the whole result to HBOX of results
            results.getChildren().add(singleResult);
        }
    }

    public void updateCanvas(List<Offer> offers) {

        // for each offer...
//        pane.getChildren().add();
    }
}
