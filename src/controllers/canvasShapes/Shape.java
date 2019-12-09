package controllers.canvasShapes;

import controllers.EnumPtr;
import controllers.ShapeEditController;
import controllers.StateEnum;
import javafx.scene.layout.Pane;
import javafx.beans.property.SimpleStringProperty;
import java.util.ArrayList;

// some common data of all shapes / geometry objects
// this is the 'app representation' of a JGeometry object
public abstract class Shape extends PointInsertor {
    int id;
    public SimpleStringProperty name;
    public SimpleStringProperty description;
    public VisualObject visualObject;
    public StateEnum type;
    protected ShapeEditController shapeEditController;

    public Shape(Pane p, EnumPtr state, ShapeEditController controller) {
        super(p, state);
        shapeEditController = controller;
        type = state.value;
        name = new SimpleStringProperty();
        description = new SimpleStringProperty();
    }

    public abstract void clear();
    public abstract boolean add(Coord c, ArrayList<Shape> areas);

    public StateEnum getType() {
        return type;
    }
    public void finish() {
        anchorVisibility(false);
        visualObject.shape.setOnMouseClicked(e -> {
            shapeEditController.edit(this);
        });
    }


    public void anchorVisibility(boolean visibility) {
        if (visualObject.anchors == null)
            return;
        for (Anchor a : visualObject.anchors) {
            a.setVisible(visibility);
        }
    }

    public Double[] getOrds() {
        Double[] points = new Double[xyPoints.size()];
        for (int i = 0; i < xyPoints.size(); ++i) {
            points[i] = xyPoints.get(i);
        }
        return points;
    }

    public int getOrdSize() {
        return xyPoints.size();
    }
}
