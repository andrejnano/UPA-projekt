package model;

import controllers.CanvasController;
import controllers.EnumPtr;
import controllers.ShapeEditController;
import controllers.canvasShapes.*;
import javafx.scene.layout.Pane;
import oracle.spatial.geometry.JGeometry;
import java.util.ArrayList;

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

    public void printGeometry() {
        ArrayList<Shape> shapes = CanvasController.getInstance().getShapes();
        Pane p = CanvasController.getInstance().getPane();
        EnumPtr state = CanvasController.getInstance().getEnumPtr();
        ShapeEditController idShapeEditController = CanvasController.getInstance().getShapeEditController();
        switch (this.shape.getType()) {
            case JGeometry.GTYPE_POINT:
                double[] pointOrds = shape.getPoint();
                Point point = new Point(p, state, idShapeEditController);
                Coord c0 = new Coord(pointOrds[1], pointOrds[2]);
                point.add(c0, shapes);
                break;
            case JGeometry.GTYPE_CURVE:
                double[] lineOrds = shape.getOrdinatesArray();
                PolyLine line = new PolyLine(p, state, shapes, idShapeEditController);
                for (int i = 0; i < lineOrds.length; i += 2) {
                    Coord c1 = new Coord(lineOrds[i], lineOrds[i+1]);
                    line.add(c1, shapes);
                }
                break;
            case JGeometry.GTYPE_POLYGON:
                double[] polygonOrds = shape.getOrdinatesArray();
                Area polygon = new Area(p, state, idShapeEditController);
                for (int i = 0; i < polygonOrds.length; i += 2) {
                    Coord c2 = new Coord(polygonOrds[i], polygonOrds[i+1]);
                    polygon.add(c2, shapes);
                }
                break;
            case JGeometry.GTYPE_MULTIPOINT:
                double[] multiOrds = shape.getOrdinatesArray();
                MultiPoint multiPoint = new MultiPoint(p, state, idShapeEditController);
                for (int i = 0; i < multiOrds.length; i += 2) {
                    Coord c3 = new Coord(multiOrds[i], multiOrds[i+1]);
                    multiPoint.add(c3, shapes);
                }
                break;
        }
    }

    // store existing jgeometry object
    public void setShape(JGeometry shape) {
        this.shape = shape;
    }

    // convert javafx shape to jgeometry for polygon
    public void setShape(Area shape) {
        int[] elemInfo = {1, 1003, 1};
        double[] ordArray = shape.getOrds();
        this.shape = new JGeometry(JGeometry.GTYPE_POLYGON, 0, elemInfo, ordArray);
    }

    // convert javafx shape to jgeometry for line
    public void setShape(PolyLine shape) {
        int[] elemInfo = {1, 2, 1};
        double[] ordArray = shape.getOrds();
        this.shape = new JGeometry(JGeometry.GTYPE_CURVE, 0, elemInfo, ordArray);
    }

    // convert javafx shape to jgeometry for point
    public void setShape(Point shape) {
        this.shape = new JGeometry(shape.getX(), shape.getY(), 0);
    }

    // add fourth gtype
    public void setShape(MultiPoint shape) {
        // each point consists of 2 coords
        int size = shape.visualObject.anchors.size()*2;
        double[] ordArray = shape.getOrds();
        int[] elemInfo = {1, 2, size/2};
        this.shape = new JGeometry(JGeometry.GTYPE_MULTIPOINT, 0, elemInfo, ordArray);

    }
}
