package main.java.controllers.canvasShapes;

import main.java.controllers.AppState;
import main.java.controllers.ShapeEditController;
import javafx.scene.layout.Pane;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

// some common data of all shapes / geometry objects
// this is the 'app representation' of a JGeometry object
/*
|--------------------------------------------------------------------------
| Shape
|--------------------------------------------------------------------------
*/
public abstract class Shape extends PointInsertor {
    public int id;
    public SimpleStringProperty name;
    public SimpleStringProperty description;
    // TODO: add entityType (land/tree/lake/street)
    public SimpleStringProperty entityType;
    public VisualObject visualObject;
    public String type;
    public String spatialObjType;
    public Boolean isContainedInOffer;
    protected ShapeEditController shapeEditController;
    public AppState appState;

    public Shape(Pane p, AppState appState, ShapeEditController controller) {
        super(p);
        id = -1;
        this.isContainedInOffer = false;
        this.appState = appState;
        this.shapeEditController = controller;
        this.type = appState.getCanvasShapeState();
        //System.out.println(appState.getCanvasShapeState());
        this.name = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.entityType = new SimpleStringProperty();
    }

    public abstract void clear();
    public abstract boolean add(Coordinate c, ArrayList<Shape> areas);

    public Shape getShape() { return this; }
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

    public void delete() {
        if (visualObject.anchors == null)
            return;
        for (Anchor a : visualObject.anchors) {
            pane.getChildren().remove(a);
        }
        pane.getChildren().removeAll(visualObject.shape);
        if (visualObject.multipoints != null) {
            pane.getChildren().removeAll(visualObject.multipoints);
        }
    }

    public double[] getOrds() {
        List<Double> ordList = new ArrayList<Double>();
        for (int i = 0; i < visualObject.anchors.size(); ++i) {
            Anchor anchor = visualObject.anchors.get(i);
            ordList.add(anchor.getCenterX());
            ordList.add(anchor.getCenterY());
        }
        double[] ordArray = new double[ordList.size()];
        for(int i = 0; i < ordArray.length; ++i) {
            ordArray[i] = ordList.get(i);
        }
        return ordArray;
    }
}
