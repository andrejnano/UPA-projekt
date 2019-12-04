package controllers;

import controllers.canvasShapes.Area;
import controllers.canvasShapes.Coord;
import controllers.canvasShapes.PolyLine;
import controllers.canvasShapes.Point;
import controllers.canvasUtils.ConvertSpatialObjects;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polyline;

// This controller renders all spatial data objects to canvas & handles
// all events associated with them
public class CanvasController implements Initializable, ConvertSpatialObjects {
    @FXML
    Button clearCanvasButton;
    @FXML
    Label canvasLabel;
    @FXML
    AnchorPane pane;

    public EnumPtr state;

    // 2D Points that should be rendered to canvas
    private ArrayList<Point> points;
    private ArrayList<Area> areas;
    private ArrayList<PolyLine> polyLines;
    private Area curArea;
    private PolyLine curPolyLine;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // simple test to see if controller is correctly connected to view
        canvasLabel.setText("This text was set from the CanvasController");
        state = new EnumPtr();
        state.value = StateEnum.addPoint;
        points = new ArrayList<>();
        polyLines = new ArrayList<>();
        // set mouse handler
        pane.setOnMousePressed(mouseHandler);

        curArea = null;
        curPolyLine = null;
        areas = new ArrayList<Area>();
    }


    @FXML
    private void createArea() {
        clearUnfinished();
        state.value = StateEnum.addPolygon;
        curArea = new Area(pane, state);
    }

    @FXML
    private void createPoint() {
        clearUnfinished();
        state.value = StateEnum.addPoint;
    }

    @FXML
    private void editMode() {
        clearUnfinished();
        state.value = StateEnum.edit;
    }

    @FXML
    private void createPolyLine() {
        clearUnfinished();
        state.value = StateEnum.addPolyline;
        curPolyLine = new PolyLine(pane, state);
        polyLines.add(curPolyLine);
    }

    @FXML
    private void clearCanvas() {
        clearUnfinished();
        for (Point p : points) {
            System.out.println("Clear point");
            p.clear();
        }
        points.clear();
        points = new ArrayList<Point>();
        for (Area a : areas) {
            a.clear();
        }
        areas.clear();
        areas = new ArrayList<Area>();
        for (PolyLine p : polyLines) {
            p.clear();
        }
        polyLines.clear();
        polyLines = new ArrayList<PolyLine>();
        curPolyLine = new PolyLine(pane, state);
        polyLines.add(curPolyLine);
    }


    private void clearUnfinished() {
        if (curArea != null)
            curArea.clearUnfinished();
    }

    EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                Coord c = new Coord(mouseEvent.getX(), mouseEvent.getY());
                switch (state.value) {
                    case addPolygon:
                        if (curArea.add(c, areas)) {
                            state.value = StateEnum.addPolygon;
                            curArea = new Area(pane, state);
                        }
                        break;
                    case addPoint:
                        Point point = new Point(c, pane, state);
                        points.add(point);
                        break;
                    case addPolyline:
                        curPolyLine.add(c);
                        break;
                }

            }
        }
    };
}