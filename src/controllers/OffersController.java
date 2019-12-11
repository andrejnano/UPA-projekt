package controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import model.Offer;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OffersController implements Initializable{
    @FXML
    ScrollPane scrollPane;
    @FXML
    Pane pane;
    @FXML
    ComboBox propertyType;
    @FXML
    ComboBox transactionType;
    @FXML
    TextField areaField;
    @FXML
    TextArea descriptionArea;
    @FXML
    TextField priceField;
    @FXML
    Pane editOfferSidebar;
    @FXML
    Pane myOffersSidebar;
    @FXML
    VBox myOffersBox;

    ArrayList<Offer> myOffers;
    Offer curOffer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        curOffer = null;
        myOffers = new ArrayList<Offer>();
    }


    private void intFieldRegex(TextField field) {
        field.textProperty().addListener((observable, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
//                field.setStyle("-fx-border-color: default;");
                field.setText(newVal.replaceAll("[^\\d]", ""));
            }
//            else {
//                field.setStyle("-fx-border-color: red;");
//            }
        });
    }

    public void bind(Offer offer) {
        offer.propertyType.bindBidirectional(this.propertyType.valueProperty());
        offer.transactionType.bindBidirectional(this.transactionType.valueProperty());
        offer.area.bindBidirectional(areaField.textProperty());
        offer.description.bindBidirectional(descriptionArea.textProperty());
        offer.price.bindBidirectional(priceField.textProperty());
    }


    public void unBind(Offer offer) {
        offer.propertyType.unbindBidirectional(this.propertyType.valueProperty());
        offer.transactionType.unbindBidirectional(this.transactionType.valueProperty());
        offer.area.unbindBidirectional(areaField.textProperty());
        offer.description.unbindBidirectional(descriptionArea.textProperty());
        offer.price.unbindBidirectional(priceField.textProperty());
    }
    @FXML
    private void setLocation() {

    }

    @FXML
    private void createOffer() {
        myOffersSidebar.setVisible(false);
        editOfferSidebar.setVisible(true);
        if (curOffer != null) {
            unBind(curOffer);
        }
        curOffer = new Offer();
        bind(curOffer);
        intFieldRegex(areaField);
        intFieldRegex(priceField);
        myOffers.add(curOffer);
    }

    @FXML
    private void myOffers() {
        editOfferSidebar.setVisible(false);
        myOffersSidebar.setVisible(true);

//        TODO
//        try {
//            AnchorPane offerListItem =  FXMLLoader.load(getClass().getResource("../views/offerListItem.fxml"));
//            myOffersBox.getChildren().add(offerListItem);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @FXML
    private void loadTitlePic() {
        String destination = chooseFile();
    }

    @FXML
    private void loadOtherPic() {
        String destination = chooseFile();
    }

    private String chooseFile() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open image file");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            return fileChooser.showOpenDialog(new Stage()).getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    @FXML
    private void saveOffer() {

    }

    @FXML
    private void deleteOffer() {

    }

}
