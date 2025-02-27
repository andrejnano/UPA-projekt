package main.java.controllers;

import main.java.controllers.canvasShapes.Coordinate;
import main.java.controllers.canvasShapes.PolyLine;
import main.java.controllers.canvasShapes.Shape;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
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
import main.java.model.*;

import javax.tools.Tool;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.IntFunction;

public class SearchController implements Initializable {
    @FXML
    ScrollPane scrollPane;
    @FXML
    Pane pane;
    @FXML
    TextField nameField;
    @FXML
    TextField streetField;
    @FXML
    Label scaleAmountLabel;
    @FXML
    ComboBox propertyType;
    @FXML
    ComboBox transactionType;
    @FXML
    ChoiceBox distance;
    @FXML
    ComboBox distanceToObjectType;
    @FXML
    HBox results;
    @FXML
    Button refreshButton;
    @FXML
    Slider maxPriceSlider;
    @FXML
    Label maxPriceValueLabel;
    @FXML
    CheckBox checkWater;
    @FXML
    CheckBox checkTrees;

    Offer queryOffer;

    ArrayList<Shape> canvasShapes;
    List<Integer> searchResultsSpatialIds;

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

    Tooltip offerNameTooltip;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Collection of shapes in the canvas
        canvasShapes = new ArrayList<>();

        // select the default property, by selecting the first one
        propertyType.getSelectionModel().selectFirst();
        transactionType.getSelectionModel().selectFirst();
        distance.getSelectionModel().selectFirst();
        distanceToObjectType.getSelectionModel().selectFirst();

        // bind slider to label showing the currently selected max price
        maxPriceValueLabel.textProperty().bind(Bindings.format("%.2f CZK", maxPriceSlider.valueProperty()));

        // calculate grid size and paint on canvas pane
        gridCellSize = pane.getPrefHeight() / gridRows;
        drawGridOnCanvas();

        // tooltip that will be displayed next to offers in canvas giving further info
        offerNameTooltip = new Tooltip();

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


    // clear the whole search form
    public void clearForm() {
        nameField.clear();
        propertyType.getSelectionModel().selectFirst();
        transactionType.getSelectionModel().selectFirst();
        streetField.clear();
        checkTrees.setSelected(false);
        checkWater.setSelected(false);
        maxPriceSlider.setValue(5000);
        distance.getSelectionModel().selectFirst();
        distanceToObjectType.getSelectionModel().selectFirst();
    }


    // filters offers (OffersDBO) that match given price interval (min - max)
    public List<OffersDBO> filterByPrice(List<OffersDBO> offers, double maxPrice) {
        List <OffersDBO> filteredOffers = new ArrayList<OffersDBO>();
        for (OffersDBO offer: offers) {
            if (offer.getPrice() <= maxPrice) {
                filteredOffers.add(offer);
            }
        }
        return filteredOffers;
    }

    // Return list of OffersDBOs that match 'propertyType' & 'nameString'
    public List<OffersDBO> searchByPropertyTypeAndName(String propertyTypeString, String nameString) {
        List<Integer> offersIds = OffersHandler.getInstance().getOffers(propertyTypeString, nameString);
        System.out.println("Got these offers ids: " + offersIds.toString());
        return OffersHandler.getInstance().loadOffers(offersIds);
    }

    // search for all offers that are close to given type within a given distance
    public List<OffersDBO> searchCloseTo(String type) {
        List<OffersDBO> offersDBOs = new ArrayList<OffersDBO>();

        // check if relevant distance is selected, otherwise dont fetch offers
        if (!distance.getSelectionModel().getSelectedItem().toString().contains("any")) {
            // get the distance
            int distanceValue = Integer.parseInt(distance.getSelectionModel().getSelectedItem().toString());
            System.out.println("Selected distance to " + type + " of value: " + distanceValue);

            // get all offers that are close to center within a given distance
            List<Integer> offersIds = SpatialHandler.getInstance().selectWithinDistance(type, distanceValue);
            System.out.println("Got these offers close to " + type + ": " + offersIds.toString());
            offersDBOs = OffersHandler.getInstance().loadOffers(offersIds);
        } else {
            System.out.println("'any' was selected, getting all offers");
            offersDBOs = OffersHandler.getInstance().getAllOffers();
        }
        return offersDBOs;
    }

