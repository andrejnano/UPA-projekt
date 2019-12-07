package controllers.canvasShapes;

import controllers.EnumPtr;
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
    public javafx.scene.shape.Shape visualObject;
    public StateEnum type;

    public Shape(Pane p, EnumPtr state) {
        super(p, state);
        type = state.value;
        name = new SimpleStringProperty();
        description = new SimpleStringProperty();
    }

    public abstract void clear();
    public abstract boolean add(Coord c, ArrayList<Shape> areas);

    public StateEnum getType() {
        return type;
    }
}
