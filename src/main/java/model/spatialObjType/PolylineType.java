package main.java.model.spatialObjType;


import main.java.controllers.canvasShapes.VisualObject;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import main.java.model.SpatialHandler;

public enum PolylineType {
    ROAD("Road", Color.BLACK, 4),
    HIGHWAY("Highway", Color.DARKGRAY, 8),
    PATH("Path", Color.BROWN, 3),
    TRAM("Tram", Color.GRAY, 4),
    POWERLINE("Power line", Color.GOLD, 3),
    RIVER("River", Color.BLUE, 15);

    private String label;
    private Color color;
    private int width;

    PolylineType(String label, Color color, int width) {
        this.label = label;
        this.color = color;
        this.width = width;
    }

    public static PolylineType getByLabel(String transaction) {
        for(PolylineType e : PolylineType.values()){
            if(transaction.equals(e.label)) return e;
        }
        return null;
    }

    public String toString() {
        return label;
    }

    // on mouse hover shows name of line and its length calculated in spatial DB
    public void toColor (VisualObject visualObj, String name, int id) {
        visualObj.strokeProperty().setValue(color);
        visualObj.widthProperty().setValue(width);
        visualObj.textProperty.setValue(label+": " + name + "\nLength: " + SpatialHandler.getInstance().selectObjectLength(id));
    }

    // objects not loaded from db show only its name
    public void toColor (VisualObject visualObj, String name) {
        visualObj.strokeProperty().setValue(color);
        visualObj.widthProperty().setValue(width);
        visualObj.textProperty.setValue(label+": " + name);
    }
}
