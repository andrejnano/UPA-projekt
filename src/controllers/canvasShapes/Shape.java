package controllers.canvasShapes;

import controllers.AppState;
import controllers.ShapeEditController;
import javafx.scene.layout.Pane;
import javafx.beans.property.SimpleStringProperty;
import java.util.ArrayList;

// some common data of all shapes / geometry objects
// this is the 'app representation' of a JGeometry object
/*
|--------------------------------------------------------------------------
| Shape
|--------------------------------------------------------------------------
*/
public abstract class Shape extends PointInsertor {
    int id;
    public SimpleStringProperty name;
    public SimpleStringProperty description;
    public VisualObject visualObject;
    public String type;
    protected ShapeEditController shapeEditController;
    public AppState appState;

    public Shape(Pane p, AppState appState, ShapeEditController controller) {
        super(p);
        this.appState = appState;
        this.shapeEditController = controller;
        this.type = appState.getCanvasShapeState();
        System.out.println(appState.getCanvasShapeState());
        this.name = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
    }

    public abstract void clear();
    public abstract boolean add(Coordinate c, ArrayList<Shape> areas);

    public String getType() {
        return type.toString();
    }

    public void finish() {
        anchorVisibility(false);
        if (visualObject.shape == null)
            return;
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

    public double[] getOrds() {
        int size = visualObject.anchors.size()*2;
        double[] ordArray = new double[size];
        for (int i = 0; i < size; i += 2) {
            Anchor anchor = visualObject.anchors.get(i);
            ordArray[i] = anchor.getCenterX();
            ordArray[i+1] = anchor.getCenterY();
        }
        return ordArray;
    }
}
