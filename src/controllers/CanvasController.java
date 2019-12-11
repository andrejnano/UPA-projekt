package controllers;

import controllers.canvasShapes.*;
import controllers.canvasUtils.ConvertSpatialObjects;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;

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

    // Mouse cursor position
    private Coordinate mouseCoordinate;

    // mouseDragDifference
    private Coordinate dragDelta;

    // 2D Points that should be rendered to canvas
    private ArrayList<Shape> shapes;

    // ??
    private Pane sidePane;

    // Canvas zoom/scaling amount (multiplier)
    final private double canvasScaleDelta = 1.1;
    final private int maxZoomLevel = 20;
    final private int minZoomLevel = 0;
    private int currentZoomLevel = 10;

    Canvas gridCanvas;

    private static CanvasController instance = null;

    // application state
    public AppState appState;
    // API
    // AppState appState = new AppState("ADMIN", "EDIT", "NONE")
    // appState.user.getState() -> returns User.currentState such as "ADMIN" or "SEARCH"
    // appState.user.setState("ADMIN") -> sets User.currentState to "ADMIN" and returns true if success
    // appState.canvas.getState() -> returns Canvas.currentState such as "CREATE" or "EDIT"
    // appState.canvas.setState("EDIT") -> sets Canvas.currentState to "EDIT" and returns true if success
    // appState.canvas.getShapeState() -> returns Canvas.currentShapeState such as "POLYGON" or "POINT"
    // appState.canvas.setShapeState("POLYGON") -> sets Canvas.currentShapeState to "POLYGON" and returns true if success

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;

        // todo: not working, draw grid on canvas
        drawGridOnCanvas();

        // Initialize new AppState, this object will be passed down to underlying components/controllers
        appState = new AppState("ADMIN", "VIEW", "NONE");
        this.viewMode();

        mouseCoordinate = new Coordinate();
        shapes = new ArrayList<>();

        sideBar.setVisible(false);
        idShapeEditController.init(scrollPane, appState, sideBar);
        idShapeEditController.shape = null;

        // assign mouse event handlers on canvas
        pane.setOnMousePressed(canvasMouseHandler);
        pane.setOnMouseMoved(canvasMouseHandler);

        // ??
        dragDelta = new Coordinate();

        // CTRL + SCROLL => Zoom
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if(event.isControlDown())
            {
                handleScrollEventAsZoom(event);
                event.consume();
            }
        });

        // Multi-touch zoom event handling
        scrollPane.setOnZoom(new EventHandler<ZoomEvent>() {
            @Override
            public void handle(ZoomEvent zoomEvent) {
                handleZoom(zoomEvent);
                zoomEvent.consume();
            }
        });
    }

    public static CanvasController getInstance() { return instance; }
    public ArrayList<Shape> getShapes() { return this.shapes; }
    public Pane getPane() { return this.pane; }
    public ShapeEditController getShapeEditController() { return this.idShapeEditController; }

    @FXML
    private void createArea() {
        changeCanvasSettingsForCreate();
        appState.setCanvasShapeState("POLYGON");
        idShapeEditController.bind(new Area(pane, appState, idShapeEditController));
    }

    @FXML
    private void createPoint() {
        changeCanvasSettingsForCreate();
        appState.setCanvasShapeState("POINT");
        idShapeEditController.bind(new Point(pane, appState, idShapeEditController));
    }
    @FXML
    private void createMultiPoint() {
        changeCanvasSettingsForCreate();
        appState.setCanvasShapeState("MULTIPOINT");
        idShapeEditController.bind(new MultiPoint(pane, appState, idShapeEditController));
    }

    @FXML
    private void createPolyLine() {
        changeCanvasSettingsForCreate();
        appState.setCanvasShapeState("POLYLINE");
        idShapeEditController.bind(new PolyLine(pane, appState, shapes, idShapeEditController));
    }

    @FXML
    private void viewMode() {
        appState.setCanvasState("VIEW");
        scrollPane.setPannable(true);
        scrollPane.setCursor(Cursor.HAND);
        canvasStateLabel.setText("[VIEW MODE]");
    }

    @FXML
    private void editMode() {
        appState.setCanvasState("EDIT");
        scrollPane.setPannable(false);
        canvasStateLabel.setText("[EDIT MODE]");
    }

    @FXML
    private void clearCanvas() {
        sideBar.setVisible(false);
        clearUnfinished();
        for (Shape s : shapes) {
            s.clear();
        }
        shapes.clear();

        if (appState.getCanvasShapeState().contains("POLYLINE")) {
            idShapeEditController.shape = new PolyLine(pane, appState, shapes, idShapeEditController);
        }
    }

    // change canvas/editor settings to mode for editing/creating
    private void changeCanvasSettingsForCreate() {
        appState.setCanvasState("CREATE");
        scrollPane.setCursor(Cursor.CROSSHAIR);
        scrollPane.setPannable(false);
        sideBar.setVisible(true);
        clearUnfinished();
        canvasStateLabel.setText("[CREATE MODE]");
    }

    private void clearUnfinished() {
        if (idShapeEditController.shape != null && !idShapeEditController.finished) {
            idShapeEditController.shape.clearUnfinished();
        }
        idShapeEditController.unBind();
    }

    // pane or "canvas" mouse event handler
    EventHandler<MouseEvent> canvasMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            // todo: refactor, not evident what it does
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {

                if (appState.getCanvasState().contains("VIEW")) { return; }
                if (appState.getCanvasState().contains("EDIT")) { return; }

                Coordinate c = new Coordinate(mouseEvent.getX(), mouseEvent.getY());

                if (!idShapeEditController.finished && idShapeEditController.shape.add(c, shapes)) {
                    idShapeEditController.finished = true;
                }
            }

            // update mouse position model [x,y] coordinates
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
                mouseCoordinate.x = mouseEvent.getX();
                mouseCoordinate.y = mouseEvent.getY();
                mouseCoordinateLabel.setText("Mouse[X: " + mouseCoordinate.x + "; Y: " +  mouseCoordinate.y + "]");
            }
        }
    };

    // Handling multi-touch zoom - Two-finger pinching motion
    public void handleZoom(ZoomEvent zoomEvent) {
        double scaleAmount = zoomEvent.getZoomFactor();
        // Construct and configure scale transformation
        Scale scaleTransform = new Scale();
        scaleTransform.setPivotX(mouseCoordinate.x);
        scaleTransform.setPivotY(mouseCoordinate.y);
        scaleTransform.setX(pane.getScaleX() * scaleAmount);
        scaleTransform.setY(pane.getScaleY() * scaleAmount);

        // Apply the transformation
        pane.getTransforms().add(scaleTransform);
        scaleAmountLabel.setText("ZoomFactor: " + zoomEvent.getTotalZoomFactor());

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
        scaleTransform.setPivotX(mouseCoordinate.x);
        scaleTransform.setPivotY(mouseCoordinate.y);
        scaleTransform.setX(pane.getScaleX() * scaleAmount);
        scaleTransform.setY(pane.getScaleY() * scaleAmount);
        // Apply the transformation
        pane.getTransforms().add(scaleTransform);
        // Update label text with the current zoom level in percentage [0-200%]
        scaleAmountLabel.setText("ZoomLevel: " + currentZoomLevel*10 + "%");

        scrollEvent.consume();
    }

    public void drawGridOnCanvas() {
            gridCanvas = new Canvas(400, 400);
            pane.getChildren().add(gridCanvas);

            GraphicsContext gc = gridCanvas.getGraphicsContext2D();
            gc.clearRect(0, 0, pane.getWidth(), pane.getHeight());
            gc.setLineWidth(1); // change the line width

            for(int i = 0; i < pane.getWidth(); i += 50) {
                gc.strokeLine(i, 0, i, pane.getHeight() - (pane.getHeight() % 50));
            }

            for(int i = 0; i < pane.getHeight(); i += 50) {
                gc.strokeLine(50, i, pane.getWidth(), i);
            }
    }
}