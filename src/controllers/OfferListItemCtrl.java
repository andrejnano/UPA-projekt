package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

    private OffersController offersController;
    OffersDBO offerDBO;

    public void init(OffersDBO offer, Image image, OffersController offersController) {
        this.offerDBO = offer;
        this.offersController = offersController;
        transactionType.setText(offer.getTransaction());
        propertyType.setText(" " + offer.getType().toLowerCase());
        area.setText(offer.getName());
        price.setText(((Integer)offer.getPrice()).toString());
        description.setText(offer.getDescription());
        if (image != null)
            picture.setImage(image);
    }

    @FXML
    private void offerSelected(MouseEvent mouseEvent) {
        offersController.unBind();
        offersController.clear();
        offersController.myOffersSidebar.setVisible(false);
        offersController.editOfferSidebar.setVisible(true);
        offersController.bind(new Offer(offerDBO));
    }
}
