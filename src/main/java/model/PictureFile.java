package model;

import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PictureFile {
    public String path;
    public ImageView image;
    public int id;

    public PictureFile() {
        path = null;
        image = null;
        id = -1;
    }

    static public String chooseFile() {
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
}
