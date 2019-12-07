package controllers.canvasShapes;

import controllers.EnumPtr;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import java.util.ArrayList;

public class PolyLine extends Shape {
    public PolyLine(Pane pane, EnumPtr state, ArrayList<Shape> shapes) {
        super(pane, state);
        visualObject = new Polyline();
        pane.getChildren().add(visualObject);
        shapes.add(this);
    }

    public void clear() {
        pane.getChildren().remove(visualObject);
        pane.getChildren().removeAll(anchors);
    }

//    TODO automatic adding new plyline to shapes
    public boolean add(Coord c, ArrayList<Shape> shapes) {
        Polyline polyline = (Polyline) visualObject;
        xyPoints.add(c.x);
        xyPoints.add(c.y);
        numberOfPoints++;
        polyline.getPoints().addAll(c.x, c.y);
        if (anchors != null) {
            pane.getChildren().removeAll(anchors);
        }
        anchors = null;
        anchors = createControlAnchorsFor(((Polyline)visualObject).getPoints(), state);
        pane.getChildren().addAll(anchors);
        return false;
    }


}
