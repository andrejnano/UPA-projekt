package model.spatialObjType;

import controllers.canvasShapes.VisualObject;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import model.CanvasEntityType;

public enum AreaType {
    LAKE("Lake", Color.BLUE),
    FOREST("Forest", Color.DARKGREEN),
    FIELD("Field", Color.LIGHTYELLOW),
    PARK("Park", Color.LIGHTGREEN),
    PARKING("Parking", Color.GRAY),
    SQUARE("Square", Color.LIGHTGRAY),
    CENTRE("City centre", Color.LIGHTYELLOW),
    SHOPPING("Shopping area", Color.LIGHTBLUE),
    Playground("Playground", Color.ORANGE);

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

    public void toColor (VisualObject visualObj) {
        visualObj.strokeProperty().setValue(color);
        visualObj.shape.setFill(color.deriveColor(1, 1, 1, 0.4));
    }
}
