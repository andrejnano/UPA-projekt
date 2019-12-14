package controllers;

import controllers.canvasShapes.Coordinate;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
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
    Label scaleAmountLabel;
    @FXML
    Label mouseCoordinateLabel;
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

    // Mouse cursor position
    private Coordinate mouseCoordinate;

    // Canvas zoom/scaling amount (multiplier)
    final private double canvasScaleDelta = 1.1;
    final private int maxZoomLevel = 20;
    final private int minZoomLevel = 0;
    private int currentZoomLevel = 10;

    // number of rows and columns in the canvas grid
    public final static double gridRows = 50;
    public final static double gridCols = 50;
    public static double gridCellSize;
    Canvas gridCanvas;

    // mouse coordinates tooltip next to cursor
    Tooltip cursorLocationToolTip;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        curOffer = null;
        listItems = new ArrayList<Pane>();
        otherPictureFiles= new ArrayList<>();
        titlePictureFile = new PictureFile();
        titlePictureFile.image = titlePicture;
        bound = false;
        updateOffer = false;

        propertyType.getSelectionModel().selectFirst();
        transactionType.getSelectionModel().selectFirst();

        // calculate grid size and paint on canvas pane
        gridCellSize = pane.getPrefHeight() / gridRows;
        drawGridOnCanvas();


        // cursor position tooltip box with coordinate [x,y], displayed on MOUSE PRESSED
        cursorLocationToolTip = new Tooltip();

        // mouse cursor coordinate model [x,y]
        mouseCoordinate = new Coordinate();

        // assign mouse event handlers on canvas
        pane.setOnMousePressed(canvasMouseHandler);
        pane.setOnMouseReleased(canvasMouseHandler);
        pane.setOnMouseMoved(canvasMouseHandler);

        // For scrolling while holding CTRL => perform Zoom
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if(event.isControlDown())
            {
                handleScrollEventAsZoom(event);
                event.consume();
            }
        });

        // Multi-touch zoom
        scrollPane.setOnZoom(zoomEvent -> {
            handleZoom(zoomEvent);
            zoomEvent.consume();
        });
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
        bound = true;
        if (curOffer.id == -1)
            return;

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



    // pane or "canvas" mouse event handler
    EventHandler<MouseEvent> canvasMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {

            // Handle mouse-click event on pane
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {

                scrollPane.setCursor(Cursor.CLOSED_HAND);
                // display tooltip next to cursor
                cursorLocationToolTip.setText("[x: "+Math.round(mouseEvent.getX())+", y: "+Math.round(mouseEvent.getY())+"]");
                Node node = (Node) mouseEvent.getSource();
                cursorLocationToolTip.show(node, mouseEvent.getScreenX() + 10, mouseEvent.getScreenY() + 20);
                return;
            }

            if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                cursorLocationToolTip.hide();
                scrollPane.setCursor(Cursor.OPEN_HAND);
            }

            // update mouse position model [x,y] coordinates
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
                mouseCoordinate.setX(mouseEvent.getX());
                mouseCoordinate.setY(mouseEvent.getY());
                mouseCoordinateLabel.setText("Mouse[X: " + Math.round(mouseCoordinate.getX()) + "; Y: " +  Math.round(mouseCoordinate.getY()) + "]");
            }
        }
    };

    // Handling multi-touch zoom - Two-finger pinching motion
    public void handleZoom(ZoomEvent zoomEvent) {
        double scaleAmount = zoomEvent.getZoomFactor();
        // Construct and configure scale transformation
        Scale scaleTransform = new Scale();
        scaleTransform.setPivotX(mouseCoordinate.getX());
        scaleTransform.setPivotY(mouseCoordinate.getY());
        scaleTransform.setX(pane.getScaleX() * scaleAmount);
        scaleTransform.setY(pane.getScaleY() * scaleAmount);

        // Apply the transformation
        pane.getTransforms().add(scaleTransform);
        scaleAmountLabel.setText(""+zoomEvent.getTotalZoomFactor());

        zoomEvent.consume();
    }

    // Handling scroll + CTRL key event as canvas pane zoom action
    public void handleScrollEventAsZoom(ScrollEvent scrollEvent) {

        double scaleAmount;

        if (scrollEvent.getDeltaY() == 0) {
            return;
        }

        // check scrolling direction
        if (scrollEvent.getDeltaY() > 0 ) {
            // Upper limit for zooming [200%]
            if (currentZoomLevel >= maxZoomLevel) {
                return;
            }
            currentZoomLevel++;
            scaleAmount = canvasScaleDelta;
        } else {
            // Lower limit for zooming [0%]
            if (currentZoomLevel <= minZoomLevel) {
                return;
            }
            currentZoomLevel--;
            scaleAmount = 1 / canvasScaleDelta;
        }

        // Construct and configure scale transformation
        Scale scaleTransform = new Scale();
        scaleTransform.setPivotX(mouseCoordinate.getX());
        scaleTransform.setPivotY(mouseCoordinate.getY());
        scaleTransform.setX(pane.getScaleX() * scaleAmount);
        scaleTransform.setY(pane.getScaleY() * scaleAmount);

        // Apply the transformation
        pane.getTransforms().add(scaleTransform);

        // Update label text with the current zoom level in percentage [0-200%]
        scaleAmountLabel.setText(currentZoomLevel*10 + "%");

        scrollEvent.consume();
    }

    // Draws base grid on canvas
    public void drawGridOnCanvas() {
        List<Line> horizontalLines = new ArrayList<>();
        List<Line> verticalLines = new ArrayList<>();
        for(int i=0; i < gridRows; i++) {
            horizontalLines.add(new Line(0, i*gridCellSize ,pane.getPrefWidth(), i*gridCellSize));
            horizontalLines.get(i).setStroke(Color.LIGHTGRAY);
            horizontalLines.get(i).setStrokeWidth(1.0);
        }
        for(int i=0; i < gridCols; i++) {
            verticalLines.add(new Line(i*gridCellSize, 0 ,i*gridCellSize, pane.getPrefHeight()));
            verticalLines.get(i).setStroke(Color.LIGHTGRAY);
            verticalLines.get(i).setStrokeWidth(1.0);
        }
        pane.getChildren().addAll(horizontalLines);
        pane.getChildren().addAll(verticalLines);
    }
}