    // get common offer objects in two lists, used to merge search results from different queries
    public List<OffersDBO> getIntersection(List<OffersDBO> first, List<OffersDBO> second) {
        List<OffersDBO> intersection = new ArrayList<>();

        for (OffersDBO offerDBOFromFirst: first) {
            for (OffersDBO offerDBOFromSecond: second) {
                if (offerDBOFromFirst.getId() == offerDBOFromSecond.getId()) {
                    intersection.add(offerDBOFromFirst);
                }
            }
        }

        return intersection;
    }

    // get union from two lists of offersdbo
    public List<OffersDBO> getUnion(List<OffersDBO> first, List<OffersDBO> second) {

        // add whole first array
        List<OffersDBO> union = new ArrayList<>(first);

        // start adding second array
        for (OffersDBO offerDBOFromSecond: second) {
            for (OffersDBO offerDBOFromUnion: union) {
                if (offerDBOFromSecond.getId() == offerDBOFromUnion.getId()) {
                    break;
                }
                union.add(offerDBOFromSecond);
            }
        }

        return union;
    }


    // load lands with corresponding object type within its boundaries
    public List<OffersDBO> getObjectWithin(String type) {
        List<Integer> offerIds = SpatialHandler.getInstance().selectWithObject(type);
        return OffersHandler.getInstance().loadOffers(offerIds);
    }

    public void clearResults() {
        // remove all previously loaded results in the HBOX of results
        results.getChildren().clear();
    }

    /* Execute search query */
    @FXML
    public void searchSubmit() {
        clearResults();
        // remove all previously loaded results from canvas
        clearCanvas();
        // load all objects from DB, display as background
        loadShapesFromDb();

        // list of filtered/queried offers
        List<OffersDBO> offers = new ArrayList<OffersDBO>();

        // 1. collect data from form
        String nameString = nameField.getText();
        String propertyTypeString = propertyType.getSelectionModel().getSelectedItem().toString();
        String transactionTypeString = transactionType.getSelectionModel().getSelectedItem().toString();
        String streetString = streetField.getText();
        boolean treesChecked = checkTrees.isSelected();
        boolean waterChecked = checkWater.isSelected();
        double maxPrice = maxPriceSlider.getValue();
        System.out.println("name: " + nameString);
        System.out.println("property type: " + propertyTypeString);
        System.out.println("transaction type: " + transactionTypeString);
        System.out.println("street: " + streetString);
        System.out.println("max price: " + maxPrice);

        // get all offers that match property type and string
        List<OffersDBO> offersDBOsMatchingTypeAndName = new ArrayList<OffersDBO>();
        List<OffersDBO> offersDBOsCloseToObject = new ArrayList<OffersDBO>();

        // 2. Search
        /* First, search only by offer type and name */
        offersDBOsMatchingTypeAndName = searchByPropertyTypeAndName(propertyTypeString, nameString);

        /* Second, search by distance to given type within given distance */
        offersDBOsCloseToObject = searchCloseTo(distanceToObjectType.getSelectionModel().getSelectedItem().toString());

        offers = getIntersection(offersDBOsMatchingTypeAndName, offersDBOsCloseToObject);
        System.out.println("Got this intersection (type,name) + close to object: " + offers.toString());

        /* Filter by price*/
        offers = filterByPrice(offers, maxPrice);


        /* Filter by trees/water*/
        List<OffersDBO> offersFilteredWithTrees = new ArrayList<OffersDBO>();
        List<OffersDBO> offersFilteredWithWater = new ArrayList<OffersDBO>();

        if (treesChecked) {
            List<OffersDBO> offersWithTrees = getIntersection(offers, getObjectWithin("Tree"));
            List<OffersDBO> offersWithForest = getIntersection(offers, getObjectWithin("Forest"));
            System.out.println("Got this intersection with trees: " + offersWithTrees.toString());
            System.out.println("Got this intersection with forest: " + offersWithForest.toString());
            offersFilteredWithTrees = getUnion(offersWithTrees, offersWithForest);
            System.out.println("Got this union of offers with trees: " + offersFilteredWithTrees);

            offers = offersFilteredWithTrees;
        }

        if (waterChecked) {
            List<OffersDBO> offersWithLake = getIntersection(offers, getObjectWithin("Lake"));
            List<OffersDBO> offersWithRiver = getIntersection(offers, getObjectWithin("River"));
            System.out.println("Got this intersection with lake: " + offersWithLake.toString());
            System.out.println("Got this intersection with river: " + offersWithRiver.toString());
            offersFilteredWithWater = getUnion(offersWithLake, offersWithRiver);
            System.out.println("Got this union of offers with water: " + offersFilteredWithWater);

            offers = offersFilteredWithWater;
        }

        // -- create spatial object ids list, ids of objects that will be painted to canvas
        searchResultsSpatialIds = new ArrayList<Integer>();
        showResults(offers);
    }

