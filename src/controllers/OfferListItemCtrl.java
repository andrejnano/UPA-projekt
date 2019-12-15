package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Offer;
import model.OffersDBO;
import model.SpatialHandler;

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

    OffersDBO offerDBO;

    public void init(OffersDBO offer, Image image) {
        this.offerDBO = offer;
        transactionType.setText(offer.getTransaction());
        propertyType.setText(" " + offer.getType().toLowerCase());
        area.setText(Integer.toString((SpatialHandler.getInstance().selectObjectArea(offer.getSpatialId())/100)));
        price.setText(((Integer)offer.getPrice()).toString());
        description.setText(offer.getDescription());
        if (image != null)
            picture.setImage(image);
    }
}
