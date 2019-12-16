package controllers.canvasShapes;

import controllers.AppState;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import controllers.CanvasController;

import javax.swing.*;
import java.util.ArrayList;

/*
|--------------------------------------------------------------------------
| Visual Object
|--------------------------------------------------------------------------
*/
public class VisualObject {
    public ObservableList<Anchor> anchors;
    public Shape shape;
    public ArrayList<Circle> multipoints;
    protected Paint stroke;
    protected Double width;
    protected ObjectProperty<Paint> strokeProperty;
    protected DoubleProperty widthProperty;
    public AppState appState;
    public SimpleStringProperty textProperty;
    private Tooltip tooltip;

    public VisualObject(Shape s, AppState appState) {
        shape = s;
        this.appState = appState;
        enableDrag();
        textProperty = new SimpleStringProperty();
        tooltip = new Tooltip();
        tooltip.setStyle("-fx-border-radius: 0;");
        textProperty.addListener(e -> {
        tooltip.setText(((SimpleStringProperty) e).getValue().toString());
        if (multipoints != null) {
            for (Circle c : multipoints) {
                Tooltip.install(c, tooltip);
            }
        } else {
                Tooltip.install(shape, tooltip);
            }
        });
    }

    // make a node movable by dragging it around with the mouse.
    private void enableDrag() {

        if (shape == null) {
            return;
        }

        final Coordinate dragDelta = new Coordinate();
        final Coordinate oldMousePosition = new Coordinate();

        shape.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                if (appState.getCanvasState().contains("EDIT")) {

                    shape.toFront();

                    // record a delta distance for the drag and drop operation.
                    dragDelta.setX(0);
                    dragDelta.setY(0);
                    oldMousePosition.setX(mouseEvent.getX());
                    oldMousePosition.setY(mouseEvent.getY());


                    shape.getScene().setCursor(Cursor.MOVE);
                }
            }
        });

        shape.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                if (appState.getCanvasState().contains("EDIT")) {

                    shape.getScene().setCursor(Cursor.HAND);

                    for (Anchor a : anchors) {
                        a.toFront();
                    }
                }
            }
        });

        shape.setOnMouseDragged(mouseEvent -> {
            if (appState.getCanvasState().contains("EDIT")) {
                dragDelta.setX(mouseEvent.getX() - oldMousePosition.getX());
                dragDelta.setY(mouseEvent.getY() - oldMousePosition.getY());
                Bounds bounds = shape.getBoundsInLocal();

                oldMousePosition.setX(mouseEvent.getX());
                oldMousePosition.setY(mouseEvent.getY());
                if (inBounds(bounds.getMinX(), bounds.getMaxX(), dragDelta.getX(), ((Pane) shape.getParent()).getWidth())) {
                    moveX(dragDelta.getX());
                }
                if (inBounds(bounds.getMinY(), bounds.getMaxY(), dragDelta.getY(), ((Pane) shape.getParent()).getHeight())) {
                    moveY(dragDelta.getY());
                }
            }
        });

        shape.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (appState.getCanvasState().contains("EDIT") && !mouseEvent.isPrimaryButtonDown()) {
                    shape.getScene().setCursor(Cursor.HAND);
                }
            }
        });

        shape.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (appState.getCanvasState().contains("EDIT") && !mouseEvent.isPrimaryButtonDown()) {
                    shape.getScene().setCursor(Cursor.DEFAULT);
                }
            }
        });
    }

    private boolean inBounds(double min, double max, double delta, double size) {
        return ((min + delta) > 0 && (max + delta) < size);
    }


    private void moveX(double deltaX) {
        for (Anchor a : anchors) {
            double curX = a.getCenterX();
            a.setCenterX(curX + deltaX);
        }
    }

    private void moveY(double deltaY) {
        for (Anchor a : anchors) {
            double curY = a.getCenterY();
            a.setCenterY(curY + deltaY);
        }
    }

    public Paint getStroke() {
        if (shape == null) {
            return stroke;
        } else {
            return shape.getStroke();
        }
    }

    public ObjectProperty<Paint> strokeProperty() {
        if (strokeProperty != null) {
            return strokeProperty;
        } else {
            return shape.strokeProperty();
        }
    }

    public DoubleProperty widthProperty() {
        if (widthProperty != null) {
            return widthProperty;
        } else {
            return shape.strokeWidthProperty();
        }
    }
}
