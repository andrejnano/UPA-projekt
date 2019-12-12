package controllers.canvasShapes;

import controllers.AppState;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;


/*
|--------------------------------------------------------------------------
| Visual Object
|--------------------------------------------------------------------------
*/
public class VisualObject {
    public ObservableList<Anchor> anchors;
    public Shape shape;
    protected Paint stroke;
    protected ObjectProperty<Paint> strokeProperty;
    public AppState appState;

    public VisualObject(Shape s, AppState appState) {
        shape = s;
        this.appState = appState;
        enableDrag();
    }

    // make a node movable by dragging it around with the mouse.
    private void enableDrag() {

        if (shape == null) {
            return;
        }

        final Coordinate dragDelta = new Coordinate();
        shape.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (appState.getCanvasState().contains("EDIT")) {
                    shape.toFront();
                    // record a delta distance for the drag and drop operation.
                    dragDelta.x = VisualObject.this.getCenterX() - mouseEvent.getX();
                    dragDelta.y = VisualObject.this.getCenterY() - mouseEvent.getY();
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
//                Bounds bounds = shape.getParent().localToScene(shape.getParent().getBoundsInLocal());
                double newX = mouseEvent.getX() + dragDelta.x;
                if (newX > 0 && newX < ((Pane) shape.getParent()).getMaxWidth()) {
                    setCenterX(newX);
                }
                double newY = mouseEvent.getY() + dragDelta.y;
                if (newY > 0 && newY < ((Pane) shape.getParent()).getMaxHeight()) {
                    setCenterY(newY);
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

    private double getCenterX () {
        double sum = 0;
        int n = 0;
        for (Anchor a : anchors) {
            sum += a.getCenterX();
            n++;
        }
        return sum / n;
    }

    private double getCenterY () {
        double sum = 0;
        int n = 0;
        for (Anchor a : anchors) {
            sum += a.getCenterY();
            n++;
        }
        return sum / n;
    }

    private void setCenterX(double x) {
        double deltaX = x - getCenterX();
        for (Anchor a : anchors) {
            double curX = a.getCenterX();
            a.setCenterX(curX + deltaX);
        }
    }

    private void setCenterY(double y) {
        double deltaY = y - getCenterY();
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
        if (shape == null) {
            return strokeProperty;
        } else {
            return shape.strokeProperty();
        }
    }
}
