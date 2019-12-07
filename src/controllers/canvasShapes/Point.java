package controllers.canvasShapes;

import controllers.EnumPtr;
import controllers.ShapeEditController;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

// simple point in 2D
public class Point extends Shape {
    private Anchor circle;


    public Point(Pane p, EnumPtr state, ShapeEditController controller) {
        super(p, state, controller);
        visualObject = new VisualObject(new Circle(), state);
        visualObject.shape.setStroke(Color.BLUE);
        pane.getChildren().add(visualObject.shape);
    }


    public boolean add(Coord c, ArrayList<Shape> shapes) {
        Circle centre = (Circle) visualObject.shape;
        centre.setCenterX(c.x);
        centre.setCenterY(c.y);
        centre.setRadius(3);
        DoubleProperty xProperty = new SimpleDoubleProperty(c.x);
        DoubleProperty yProperty = new SimpleDoubleProperty(c.y);
        xProperty.addListener((observable, oldX, x) -> {
            c.x = x.doubleValue();
            centre.setCenterX(x.doubleValue());
        });
        yProperty.addListener((observable, oldY, y) -> {
            c.y = y.doubleValue();
            centre.setCenterY(y.doubleValue());
        });

        circle = new Anchor(Color.GOLD, xProperty, yProperty, state);
        visualObject.anchors = FXCollections.observableArrayList();
        visualObject.anchors.add(circle);
        anchorVisibility(false);
        pane.getChildren().add(circle);
        shapes.add(this);
        return true;
    }

    public double getX() {
        return circle.getCenterX();
    }

    public double getY() {
        return circle.getCenterY();
    }

    public void clear() {
        pane.getChildren().remove(circle);
        pane.getChildren().remove(visualObject.shape);
    }
}
