package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    public Pane editOfferSidebar;
    @FXML
    public Pane myOffersSidebar;
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
    PictureFile titlePictureFile;
    ArrayList<PictureFile> otherPictureFiles;
    MultimediaHandler multiHandler;
    boolean bound;
    boolean updateOffer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        curOffer = null;
        listItems = new ArrayList<Pane>();
        otherPictureFiles= new ArrayList<>();
        titlePictureFile = new PictureFile();
        titlePictureFile.image = titlePicture;
        bound = false;
        updateOffer = false;
    }

    private void intFieldRegex(TextField field) {
        field.textProperty().addListener((observable, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                field.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void bind(Offer offer) {
        curOffer = offer;
        otherPictureFiles.clear();
        titlePictureFile = new PictureFile();
        titlePictureFile.image = titlePicture;
        updateOffer = true;

        this.propertyType.valueProperty().bindBidirectional(offer.propertyType);
        this.transactionType.valueProperty().bindBidirectional(offer.transactionType);
        areaField.textProperty().bindBidirectional(offer.area);
        descriptionArea.textProperty().bindBidirectional(offer.description);
        priceField.textProperty().bindBidirectional(offer.price);


        multiHandler = MultimediaHandler.getInstance();
        List<Integer> ids =  multiHandler.getImageId(curOffer.id);
        for (int i = 0; i < ids.size(); i++) {
            if (i == 0) {
                titlePicture.setImage(multiHandler.getPicture(ids.get(i)));
                titlePictureFile.id = ids.get(i);
            } else {
                ImageView view = emptyImageView();
                view.setImage(multiHandler.getPicture(ids.get(i)));
                final PictureFile pictureFile = new PictureFile();
                pictureFile.id = ids.get(i);
                pictureFile.image = view;
                view.setOnMouseClicked(e -> {
                    pictureEdit(pictureFile);
                });
                otherPictureFiles.add(pictureFile);
                otherPictures.getChildren().add(view);
            }
        }
        bound = true;
    }

    public void unBind() {
        if (bound) {
            this.propertyType.valueProperty().bindBidirectional(curOffer.propertyType);
            this.transactionType.valueProperty().bindBidirectional(curOffer.transactionType);
            areaField.textProperty().bindBidirectional(curOffer.area);
            descriptionArea.textProperty().bindBidirectional(curOffer.description);
            priceField.textProperty().bindBidirectional(curOffer.price);
        }
        bound = false;
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
            unBind();
        }
        clear();
        curOffer = new Offer();
        bind(curOffer);
        intFieldRegex(areaField);
        intFieldRegex(priceField);
    }

    public void clear() {
        propertyType.getSelectionModel().clearSelection();
        transactionType.getSelectionModel().clearSelection();
        areaField.clear();
        descriptionArea.clear();
        priceField.clear();
        titlePicture.setImage(null);
        for (PictureFile pf : otherPictureFiles) {
            otherPictures.getChildren().remove(pf.image);
        }
        titlePictureFile = new PictureFile();
        otherPictureFiles.clear();
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

                multiHandler = MultimediaHandler.getInstance();
                int imageId = multiHandler.getFirstImageId(o.getId());
                Image image = (imageId == -1) ? null : multiHandler.getPicture(imageId);
                itemController.init(o, image, this);
                myOffersBox.getChildren().add(offerListItem);
                listItems.add(offerListItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void loadTitlePic() {
        String path = PictureFile.chooseFile();
        try {
            URL url = new File(path).toURI().toURL();
            titlePicture.setImage(new Image(url.toString()));
            titlePictureFile.path = path;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadOtherPic() {
        String path = PictureFile.chooseFile();
        try {
            URL url = new File(path).toURI().toURL();
            ImageView image = emptyImageView();
            image.setImage(new Image(url.toString()));
            otherPictures.getChildren().add(image);
            PictureFile pictureFile = new PictureFile();
            pictureFile.path = path;
            pictureFile.image = image;
            otherPictureFiles.add(pictureFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ImageView emptyImageView() {
        ImageView view = new ImageView();
        view.setPreserveRatio(true);
        view.setFitHeight(150);
        return view;
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
        if (curOffer.id == -1)
            curOffer.id = dbHandler.insertOffer(curOffer.toDBO());
        else
            dbHandler.updateOffer(curOffer.toDBO());
        if (titlePictureFile.path != null)
            multiHandler.storeImage(curOffer.id, titlePictureFile.path);
        for (PictureFile pf : otherPictureFiles) {
            // TODO update
            if (pf.path != null) {
                multiHandler.storeImage(curOffer.id, pf.path);
            }
        }
    }

    @FXML
    private void deleteOffer() {
        if (curOffer != null) {
            clear();
            editOfferSidebar.setVisible(false);
            errorLabel.setText("");
            OffersHandler dbHandler = getDbHandler();
            if (curOffer.id != -1) {
                dbHandler.deleteOffer(curOffer.id);
                if (titlePictureFile.id != -1)
                    multiHandler.deleteEntry(titlePictureFile.id);
                for (PictureFile pf : otherPictureFiles) {
                    if (pf.id != -1)
                        multiHandler.deleteEntry(pf.id);
                }
            }
        }
    }

    private OffersHandler getDbHandler() {
        multiHandler = MultimediaHandler.getInstance();
        return OffersHandler.getInstance();
    }


    @FXML
    private void editTitlePicture() {
        pictureEdit(titlePictureFile);
    }

    private void pictureEdit(PictureFile pictureFile) {
        try {
            Dialog dialog = new Dialog();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/pictureEditView.fxml"));
            DialogPane dialogPane = loader.load();
            PictureEditController pictureController =  loader.getController();

            dialog.setDialogPane(dialogPane);
            dialog.show();
            pictureController.init(pictureFile, curOffer.id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

