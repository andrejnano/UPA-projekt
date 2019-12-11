package controllers.canvasShapes;

import controllers.AppState;
import controllers.ShapeEditController;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

// simple point in 2D
public class MultiPoint extends Shape {
    private ArrayList<Circle> circles;

    public MultiPoint(Pane p, AppState appState, ShapeEditController controller) {
        super(p, appState, controller);
        visualObject = new VisualObject(null, appState);
        visualObject.anchors = FXCollections.observableArrayList();
        circles = new ArrayList<Circle>();
        visualObject.stroke = Color.BLUE;
        visualObject.strokeProperty = new SimpleObjectProperty(visualObject.stroke);
    }


    public boolean add(Coordinate c, ArrayList<Shape> shapes) {

        Circle centre = new Circle(c.x, c.y, 3);
        centre.setFill(visualObject.stroke);

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

        visualObject.strokeProperty.addListener((observable, oldPaint, paint) -> {
            centre.setFill(paint);
            visualObject.stroke = paint;
        });

        centre.setOnMouseClicked(e -> {
            shapeEditController.edit(this);
        });

        centre.setOnMouseEntered(e -> {
            if (appState.getCanvasState().contains("EDIT") && !e.isPrimaryButtonDown()) {
                centre.getScene().setCursor(Cursor.HAND);
            }
        });

        centre.setOnMouseExited(e -> {
            if (appState.getCanvasState().contains("EDIT") && !e.isPrimaryButtonDown()) {
                centre.getScene().setCursor(Cursor.DEFAULT);
            }
        });

        circles.add(centre);
        Anchor anchor = new Anchor(Color.GOLD, xProperty, yProperty);
        visualObject.anchors.add(anchor);
        anchorVisibility(false);
        pane.getChildren().add(centre);
        pane.getChildren().add(anchor);
        shapes.add(this);
        return false;
    }

    public void clear() {
        pane.getChildren().removeAll(circles);
    }
}
