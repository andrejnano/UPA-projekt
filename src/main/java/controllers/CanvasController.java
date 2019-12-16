package main.java.controllers;

import main.java.controllers.canvasShapes.*;
import main.java.controllers.canvasUtils.ConvertSpatialObjects;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
import main.java.model.DatabaseManager;
import main.java.model.SpatialDBO;
import main.java.model.SpatialHandler;

/*
|--------------------------------------------------------------------------
| Canvas Controller
|  - renders all spatial data objects to canvas & handles all events associated with them
|--------------------------------------------------------------------------
*/
public class CanvasController implements Initializable, ConvertSpatialObjects {
    @FXML
    Button clearCanvasButton;
    @FXML
    Label canvasLabel;
    @FXML
    Label scaleAmountLabel;
    @FXML
    Label mouseCoordinateLabel;
    @FXML
    Label canvasStateLabel;
    @FXML
    Pane pane;
    @FXML
    ScrollPane scrollPane;
    @FXML
    AnchorPane sideBar;
    @FXML
    ShapeEditController idShapeEditController;
    @FXML
    Pane idShapeEdit;
    @FXML
    Button refreshButton;
    @FXML
    Button viewButton;
    @FXML
    Button editButton;
    @FXML
    Button createPointButton;
    @FXML
    Button createMultiPointButton;
    @FXML
    Button createPolyLineButton;
    @FXML
    Button createPolygonButton;

    // This controller instance
    private static CanvasController instance = null;

    // application state
    public AppState appState;

    // Mouse cursor position
    private Coordinate mouseCoordinate;

    // mouseDragDifference
    private Coordinate dragDelta;

    // 2D Points that should be rendered to canvas
    private ArrayList<Shape> canvasShapes;

    // ??
    private Pane sidePane;

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
        instance = this;

        // calculate grid size and paint on canvas pane
        gridCellSize = pane.getPrefHeight() / gridRows;
        drawGridOnCanvas();

        // Initialize new AppState, this object will be passed down to underlying components/controllers
        appState = new AppState("ADMIN", "VIEW", "NONE");

        // setup canvas as view mode
        this.viewMode();

        // Collection of shapes in the canvas
        canvasShapes = new ArrayList<>();

        // cursor position tooltip box with coordinate [x,y], displayed on MOUSE PRESSED
        cursorLocationToolTip = new Tooltip();

        // mouse cursor coordinate model [x,y]
        mouseCoordinate = new Coordinate();

        // initialize Shape Editor on the right side bar
        idShapeEditController.init(instance, appState, scrollPane, sideBar);
        idShapeEditController.shape = null;

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

    public static CanvasController getInstance() { return instance; }
    public ArrayList<Shape> getShapes() { return this.canvasShapes; }
    public Pane getPane() { return this.pane; }
    public ShapeEditController getShapeEditController() { return this.idShapeEditController; }

    @FXML
    private void createAreaMode() {
        createMode();
        appState.setCanvasShapeState("POLYGON");
        canvasStateLabel.setText(appState.getCanvasState() + " " + appState.getCanvasShapeState());
        createPolygonButton.getStyleClass().add("active");
        idShapeEditController.bind(new Area(pane, appState, idShapeEditController));
    }

    @FXML
    private void createPointMode() {
        createMode();
        appState.setCanvasShapeState("POINT");
        canvasStateLabel.setText(appState.getCanvasState() + " " + appState.getCanvasShapeState());
        createPointButton.getStyleClass().add("active");
        idShapeEditController.bind(new Point(pane, appState, idShapeEditController));
    }

    @FXML
    private void createMultiPointMode() {
        createMode();
        appState.setCanvasShapeState("MULTIPOINT");
        canvasStateLabel.setText(appState.getCanvasState() + " " + appState.getCanvasShapeState());
        createMultiPointButton.getStyleClass().add("active");
        idShapeEditController.bind(new MultiPoint(pane, appState, idShapeEditController));
    }

    @FXML
    private void createPolyLineMode() {
        createMode();
        appState.setCanvasShapeState("POLYLINE");
        canvasStateLabel.setText(appState.getCanvasState() + " " + appState.getCanvasShapeState());
        createPolyLineButton.getStyleClass().add("active");
        idShapeEditController.bind(new PolyLine(pane, appState, canvasShapes, idShapeEditController));
    }

    @FXML
    void viewMode() {
        appState.setCanvasState("VIEW");
        sideBar.setVisible(false);
        scrollPane.setPannable(true);
        scrollPane.setCursor(Cursor.OPEN_HAND);
        canvasStateLabel.setText("VIEW");
        removeActiveClassFromAllButtons();
        viewButton.getStyleClass().add("active");
    }

    @FXML
    void editMode() {
        appState.setCanvasState("EDIT");
        sideBar.setVisible(true);
        scrollPane.setPannable(false);
        scrollPane.setCursor(Cursor.MOVE);
        canvasStateLabel.setText("EDIT");
        removeActiveClassFromAllButtons();
        editButton.getStyleClass().add("active");
    }

