package controllers.canvasShapes;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;


/*
|--------------------------------------------------------------------------
| Point Insertor
|--------------------------------------------------------------------------
*/
public class PointInsertor extends Manipulator {
    protected int numberOfPoints;
    protected Pane pane;
    protected Coordinate oldCoordinate, firstCoordinate;
    protected ArrayList<Double> xyPoints;
    protected Circle firstPoint;
    protected ArrayList<Line> lines;
//    protected ObservableList<Anchor> anchors;

    public PointInsertor() {
        init();
    }

    public PointInsertor(Pane pane) {
        this.pane = pane;
        init();
    }

    public void clearUnfinished() {
        pane.getChildren().removeAll(lines);
        if (firstPoint != null)
            pane.getChildren().remove(firstPoint);
        init();
    }

    private void init() {
        this.numberOfPoints = 0;
        lines = new ArrayList<>();
        oldCoordinate = null;
        xyPoints = new ArrayList<>();

        firstPoint = null;
        firstCoordinate = null;
    }

    protected void addLine(Coordinate oldC, Coordinate c) {
        if (c == null || oldC == null)
            return;
        System.out.println(c.x);
        System.out.println(oldC.x);
        Line l = new Line(oldC.x, oldC.y, c.x, c.y);

        lines.add(l);
        pane.getChildren().add(l);
    }

    protected void addPoint(Coordinate c) {
        xyPoints.add(c.x);
        xyPoints.add(c.y);
        if (oldCoordinate != null) {
            numberOfPoints = 0;
            addLine(oldCoordinate, c);
        }
        oldCoordinate = c;
        numberOfPoints++;
    }
    protected void setFirstPoint(Coordinate c) {
        double r = 5;
        firstCoordinate = c;
        firstPoint = new Circle(c.x, c.y, r);
        Color color = Color.rgb(0, 200, 0);
        firstPoint.setFill(color.deriveColor(1, 1, 1, 0.5));
        firstPoint.setStroke(color);
        firstPoint.setStrokeWidth(1);
        firstPoint.setStrokeType(StrokeType.OUTSIDE);
        pane.getChildren().add(firstPoint);
    }

}
