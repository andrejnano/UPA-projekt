package main.java.controllers.canvasShapes;

import main.java.controllers.AppState;
import main.java.controllers.ShapeEditController;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;

public class Area extends Shape {

    public Area(Pane p, AppState appState, ShapeEditController controller) {
        super(p, appState, controller);
        visualObject = new VisualObject(new Polygon(), appState);
        visualObject.shape.setStroke(Color.BLUE);
        visualObject.shape.setStrokeType(StrokeType.INSIDE);
    }

    public void clear() {
        pane.getChildren().remove(visualObject.shape);
        pane.getChildren().removeAll(visualObject.anchors);
    }

    public boolean add(Coordinate c, ArrayList<Shape> shapes) {
        double r = 5;
        if (numberOfPoints > 0) {
            double sx = firstCoordinate.getX();
            double sy = firstCoordinate.getY();
            double x = c.getX();
            double y = c.getY();
            if ((sx-x)*(sx-x) + (sy-y)*(sy-y) <= r*r) {
                addLine(oldCoordinate, firstCoordinate);
                addPolygon();
                shapes.add(this);
                pane.getChildren().remove(firstPoint);
                finish();
                return true;
            }
        } else {
            setFirstPoint(c);
        }
        addPoint(c);
        return false;
    }

    public void addPolygon() {
        pane.getChildren().removeAll(lines);
        Polygon polygon = (Polygon) visualObject.shape;

        int size = xyPoints.size();
        Double[] points = new Double[size];
        for (int i = 0; i < size; i++) {
            points[i] = xyPoints.get(i);
        }
        polygon.getPoints().addAll(points);
        pane.getChildren().add(visualObject.shape);
        oldCoordinate = null;

        visualObject.anchors = createControlAnchorsFor(((Polygon)visualObject.shape).getPoints());
        anchorVisibility(false);
        pane.getChildren().addAll(visualObject.anchors);
    }
}
