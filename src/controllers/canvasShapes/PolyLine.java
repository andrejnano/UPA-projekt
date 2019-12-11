package controllers.canvasShapes;

import controllers.AppState;
import controllers.EnumPtr;
import controllers.ShapeEditController;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import java.util.ArrayList;

public class PolyLine extends Shape {
    public PolyLine(Pane pane, AppState appState, ArrayList<Shape> shapes, ShapeEditController controller) {
        super(pane, appState, controller);
        visualObject = new VisualObject(new Polyline(), appState);
        visualObject.shape.setStroke(Color.BLUE);
        pane.getChildren().add(visualObject.shape);
        shapes.add(this);
    }

    public void clear() {
        pane.getChildren().remove(visualObject.shape);
        pane.getChildren().removeAll(visualObject.anchors);
    }

    public boolean add(Coordinate c, ArrayList<Shape> shapes) {
        Polyline polyline = (Polyline) visualObject.shape;
        xyPoints.add(c.x);
        xyPoints.add(c.y);
        numberOfPoints++;
        polyline.getPoints().addAll(c.x, c.y);
        if (visualObject.anchors != null) {
            pane.getChildren().removeAll(visualObject.anchors);
        }
        visualObject.anchors = null;
        visualObject.anchors = createControlAnchorsFor(((Polyline)visualObject.shape).getPoints());
        anchorVisibility(false);
        pane.getChildren().addAll(visualObject.anchors);
        return false;
    }


}
