package controllers.canvasShapes;

import controllers.EnumPtr;
import controllers.ShapeEditController;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;

public class Area extends Shape {

    public Area(Pane p, EnumPtr state, ShapeEditController controller) {
        super(p, state, controller);
        visualObject = new VisualObject(new Polygon(), state);
        visualObject.shape.setStroke(Color.BLUE);
    }

    public void clear() {
        pane.getChildren().remove(visualObject.shape);
        pane.getChildren().removeAll(visualObject.anchors);
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
                finish();
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
        Polygon polygon = (Polygon) visualObject.shape;

        int size = xyPoints.size();
        Double[] points = new Double[size];
        for (int i = 0; i < size; i++) {
            points[i] = xyPoints.get(i);
        }
        polygon.getPoints().addAll(points);
        pane.getChildren().add(visualObject.shape);
        oldCoord = null;

        visualObject.anchors = createControlAnchorsFor(((Polygon)visualObject.shape).getPoints(), state);
        anchorVisibility(false);
        pane.getChildren().addAll(visualObject.anchors);
    }
}
