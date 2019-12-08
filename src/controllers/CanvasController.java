package controllers;

import controllers.canvasShapes.*;
import controllers.canvasUtils.ConvertSpatialObjects;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.skin.SplitPaneSkin;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

// This controller renders all spatial data objects to canvas & handles
// all events associated with them
public class CanvasController implements Initializable, ConvertSpatialObjects {
    @FXML
    Button clearCanvasButton;
    @FXML
    Label canvasLabel;
    @FXML
    AnchorPane pane;
    @FXML
    AnchorPane sideBar;
    @FXML
    ShapeEditController idShapeEditController;
    @FXML
    VBox idShapeEdit;

    public EnumPtr state;

    // 2D Points that should be rendered to canvas
    private ArrayList<Shape> shapes;
    private Pane sidePane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // simple test to see if controller is correctly connected to view
        sideBar.setVisible(false);
        canvasLabel.setText("This text was set from the CanvasController");
        state = new EnumPtr();
        state.value = StateEnum.edit;
        shapes = new ArrayList<>();
        idShapeEditController.shape = null;
        // set mouse handler
        pane.setOnMousePressed(mouseHandler);
        pane.setOnScroll(onScrollEventHandler);
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
    private void editMode() {
        createVisualObject(StateEnum.edit);
    }

    @FXML
    private void createPolyLine() {
        createVisualObject(StateEnum.Polyline);
        idShapeEditController.bind(new PolyLine(pane, state, shapes, idShapeEditController));
    }

    @FXML
    private void clearCanvas() {
//        sideBar.getChildren().remove(sidePane);
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
//        sideBar.getChildren().add(sidePane);
        state.value = s;
        clearUnfinished();
        sideBar.setVisible(true);
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
        }
    };

    private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

        @Override
        public void handle(ScrollEvent event) {
            System.out.println("Scrolllllllllllll");
            double delta = 1.2;

            double scale = pane.getScaleX(); // currently we only use Y, same value is used for X
            double oldScale = scale;

            if (event.getDeltaY() < 0)
                scale /= delta;
            else
                scale *= delta;

            scale = clamp( scale, 0, 10);

            double f = (scale / oldScale)-1;

            double dx = (event.getSceneX() - (pane.getBoundsInParent().getWidth()/2 + pane.getBoundsInParent().getMinX()));
            double dy = (event.getSceneY() - (pane.getBoundsInParent().getHeight()/2 + pane.getBoundsInParent().getMinY()));

            pane.setScaleX( scale);
            pane.setScaleY( scale);

            // note: pivot value must be untransformed, i. e. without scaling
//            pane.setPivot(f*dx, f*dy);
//            pane.

            event.consume();

        }

        public double clamp( double value, double min, double max) {

            if( Double.compare(value, min) < 0)
                return min;

            if( Double.compare(value, max) > 0)
                return max;

            return value;
        }

    };
}