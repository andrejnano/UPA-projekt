package controllers;

import controllers.canvasShapes.Coordinate;
import controllers.canvasShapes.Shape;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
import model.*;
import oracle.sql.INTERVALDS;

import javax.swing.text.DefaultEditorKit;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
    Label mouseCoordinateLabel;
    @FXML
    ComboBox propertyType;
    @FXML
    ComboBox transactionType;
    @FXML
    ChoiceBox centerDistance;
    @FXML
    ChoiceBox lakeDistance;
    @FXML
    HBox results;

    Offer queryOffer;

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

        // select the default property, by selecting the first one
        propertyType.getSelectionModel().selectFirst();
        transactionType.getSelectionModel().selectFirst();
        centerDistance.getSelectionModel().selectFirst();
        lakeDistance.getSelectionModel().selectFirst();

        results.setPadding(new Insets(5, 5, 5,5));
        results.setSpacing(10.0);

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

    // Return list of OffersDBOs that match 'propertyType' & 'nameString'
    public List<OffersDBO> searchByPropertyTypeAndName(String propertyTypeString, String nameString) {
        List<Integer> offersIds = OffersHandler.getInstance().getOffers(propertyTypeString, nameString);
        System.out.println("Got these offers ids: " + offersIds.toString());
        return OffersHandler.getInstance().loadOffers(offersIds);
    }

    // search for all offers that are close to given type within a given distance
    public List<OffersDBO> searchCloseTo(String type) {
        List<Integer> offersIds;

        if (type == "center") {
            // check if relevant distance is selected, otherwise dont fetch offers
            if (!centerDistance.getSelectionModel().getSelectedItem().toString().contains("any")) {
                // get the distance
                int distanceToCenter = Integer.parseInt(centerDistance.getSelectionModel().getSelectedItem().toString());
                // get all offers that are close to center within a given distance
                offersIds = SpatialHandler.getInstance().selectWithinDistance(type, distanceToCenter);
            } else {
                offersIds = SpatialHandler.getInstance().selectAllObjects();
            }

            System.out.println("Got these offers close to " + type + ": " + offersIds.toString());
            return OffersHandler.getInstance().loadOffers(offersIds);
        }

        if (type == "lake") {
            // check if relevant distance is selected, otherwise dont fetch offers
            if (!lakeDistance.getSelectionModel().getSelectedItem().toString().contains("any")) {
                // get the distance
                int distanceToLake = Integer.parseInt(lakeDistance.getSelectionModel().getSelectedItem().toString());
                // get all offers that are close to center within a given distance
                offersIds = SpatialHandler.getInstance().selectWithinDistance(type, distanceToLake);
            } else {
                offersIds = SpatialHandler.getInstance().selectAllObjects();
            }

            System.out.println("Got these offers close to " + type + ": " + offersIds.toString());
            return OffersHandler.getInstance().loadOffers(offersIds);
        }

        return new ArrayList<OffersDBO>();
    }



    @FXML
    public void searchSubmit() {
        results.getChildren().removeAll();

        // 1. collect data from form
        String nameString = nameField.getText();
        String propertyTypeString = propertyType.getSelectionModel().getSelectedItem().toString();
        String transactionTypeString = transactionType.getSelectionModel().getSelectedItem().toString();
        String streetString = streetField.getText();

        System.out.println("name: " + nameString);
        System.out.println("property type: " + propertyTypeString);
        System.out.println("transaction type: " + transactionTypeString);
        System.out.println("street: " + streetString);

        // get all offers that match property type and string
        List<OffersDBO> offersDBOsMatchingTypeAndName = new ArrayList<OffersDBO>();
        List<OffersDBO> offersDBOsCloseToLake = new ArrayList<OffersDBO>();
        List<OffersDBO> offersDBOsCloseToCenter = new ArrayList<OffersDBO>();

        /* First, search only by offer type and name */
        offersDBOsMatchingTypeAndName = searchByPropertyTypeAndName(propertyTypeString, nameString);

        /* Second, search by distance to given type within given distance (both for lake and center) */
        offersDBOsCloseToLake = searchCloseTo("lake");
        offersDBOsCloseToCenter = searchCloseTo("center");

        // -- create spatial object ids list, ids of objects that will be painted to canvas
        List<Integer> spatialIds = new ArrayList<Integer>();

        // 3. display results
        for (OffersDBO offer: offersDBOsMatchingTypeAndName) {

            // check if this offer is in also in the array of offers close to center
            for (OffersDBO offerCloseToCenter: offersDBOsCloseToCenter) {
                if (offer.getId() != offerCloseToCenter.getId()) {
                    continue;
                }
            }

            // check if this offer is in also in the array of offers close to lake
            for (OffersDBO offerCloseToLake: offersDBOsCloseToLake) {
                if (offer.getId() != offerCloseToLake.getId()) {
                    continue;
                }
            }

            // configure a single result
            VBox singleResult = new VBox();
            singleResult.setPadding(new Insets(5, 10, 5, 10));
            singleResult.getStyleClass().add("resultBox");
            singleResult.setPrefHeight(130);
            singleResult.setAlignment(Pos.CENTER_LEFT);

            // name
            Label name = new Label("Name: " + offer.getName());
            singleResult.getChildren().add(name);

            // id
            Label id = new Label("ID: " + Integer.toString(offer.getId()));
            singleResult.getChildren().add(new Label("ID: " + Integer.toString(offer.getId())));

            // spatial Id, not displayed, just stored
            spatialIds.add(offer.getSpatialId());

            // price
            Label price = new Label("Price: " + Integer.toString(offer.getPrice()));
            singleResult.getChildren().add(price);

            // description
            Label description = new Label("Description: " + offer.getDescription());
            description.setWrapText(true);
            description.setPrefWidth(300);
            description.setMinWidth(300);
            singleResult.getChildren().add(description);

            // type
            Label type = new Label("Type: " + offer.getType());
            singleResult.getChildren().add(type);

            // transaction
            Label transaction = new Label("Transaction: " + offer.getTransaction());
            singleResult.getChildren().add(transaction);

            // Add selection click  handler
            singleResult.setOnMousePressed(mouseEvent -> {
                System.out.println("mouse clicked on result " + id);
                // remove "selectedResult" style from all results
                for (Node result: results.getChildren()
                     ) {
                    result.getStyleClass().remove("selectedResult");
                }
                // add "selectedResult" for this one
                singleResult.getStyleClass().add("selectedResult");
            });

            // add the whole result to HBOX of results
            results.getChildren().add(singleResult);
        }

        // draw
        for(int spatialId: spatialIds) {
            SpatialDBO object = SpatialHandler.getInstance().loadObject(spatialId);
            if (object.getShape() != null) {
                Shape canvasShape = object.setGeometry(pane);
                canvasShape.name.set(object.getName());
                canvasShape.description.set(object.getDescription());
                canvasShape.entityType.set(object.getType());
                canvasShape.id = object.getId();
            }
        }
    }

    public void updateCanvas(List<Offer> offers) {

        // for each offer...
//        pane.getChildren().add();
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
