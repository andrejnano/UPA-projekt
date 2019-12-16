package main.java.controllers.canvasShapes;

import main.java.controllers.AppState;
import main.java.controllers.CanvasController;
import main.java.controllers.ShapeEditController;
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
        visualObject.width = 0.0;
        visualObject.strokeProperty = new SimpleObjectProperty(visualObject.stroke);
        visualObject.widthProperty = new SimpleDoubleProperty(visualObject.width);
        visualObject.multipoints = new ArrayList<>();
    }


    public boolean add(Coordinate c, ArrayList<Shape> shapes) {

        Circle centre = new Circle(c.getX(), c.getY(), 3);
        centre.setFill(visualObject.stroke);

        visualObject.multipoints.add(centre);
        DoubleProperty xProperty = new SimpleDoubleProperty(c.getX());
        DoubleProperty yProperty = new SimpleDoubleProperty(c.getY());

        xProperty.addListener((observable, oldX, x) -> {
            c.setX(x.doubleValue(), CanvasController.gridCellSize);
            centre.setCenterX(x.doubleValue());
        });

        yProperty.addListener((observable, oldY, y) -> {
            c.setY(y.doubleValue(), CanvasController.gridCellSize);
            centre.setCenterY(y.doubleValue());
        });

        visualObject.strokeProperty.addListener((observable, oldPaint, paint) -> {
            centre.setFill(paint);
            visualObject.stroke = paint;
        });

        visualObject.widthProperty.addListener((observable, oldWidth, newWidth) -> {
            centre.setRadius(newWidth.intValue());
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
