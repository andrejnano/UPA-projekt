package controllers.canvasShapes;

import controllers.EnumPtr;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;

// simple point in 2D
public class Point {
    double r;
    private Pane pane;
    private Anchor circle;
    private EnumPtr state;

    public Point(Coord c, Pane p, EnumPtr state) {
        this.state = state;
        pane = p;
        this.r = 2;

        DoubleProperty xProperty = new SimpleDoubleProperty(c.x);
        DoubleProperty yProperty = new SimpleDoubleProperty(c.y);
        xProperty.addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> ov, Number oldX, Number x) {
                c.x = (double) x;
            }
        });

        yProperty.addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> ov, Number oldY, Number y) {
                c.y = (double) y;;
            }
        });

        circle = new Anchor(Color.GOLD, xProperty, yProperty, state);
        circle.setOnDragDetected(e -> {

        });
        p.getChildren().add(circle);
    }

    public double getX() {
        return circle.getCenterX();
    }

    public double getY() {
        return circle.getCenterY();
    }

    public void clear() {
        pane.getChildren().remove(circle);
    }

    public double getR() {
        return r;
    }
}
