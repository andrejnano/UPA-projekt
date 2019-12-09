package controllers;

import controllers.canvasShapes.*;
import controllers.canvasUtils.ConvertSpatialObjects;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;

// This controller renders all spatial data objects to canvas & handles
// all events associated with them
public class CanvasController implements Initializable, ConvertSpatialObjects {
    @FXML
    Button clearCanvasButton;
    @FXML
    Label canvasLabel;
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

    public EnumPtr state;
    private Coord mouseCoord;
    private Coord dragDelta;
    // 2D Points that should be rendered to canvas
    private ArrayList<Shape> shapes;
    private Pane sidePane;
    private double scaleX;
    private double scaleY;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mouseCoord = new Coord();
        // simple test to see if controller is correctly connected to view
        sideBar.setVisible(false);
        canvasLabel.setText("This text was set from the CanvasController");
        state = new EnumPtr();
        state.value = StateEnum.edit;
        shapes = new ArrayList<>();

        idShapeEditController.init(scrollPane, state, sideBar);
        idShapeEditController.shape = null;
        // set mouse handler
        pane.setOnMousePressed(mouseHandler);
        pane.setOnMouseMoved(mouseHandler);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        dragDelta = new Coord();
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if(event.isControlDown())
            {
                zoom(event); // zoom the canvas instead of scrolling the actual pane.
                event.consume();
            }
        });
        scrollPane.setPannable(true);
        scaleX = 1;
        scaleY = 1;
    }


    @FXML
    private void createArea() {
        createVisualObject(StateEnum.Polygon);
        idShapeEditController.bind(new Area(pane, state, idShapeEditController));
    }

    @FXML
    private void createPoint() {
        createVisualObject(StateEnum.Point);
        idShapeEditController.bind(new Point(pane, state, idShapeEditController));
    }
    @FXML
    private void createMultiPoint() {
        createVisualObject(StateEnum.MultiPoint);
        idShapeEditController.bind(new MultiPoint(pane, state, idShapeEditController));
    }

    @FXML
    private void editMode() {
        state.value = StateEnum.edit;
        scrollPane.setPannable(false);
    }

    @FXML
    private void createPolyLine() {
        createVisualObject(StateEnum.Polyline);
        idShapeEditController.bind(new PolyLine(pane, state, shapes, idShapeEditController));
    }

    @FXML
    private void clearCanvas() {
        sideBar.setVisible(false);
        clearUnfinished();
        for (Shape s : shapes) {
            s.clear();
        }
        shapes.clear();

        if (state.value == StateEnum.Polyline) {
            idShapeEditController.shape = new PolyLine(pane, state, shapes, idShapeEditController);
        }
    }



    private void createVisualObject(StateEnum s) {
        scrollPane.setPannable(false);
        state.value = s;
        sideBar.setVisible(true);
        clearUnfinished();
    }

    private void clearUnfinished() {
        if (idShapeEditController.shape != null && !idShapeEditController.finished) {
            idShapeEditController.shape.clearUnfinished();
        }
        idShapeEditController.unBind();
    }

    EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                Coord c = new Coord(mouseEvent.getX(), mouseEvent.getY());
                if (state.value != StateEnum.edit && !idShapeEditController.finished && idShapeEditController.shape.add(c, shapes)) {
                    idShapeEditController.finished = true;
                }
            }
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
                mouseCoord.x = mouseEvent.getX();
                mouseCoord.y =mouseEvent.getY();
            }
        }
    };


        public void zoom(ScrollEvent event) {
            double zoom_fac = 1.05;

            if(event.getDeltaY() < 0) {
                zoom_fac = 2.0 - zoom_fac;
            }
            Scale newScale = new Scale();

            newScale.setPivotX(mouseCoord.x);
            newScale.setPivotY(mouseCoord.y);
            scaleX *= zoom_fac;
            scaleY *= zoom_fac;
            newScale.setX(zoom_fac);
            newScale.setY(zoom_fac);
            pane.getTransforms().add(newScale);
            event.consume();


        }
}