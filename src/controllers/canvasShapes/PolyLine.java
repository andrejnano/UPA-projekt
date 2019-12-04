package controllers.canvasShapes;

import controllers.EnumPtr;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

public class PolyLine extends PointInsertor {
    private Polyline polyline;
    public PolyLine(Pane pane, EnumPtr state) {
        super(pane, state);
        polyline = null;
    }

    public void clear() {
        pane.getChildren().remove(polyline);
        pane.getChildren().removeAll(anchors);
    }

    public boolean add(Coord c) {
        if (firstPoint == null) {
            firstCoord = c;
        }
        addPoint(c);
        if (polyline != null) {
            pane.getChildren().removeAll(polyline);
            pane.getChildren().removeAll(anchors);
        }
        addPolyline();
        return false;
    }

    private void addPolyline() {
        pane.getChildren().removeAll(lines);
        polyline = new Polyline(xyPoints.stream().mapToDouble(Double::doubleValue).toArray());
        pane.getChildren().add(polyline);
        oldCoord = null;

        polyline.setStroke(Color.BLUE);
        polyline.setStrokeWidth(2);
        anchors = createControlAnchorsFor(polyline.getPoints(), state);
        pane.getChildren().addAll(anchors);
    }


}
