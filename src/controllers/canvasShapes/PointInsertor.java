package controllers.canvasShapes;

import controllers.EnumPtr;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;

public class PointInsertor extends Manipulator {
    int numberOfPoints;
    Pane pane;
    protected Coord oldCoord, firstCoord;
    protected ArrayList<Double> xyPoints;
    protected Circle firstPoint;
    protected ArrayList<Line> lines;
    protected ObservableList<Anchor> anchors;
    protected EnumPtr state;


    public PointInsertor(Pane pane, EnumPtr state) {
        this.state = state;
        this.pane = pane;
        this.numberOfPoints = 0;
        lines = new ArrayList<>();
        oldCoord = null;
        xyPoints = new ArrayList<>();

        firstPoint = null;
        firstCoord = null;
    }

    public void clearUnfinished() {
        pane.getChildren().removeAll(lines);
        if (firstPoint != null)
            pane.getChildren().remove(firstPoint);
    }

    protected void addLine(Coord oldC, Coord c) {
        if (c == null || oldC == null)
            return;
        System.out.println(c.x);
        System.out.println(oldC.x);
        Line l = new Line(oldC.x, oldC.y, c.x, c.y);

        lines.add(l);
        pane.getChildren().add(l);
    }

    protected void addPoint(Coord c) {
        xyPoints.add(c.x);
        xyPoints.add(c.y);
        if (oldCoord != null) {
            numberOfPoints = 0;
            addLine(oldCoord, c);
        }
        oldCoord = c;
        numberOfPoints++;
    }
    protected void setFirstPoint(Coord c) {
        double r = 10;
        firstCoord = c;
        firstPoint = new Circle(c.x, c.y, r);
        Color color = Color.rgb(0, 200, 0);
        firstPoint.setFill(color.deriveColor(1, 1, 1, 0.5));
        firstPoint.setStroke(color);
        firstPoint.setStrokeWidth(1);
        firstPoint.setStrokeType(StrokeType.OUTSIDE);
        pane.getChildren().add(firstPoint);
    }

}
