package controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import model.MultimediaHandler;
import model.Offer;
import model.OffersDBO;
import model.OffersHandler;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/*
|--------------------------------------------------------------------------
| Offers Controller
|--------------------------------------------------------------------------
*/
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
    @FXML
    Label errorLabel;
    @FXML
    ImageView titlePicture;
    @FXML
    HBox otherPictures;

    Offer curOffer;
    ArrayList<Pane> listItems;
    String titlePicturePath;
    ArrayList<String> otherPicturePaths;
    ArrayList<ImageView> otherPicturesImages;
    MultimediaHandler multiHandler;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        curOffer = null;
        listItems = new ArrayList<Pane>();
        otherPicturePaths = new ArrayList<String>();
        otherPicturesImages = new ArrayList<>();
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
        errorLabel.setText("");
        myOffersSidebar.setVisible(false);
        editOfferSidebar.setVisible(true);
        if (curOffer != null) {
            unBind(curOffer);
        }
        clear();
        curOffer = new Offer();
        bind(curOffer);
        intFieldRegex(areaField);
        intFieldRegex(priceField);
    }

    private void clear() {
        propertyType.getSelectionModel().clearSelection();
        transactionType.getSelectionModel().clearSelection();
        areaField.clear();
        descriptionArea.clear();
        priceField.clear();
        titlePicture.setImage(null);
        otherPictures.getChildren().removeAll(otherPicturesImages);
        titlePicturePath = null;
        otherPicturePaths.clear();
    }

    @FXML
    private void myOffers() {
        errorLabel.setText("");
        editOfferSidebar.setVisible(false);
        myOffersSidebar.setVisible(true);

        myOffersBox.getChildren().removeAll(listItems);
        listItems.clear();



        OffersHandler dbHandler = getDbHandler();
        ArrayList<OffersDBO> offers = dbHandler.getAllOffers();

        for (OffersDBO o: offers) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/offerListItem.fxml"));
                AnchorPane offerListItem = loader.load();
                OfferListItemCtrl itemController =  loader.getController();
                itemController.init(o, multiHandler.getPicture(multiHandler.getFirstImageId(o.getId())));
                myOffersBox.getChildren().add(offerListItem);
                listItems.add(offerListItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void loadTitlePic() {
        String path = chooseFile();
        try {
            URL url = new File(path).toURI().toURL();
            titlePicture.setImage(new Image(url.toString()));
            titlePicturePath = path;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadOtherPic() {
        String path = chooseFile();
        try {
            URL url = new File(path).toURI().toURL();
            ImageView image = new ImageView();
            image.setImage(new Image(url.toString()));
            image.setPreserveRatio(true);
            image.setFitHeight(150);
            otherPictures.getChildren().add(image);
            otherPicturePaths.add(path);
            otherPicturesImages.add(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (curOffer != null && curOffer.isValid()) {
            errorLabel.setTextFill(Color.GREEN);
            storeImages();
            errorLabel.setText("Insertion successful!");
        } else {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Error: some fields in form are empty!");
        }
    }

    private void storeImages() {
        OffersHandler dbHandler = getDbHandler();
        curOffer.id = dbHandler.insertOffer(curOffer.toDBO());
        multiHandler.storeImage(curOffer.id, titlePicturePath);
        for (String path : otherPicturePaths) {
            System.out.println("CurPath "+ path);
            multiHandler.storeImage(curOffer.id, path);
        }
    }

    @FXML
    private void deleteOffer() {
        if (curOffer != null) {
            clear();
            editOfferSidebar.setVisible(false);
            errorLabel.setText("");
            OffersHandler dbHandler = getDbHandler();
            if (curOffer.id != -1)
                dbHandler.deleteOffer(curOffer.id);
        }
    }

    private OffersHandler getDbHandler() {
        multiHandler = MultimediaHandler.getInstance();
        return OffersHandler.getInstance();
    }

}
