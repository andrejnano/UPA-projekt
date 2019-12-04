package controllers.canvasShapes;

import controllers.EnumPtr;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;

public class Area extends PointInsertor {
    private Polygon polygon;

    public Area(Pane p, EnumPtr state) {
        super(p, state);
    }

    public void clear() {
        pane.getChildren().remove(polygon);
        pane.getChildren().removeAll(anchors);
    }

    public boolean add(Coord c, ArrayList<Area> areas) {
        double r = 10;
        if (numberOfPoints > 0) {
            double sx = firstCoord.x;
            double sy = firstCoord.y;
            double x = c.x;
            double y = c.y;
            if ((sx-x)*(sx-x) + (sy-y)*(sy-y) <= r*r) {
                addLine(oldCoord, firstCoord);
                addPolygon();
                areas.add(this);
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
        polygon = new Polygon(xyPoints.stream().mapToDouble(Double::doubleValue).toArray());
        polygon.setFill(Color.rgb(0,0,200, 0.2));
        pane.getChildren().add(polygon);
        oldCoord = null;

        polygon.setStroke(Color.BLUE);
        polygon.setStrokeWidth(2);
        anchors = createControlAnchorsFor(polygon.getPoints(), state);
        pane.getChildren().addAll(anchors);
    }
}
