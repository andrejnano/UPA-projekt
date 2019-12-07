package controllers.canvasShapes;

import controllers.EnumPtr;
import controllers.StateEnum;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;

public class VisualObject {
    protected ObservableList<Anchor> anchors;
    protected EnumPtr state;
    public Shape shape;

    public VisualObject(Shape s, EnumPtr state) {
        shape = s;
        this.state = state;
        enableDrag();
    }
    // make a node movable by dragging it around with the mouse.
    private void enableDrag() {
        final Coord dragDelta = new Coord();
        shape.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                state.value = StateEnum.edit;
                // record a delta distance for the drag and drop operation.
                dragDelta.x = getCenterX() - mouseEvent.getX();
                dragDelta.y = getCenterY() - mouseEvent.getY();
                shape.getScene().setCursor(Cursor.MOVE);
            }
        });
        shape.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                shape.getScene().setCursor(Cursor.HAND);
            }
        });
        shape.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                double newX = mouseEvent.getX() + dragDelta.x;
                if (newX > 0 && newX < shape.getScene().getWidth()) {
                    setCenterX(newX);
                }
                double newY = mouseEvent.getY() + dragDelta.y;
                if (newY > 0 && newY < shape.getScene().getHeight()) {
                    setCenterY(newY);
                }
            }
        });
        shape.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    shape.getScene().setCursor(Cursor.HAND);
                }
            }
        });
        shape.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown()) {
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
}
