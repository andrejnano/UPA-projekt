package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Offer;
import model.OffersDBO;

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
    @FXML
    ImageView picture;

    public void init(OffersDBO offer, Image image) {
        transactionType.setText(offer.getTransaction());
        propertyType.setText(" " + offer.getType().toLowerCase());
        area.setText(offer.getName());
        price.setText(((Integer)offer.getPrice()).toString());
        description.setText(offer.getDescription());
        picture.setImage(image);
    }
}
