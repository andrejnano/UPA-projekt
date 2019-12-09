package model;

import controllers.canvasShapes.Area;
import controllers.canvasShapes.Point;
import controllers.canvasShapes.PolyLine;
import oracle.spatial.geometry.JGeometry;

public class SpatialDBO {

    private static final int defaultId = 0;

    private int id;
    private String name, description, type;
    private JGeometry shape;

    public SpatialDBO() {
        this.id = defaultId;
        this.name = "";
        this.description = "";
        this.type = "";
        this.shape = null;
    }

    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public String getDescription() { return this.description; }
    public String getType() { return this.type; }
    public JGeometry getShape() { return this.shape; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }

    // store existing jgeometry object
    public void setShape(JGeometry shape) {
        this.shape = shape;
    }

    // convert javafx shape to jgeometry for polygon
    public void setShape(Area shape) {
        int[] elemInfo = {1, 1003, 1};
        double[] ordArray = new double[shape.getOrdSize()];
        for(int i = 0; i < shape.getOrdSize(); ++i) {
            ordArray[i] = shape.getOrds()[i];
        }
        this.shape = new JGeometry(JGeometry.GTYPE_POLYGON, 0, elemInfo, ordArray);
    }

    // convert javafx shape to jgeometry for line
    public void setShape(PolyLine shape) {
        int[] elemInfo = {1, 2, 1};
        double[] ordArray = new double[shape.getOrdSize()];
        for(int i = 0; i < shape.getOrdSize(); ++i) {
            ordArray[i] = shape.getOrds()[i];
        }
        this.shape = new JGeometry(JGeometry.GTYPE_CURVE, 0, elemInfo, ordArray);
    }

    // convert javafx shape to jgeometry for point
    public void setShape(Point shape) {
        this.shape = new JGeometry(shape.getX(), shape.getY(), 0);
    }

    // add fourth gtype
}
