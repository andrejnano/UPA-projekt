package main.java.model.spatialObjType;

import main.java.controllers.canvasShapes.VisualObject;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public enum PointType {
    BUS("Bus stop", Color.BLUE, 4),
    TRAM("Tram stop", Color.RED, 4),
    COFFEE("Coffee", Color.BROWN, 6),
    CINEMA("Cinema", Color.GOLD, 6),
    SHOP("Small shop", Color.GREEN, 6),
    WIFI("Wi-fi AP", Color.LIGHTBLUE, 6),
    CHURCH("Church", Color.LIGHTGREEN, 8),
    TREE("Tree", Color.DARKGREEN, 4);

    private String label;
    private Color color;
    private double width;

    PointType(String label, Color color, double width) {
        this.label = label;
        this.color = color;
        this.width = width;
    }

    public static PointType getByLabel(String transaction) {
        for(PointType e : PointType.values()){
            if(transaction.equals(e.label)) return e;
        }
        return null;
    }

    public String toString() {
        return label;
    }

    public void toColor (VisualObject visualObj, String name) {
        visualObj.strokeProperty().setValue(color);
        visualObj.widthProperty().setValue(width);
        visualObj.textProperty.setValue(label+": "+name);
    }
}
