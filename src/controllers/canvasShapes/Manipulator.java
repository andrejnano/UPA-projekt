package controllers.canvasShapes;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.scene.paint.Color;

/** Drag the anchors around to change a polygon's points. */
public class Manipulator {
    // @return a list of anchors which can be dragged around to modify points in the format [x1, y1, x2, y2...]
    protected ObservableList<Anchor> createControlAnchorsFor(final ObservableList<Double> points) {
        ObservableList<Anchor> anchors = FXCollections.observableArrayList();

        for (int i = 0; i < points.size(); i+=2) {
            final int idx = i;

            DoubleProperty xProperty = new SimpleDoubleProperty(points.get(i));
            DoubleProperty yProperty = new SimpleDoubleProperty(points.get(i + 1));

            xProperty.addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> ov, Number oldX, Number x) {
                    points.set(idx, (double) x);
                }
            });

            yProperty.addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> ov, Number oldY, Number y) {
                    points.set(idx + 1, (double) y);
                }
            });

            anchors.add(new Anchor(Color.GOLD, xProperty, yProperty));
        }

        return anchors;
    }
}
