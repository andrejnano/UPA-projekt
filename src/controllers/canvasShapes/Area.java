package controllers.canvasShapes;

import controllers.EnumPtr;
import controllers.ShapeEditController;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;

public class Area extends Shape {

    public Area(Pane p, EnumPtr state) {
        super(p, state);
        visualObject = new Polygon();
    }

    public void clear() {
        pane.getChildren().remove(visualObject);
        pane.getChildren().removeAll(anchors);
    }

    public boolean add(Coord c, ArrayList<Shape> shapes) {
        double r = 10;
        if (numberOfPoints > 0) {
            double sx = firstCoord.x;
            double sy = firstCoord.y;
            double x = c.x;
            double y = c.y;
            if ((sx-x)*(sx-x) + (sy-y)*(sy-y) <= r*r) {
                addLine(oldCoord, firstCoord);
                addPolygon();
                shapes.add(this);
                pane.getChildren().remove(firstPoint);
                return true;
            }
        } else {
            setFirstPoint(c);
        }
        addPoint(c);
        return false;
    }

    private void addPolygon() {
        pane.getChildren().removeAll(lines);
        Polygon polygon = (Polygon) visualObject;

        int size = xyPoints.size();
        Double[] points = new Double[size];
        for (int i = 0; i < size; i++) {
            points[i] = xyPoints.get(i);
        }
        polygon.getPoints().addAll(points);
//        visualObject.setFill(Color.rgb(0,0,200, 0.2));
        pane.getChildren().add(visualObject);
        oldCoord = null;

        anchors = createControlAnchorsFor(((Polygon)visualObject).getPoints(), state);
        pane.getChildren().addAll(anchors);
    }
}
