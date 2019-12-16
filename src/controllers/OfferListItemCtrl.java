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
    Label centreDistance;
    @FXML
    Label shopDistance;
    @FXML
    Label name;
    @FXML
    ImageView picture;

    OffersDBO offerDBO;

    public void init(OffersDBO offer, Image image) {
        this.offerDBO = offer;
        name.setText(offer.getName());
        transactionType.setText(offer.getTransaction());
        propertyType.setText(" " + offer.getType().toLowerCase());
        area.setText(Integer.toString(SpatialHandler.getInstance().selectObjectArea(offer.getSpatialId())));
        price.setText(((Integer)offer.getPrice()).toString());
        centreDistance.setText(Integer.toString(SpatialHandler.getInstance().selectObjectDistance(offer.getSpatialId(), "City centre")));
        shopDistance.setText(Integer.toString(SpatialHandler.getInstance().selectObjectDistance(offer.getSpatialId(), "Shopping area")));
        description.setText(offer.getDescription());
        description.setWrapText(true);
        if (image != null)
            picture.setImage(image);
    }
}
