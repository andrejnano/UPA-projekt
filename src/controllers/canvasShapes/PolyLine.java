package controllers.canvasShapes;

import controllers.EnumPtr;
import controllers.ShapeEditController;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import java.util.ArrayList;

public class PolyLine extends Shape {
    public PolyLine(Pane pane, EnumPtr state, ArrayList<Shape> shapes, ShapeEditController controller) {
        super(pane, state, controller);
        visualObject = new VisualObject(new Polyline(), state);
        visualObject.shape.setStroke(Color.BLUE);
        pane.getChildren().add(visualObject.shape);
        shapes.add(this);
    }

    public void clear() {
        pane.getChildren().remove(visualObject.shape);
        pane.getChildren().removeAll(visualObject.anchors);
    }

    public boolean add(Coord c, ArrayList<Shape> shapes) {
        Polyline polyline = (Polyline) visualObject.shape;
        xyPoints.add(c.x);
        xyPoints.add(c.y);
        numberOfPoints++;
        polyline.getPoints().addAll(c.x, c.y);
        if (visualObject.anchors != null) {
            pane.getChildren().removeAll(visualObject.anchors);
        }
        visualObject.anchors = null;
        visualObject.anchors = createControlAnchorsFor(((Polyline)visualObject.shape).getPoints(), state);
        anchorVisibility(false);
        pane.getChildren().addAll(visualObject.anchors);
        return false;
    }


}
