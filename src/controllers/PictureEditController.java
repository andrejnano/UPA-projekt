package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Window;
import model.MultimediaHandler;
import model.PictureFile;
import javafx.scene.image.ImageView;
import java.io.File;
import java.net.URL;

public class PictureEditController {
    @FXML
    DialogPane dialogPane;
    @FXML
    ImageView picture;

    private Window window;
    private MultimediaHandler multiHandler;
    private PictureFile pictureFile;
    private int offerId;

    public void init(PictureFile pictureFile, int offerId) {
        this.offerId = offerId;
        multiHandler = MultimediaHandler.getInstance();
        window = dialogPane.getScene().getWindow();
        window.setOnCloseRequest(event -> window.hide());
        this.pictureFile = pictureFile;
        picture.setImage(pictureFile.image.getImage());
    }

    @FXML
    private void replaceImage(ActionEvent actionEvent) {
        String path = PictureFile.chooseFile();
        try {
            URL url = new File(path).toURI().toURL();
            picture.setImage(new Image(url.toString()));
            pictureFile.image = picture;
            if (pictureFile.path != null) {
                multiHandler.storeImage(offerId, path);
            } else if (pictureFile.id != -1) {
                multiHandler.updateImage(pictureFile.id, path);
            }
            pictureFile.path = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void rotateLeft(ActionEvent actionEvent) {
    }

    @FXML
    private void rotateRight(ActionEvent actionEvent) {
    }

    @FXML
    private void deleteImage(ActionEvent actionEvent) {
        if (pictureFile.id != -1) {
            multiHandler.deleteEntry(pictureFile.id);
        }
        pictureFile.image.setImage(null);
        window.hide();
    }

    @FXML
    private void mirror(ActionEvent actionEvent) {
    }

    @FXML
    private void flip(ActionEvent actionEvent) {
    }

    @FXML
    private void save(ActionEvent actionEvent) {
    }
}