    // change canvas/editor settings to mode for editing/creating
    private void createMode() {
        appState.setCanvasState("CREATE");
        scrollPane.setCursor(Cursor.CROSSHAIR);
        scrollPane.setPannable(false);
        sideBar.setVisible(true);
        clearUnfinished();
        removeActiveClassFromAllButtons();
    }

    private void removeActiveClassFromAllButtons() {
        viewButton.getStyleClass().remove("active");
        editButton.getStyleClass().remove("active");
        createPointButton.getStyleClass().remove("active");
        createMultiPointButton.getStyleClass().remove("active");
        createPolyLineButton.getStyleClass().remove("active");
        createPolygonButton.getStyleClass().remove("active");
    }

    @FXML
    private void clearCanvas() {
        sideBar.setVisible(false);
        clearUnfinished();

        for (Shape shape : canvasShapes) {
            shape.clear();
        }

        canvasShapes.clear();

        if (appState.getCanvasShapeState().contains("POLYLINE")) {
            idShapeEditController.shape = new PolyLine(pane, appState, canvasShapes, idShapeEditController);
        }
    }

    @FXML
    public void refreshCanvas() {
        clearCanvas();
        loadShapesFromDb();
    }

    private void clearUnfinished() {
        if (idShapeEditController.shape != null && !idShapeEditController.finishedEditingShape) {
            idShapeEditController.shape.clearUnfinished();
        }
        idShapeEditController.unBind();
    }

    // pane or "canvas" mouse event handler
    EventHandler<MouseEvent> canvasMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {

            // Handle mouse-click event on pane
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {

                if (appState.getCanvasState().contains("VIEW")) {
                    scrollPane.setCursor(Cursor.CLOSED_HAND);
                    // display tooltip next to cursor
                    cursorLocationToolTip.setText("[x: "+Math.round(mouseEvent.getX())+", y: "+Math.round(mouseEvent.getY())+"]");
                    Node node = (Node) mouseEvent.getSource();
                    cursorLocationToolTip.show(node, mouseEvent.getScreenX() + 10, mouseEvent.getScreenY() + 20);
                    return;
                }

                if (appState.getCanvasState().contains("EDIT")) { return; }

                Coordinate c = new Coordinate(mouseEvent.getX(), mouseEvent.getY(), gridCellSize);

                if (idShapeEditController.shape.add(c, canvasShapes) && !idShapeEditController.finishedEditingShape) {
                    idShapeEditController.finishedEditingShape = true;
                }
            }


            if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (appState.getCanvasState().contains("VIEW")) {
                    cursorLocationToolTip.hide();
                    scrollPane.setCursor(Cursor.OPEN_HAND);
                }
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

    // fills canvas with all objects
    public void loadShapesFromDb() {
        if (!DatabaseManager.getInstance().isConnected())
            return;
        SpatialHandler sh = SpatialHandler.getInstance();
        // temporary, load whole canvas
        //int[] borders = {0, 0, 1000, 1000};
        List<Integer> objectIds = sh.selectWithinCanvas(getVisibleNodes());
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

    // computes required zoom for canvas view function getVisibleNodes()
    private double getZoom() {
        switch (currentZoomLevel) {
            case 7:  return 1.329577;
            case 8:  return 1.208451;
            case 9:  return 1.097183;
            case 10: return 1.000000;
            case 11: return 0.907042;
            case 12: return 0.825352;
            case 13: return 0.749296;
            case 14: return 0.680282;
            case 15: return 0.619718;
            case 16: return 0.563380;
            case 17: return 0.512676;
            case 18: return 0.477465;
            case 19: return 0.423944;
            case 20: return 0.385915;
            default: return currentZoomLevel/10.0;
        }
    }

    // returns array of canvas view boundaries
    private int[] getVisibleNodes() {
        if (currentZoomLevel > 6) {
            // for zoom over 60% load only object below canvas view
            // boundaries of scrollPane and pane inside
            Bounds bounds = scrollPane.getViewportBounds();
            Bounds boundsPane = pane.getBoundsInParent();
            // default size of canvas view, required only in this scope
            int defaultPaneX = 710;
            int defaultPaneY = 630;
            // add 10 points around shown canvas in case of rounding errors
            int roundingDeviation = 10;
            // get top left coordinates of canvas view
            // because of canvas error, requires boundsPane to be added to compute when changing zoom
            double minX = (Math.abs(bounds.getMinX() + boundsPane.getMinX() + 4)) * getZoom();
            double minY = (Math.abs(bounds.getMinY() + boundsPane.getMinY() + 4)) * getZoom();
            int[] rectangleEdges = {
                    (int) minX - roundingDeviation,
                    (int) minY - roundingDeviation,
                    (int) (minX + defaultPaneX * getZoom() + roundingDeviation),
                    (int) (minY + defaultPaneY * getZoom() + roundingDeviation)
            };
            return rectangleEdges;
        } else {
            // for smaller zooms load whole canvas
            int[] paneEdges = {0,0,1000,1000};
            return paneEdges;
        }

    }
}