    public void showResults(List<OffersDBO> offers) {
        // 3. Display results
        for (OffersDBO offer: offers) {
            AnchorPane offerListItem = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/offerListItem.fxml"));
                offerListItem = loader.load();
                offerListItem.setPadding(new Insets(5, 10, 5, 10));
                offerListItem.getStyleClass().add("resultBox");
                offerListItem.setPrefHeight(130);
                OfferListItemCtrl itemController = loader.getController();
                itemController.setSearchController(this);

                MultimediaHandler multiHandler = MultimediaHandler.getInstance();
                int imageId = multiHandler.getFirstImageId(offer.getId());
                Image image = (imageId == -1) ? null : multiHandler.getPicture(imageId);
                itemController.init(offer, image, true);

                // spatial Id, not displayed, just stored
                searchResultsSpatialIds.add(offer.getSpatialId());

                // Add selection click  handler
                AnchorPane finalOfferListItem = offerListItem;
                offerListItem.setOnMousePressed(mouseEvent -> {

                    System.out.println("Clicked on offer " + offer.getId());
                    // select this object in the canvas
                    selectObjectById(offer.getSpatialId());

                    // remove "selectedResult" style from all results
                    // todo: update this to previous list of results
                    // enable adding/removing styles "selectedResult"
                    for (Node result : results.getChildren()
                    ) {
                        result.getStyleClass().remove("selectedResult");
                    }
                    // add "selectedResult" for this one
                    finalOfferListItem.getStyleClass().add("selectedResult");
                });

                // add the whole result to HBOX of results
                results.getChildren().add(offerListItem);
            } catch (Exception e) {
                e.printStackTrace();
            }


            // highlight specific object with spatialId in the canvasShapes array < - > canvas
            for (Shape shape : canvasShapes) {
                if (shape.id == offer.getSpatialId()) {

                    // highlight with yellow color, so it's diffrent from the rest of "Land" objects
                    shape.visualObject.strokeProperty().setValue(Color.YELLOW);
                    shape.visualObject.shape.setFill(Color.YELLOW.deriveColor(1, 1, 1, 0.3));

                    // apply mouse event handlers to display proper cursors
                    shape.visualObject.shape.setOnMouseEntered(mouseEvent -> {
                        scrollPane.setCursor(Cursor.HAND);
                    });
                    shape.visualObject.shape.setOnMouseExited(mouseEvent -> {
                        scrollPane.setCursor(Cursor.OPEN_HAND);
                    });

                    // handle clicking on the shape by selecting, repainting and showing tooltip
                    AnchorPane finalOfferListItem1 = offerListItem;
                    shape.visualObject.shape.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                        // select this object in the canvas
                        selectObjectById(offer.getSpatialId());

                        // hide the previous tooltip
                        offerNameTooltip.hide();

                        // update tooltip with new offer name
                        offerNameTooltip.setText(offer.getName());

                        // todo: this should be placed in the location of the offer spatial object in canvas, not mouseEvent [x,y]
                        offerNameTooltip.show((Node) mouseEvent.getSource(), mouseEvent.getScreenX() + 20, mouseEvent.getScreenY());
//                        Tooltip.install(shape.visualObject.shape, offerNameTooltip);

                        // remove "selectedResult" class from all results in HBOX of results
                        for (Node result : results.getChildren()) {
                            result.getStyleClass().remove("selectedResult");
                        }

                        // add "selectedResult" class for this specific one
                        finalOfferListItem1.getStyleClass().add("selectedResult");

                        mouseEvent.consume();
                    });
                }
            }
        }
    }

    // removes all shapes from canvas
    @FXML
    private void clearCanvas() {
        for (Shape shape : canvasShapes) {
            shape.clear();
        }
        canvasShapes.clear();
    }

    @FXML
    private void refreshCanvas() {
        clearCanvas();
        loadShapesFromDb();
    }

    // fills canvas with all objects
    @FXML
    public void loadShapesFromDb() {
        if (!DatabaseManager.getInstance().isConnected())
            return;
        SpatialHandler sh = SpatialHandler.getInstance();
        // temporary, load whole canvas
        int[] borders = {0, 0, 1000, 1000};
        List<Integer> objectIds = sh.selectWithinCanvas(borders);
        for(int objectId: objectIds) {
            SpatialDBO object = sh.loadObject(objectId);
            if (object.getShape() != null) {
                Shape canvasShape = object.drawShapeToCanvas(pane, canvasShapes);
                canvasShape.name.set(object.getName());
                canvasShape.description.set(object.getDescription());
                canvasShape.entityType.set(object.getType());
                canvasShape.id = object.getId();
            }
        }
    }

    // select specific shape object on canvas by ID, highlight with bright yellow color
    public void selectObjectById(int id) {
        for (Shape shape: canvasShapes) {

            // update colors only for Land type objects, ignore others
            if (shape.entityType.toString().contains("Land")) {

                // reset all Land objects, even those that are not in search results
                shape.visualObject.shape.setStrokeWidth(2);
                shape.visualObject.strokeProperty().setValue(Color.DARKSLATEGRAY);
                shape.visualObject.shape.setFill(Color.DARKSLATEGRAY.deriveColor(1, 1, 1, 0.2));

                // repaint search results
                for (int searchResultSpatialId: searchResultsSpatialIds) {
                    if (shape.id == searchResultSpatialId) {
                        shape.visualObject.shape.setStrokeWidth(2);
                        shape.visualObject.strokeProperty().setValue(Color.YELLOW);
                        shape.visualObject.shape.setFill(Color.YELLOW.deriveColor(1, 1, 1, 0.3));
                    }
                }

                // finally, primarily select the one which was selected by clicking
                if (shape.id == id) {
                    shape.visualObject.shape.setStrokeWidth(3);
                    shape.visualObject.shape.setStroke(Color.RED);
                    shape.visualObject.shape.setFill(Color.YELLOW.deriveColor(1, 1, 1, 0.5));
                }
            }
        }
    }


    // pane or "canvas" mouse event handler
    EventHandler<MouseEvent> canvasMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {

            // Handle mouse-click event on pane
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                offerNameTooltip.hide();
                scrollPane.setCursor(Cursor.CLOSED_HAND);
                return;
            }

            if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                scrollPane.setCursor(Cursor.OPEN_HAND);
            }

            // update mouse position model [x,y] coordinates
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
                mouseCoordinate.setX(mouseEvent.getX());
                mouseCoordinate.setY(mouseEvent.getY());
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
