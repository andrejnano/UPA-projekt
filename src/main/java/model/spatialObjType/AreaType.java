package main.java.model.spatialObjType;

import main.java.controllers.canvasShapes.VisualObject;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import main.java.model.CanvasEntityType;
import main.java.model.SpatialHandler;

public enum AreaType {
    LAKE("Lake", Color.BLUE),
    FOREST("Forest", Color.DARKGREEN),
    FIELD("Field", Color.LIGHTYELLOW),
    PARK("Park", Color.LIGHTGREEN),
    PARKING("Parking", Color.LIGHTBLUE),
    SQUARE("Square", Color.PURPLE),
    CENTRE("City centre", Color.SANDYBROWN),
    SHOPPING("Shopping area", Color.LIGHTBLUE),
    PLAYGROUND("Playground", Color.ORANGE),
    LAND("Land", Color.DARKSLATEGRAY);

    private String label;
    private Color color;

    AreaType(String label, Color color) {
        this.label = label;
        this.color = color;
    }

    public String toString() {
        return label;
    }

    public static AreaType getByLabel(String code){
        for(AreaType e : AreaType.values()){
            if(code.equals(e.label)) {
                return e;
            }
        }
        return null;
    }

    // on mouse hover shows name and area of polygon type calculated in DB
    public void toColor (VisualObject visualObj, String name, int id) {
        visualObj.strokeProperty().setValue(color);
        visualObj.shape.setFill(color.deriveColor(1, 1, 1, 0.2));
        visualObj.textProperty.setValue(label+": " + name + "\nTotal area: " + SpatialHandler.getInstance().selectObjectArea(id) + "m²");
    }

    // objects not loaded from db show only its name
    public void toColor (VisualObject visualObj, String name) {
        visualObj.strokeProperty().setValue(color);
        visualObj.shape.setFill(color.deriveColor(1, 1, 1, 0.2));
        visualObj.textProperty.setValue(label+": " + name);
    }
}
