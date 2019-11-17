package controllers.canvasShapes;

// some common data of all shapes / geometry objects
// this is the 'app representation' of a JGeometry object
public class Shape {
    int id;
    String description;
    String name;
    String type;

    public String getType() {
        return type;
    }
}
