package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import main.java.model.*;

import java.util.ArrayList;
import java.util.List;

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
    @FXML
    Button similarButton;

    OffersDBO offerDBO;
    private SearchController searchController;

    public void init(OffersDBO offer, Image image, boolean buttonVisibility) {
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
        similarButton.setVisible(buttonVisibility);
        if (image != null)
            picture.setImage(image);
        else
            similarButton.setVisible(false);
    }

    public void setSearchController(SearchController ctrl) {
        this.searchController = ctrl;
    }

    public void findSimilar(MouseEvent mouseEvent) {
        searchController.clearResults();
        MultimediaHandler multiHandler = MultimediaHandler.getInstance();
        List<Integer> similarImages = multiHandler.getSimilarities(offerDBO.getId());

        ArrayList<OffersDBO> offersOut = new ArrayList<OffersDBO>();
        for (OffersDBO o : OffersHandler.getInstance().getAllOffers()) {
            for (int i : similarImages) {
                if (multiHandler.getFirstImageId(o.getId()) == i)
                    offersOut.add(o);
            }
        }
        searchController.showResults(offersOut);
    }
}
