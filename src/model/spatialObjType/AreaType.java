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

    public void toColor (VisualObject visualObj) {
        visualObj.strokeProperty().setValue(color);
        visualObj.shape.setFill(color.deriveColor(1, 1, 1, 0.2));
    }
}
