package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Offer;

public class OfferListItemCtrl {
    @FXML
    Label transactionType;
    @FXML
    Label propertyType;
    @FXML
    Label area;
    @FXML
    Label price;
    @FXML
    Label description;

    public void init(Offer offer) {
        transactionType.setText(offer.transactionType.getValue().toString());
        propertyType.setText(" " + offer.propertyType.getValue().toString().toLowerCase());
        System.out.println(offer.area.getValue());
        area.setText(offer.area.getValue());
        price.setText(offer.price.getValue());
        description.setText(offer.description.getValue());
    }
}
