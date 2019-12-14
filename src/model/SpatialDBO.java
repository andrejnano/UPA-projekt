package model;

import controllers.AppState;
import controllers.CanvasController;
import controllers.ShapeEditController;
import controllers.canvasShapes.*;
import javafx.scene.layout.Pane;
import oracle.spatial.geometry.JGeometry;
import java.util.ArrayList;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class SpatialDBO {

    private static final int defaultId = 0;

    private int id;
    private String name, description, type, spatialType;
    private JGeometry shape, reverseShape;

    public SpatialDBO() {
        this.id = defaultId;
        this.name = "";
        this.description = "";
        this.type = "";
        this.spatialType = "";
        this.shape = null;
        this.reverseShape = null;
    }

    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public String getDescription() { return this.description; }
    public String getType() { return this.type; }
    public String getSpatialType() { return this.spatialType; }
    public JGeometry getShape() { return this.shape; }
    public JGeometry getReverseShape() { return this.reverseShape; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    public void setSpatialType(String spatialType) { this.spatialType = spatialType; }

    // prints to canvas
    public Shape setGeometry(Pane canvasPane) {
        ArrayList<Shape> shapes = CanvasController.getInstance().getShapes();
//        Pane p = CanvasController.getInstance().getPane();
        AppState appState = CanvasController.getInstance().appState;
        ShapeEditController idShapeEditController = CanvasController.getInstance().getShapeEditController();
        Shape canvasShape = null;

        switch (this.shape.getType()) {
            case JGeometry.GTYPE_POINT:
                double[] pointOrds = shape.getPoint();
                Point point = new Point(canvasPane, appState, idShapeEditController);
                Coordinate c0 = new Coordinate(pointOrds[0], pointOrds[1]);
                point.add(c0, shapes);
                canvasShape = point.getShape();
                canvasShape.type = "POINT";
                break;
            case JGeometry.GTYPE_CURVE:
                double[] lineOrds = shape.getOrdinatesArray();
                PolyLine line = new PolyLine(canvasPane, appState, shapes, idShapeEditController);
                for (int i = 0; i < lineOrds.length; i += 2) {
                    Coordinate c1 = new Coordinate(lineOrds[i], lineOrds[i+1]);
                    line.add(c1, shapes);
                }
                canvasShape = line.getShape();
                canvasShape.type = "POLYLINE";
                break;
            case JGeometry.GTYPE_POLYGON:
                double[] polygonOrds = shape.getOrdinatesArray();
                if (this.shape.isRectangle()) {
                    polygonOrds = computeRectangle(polygonOrds);
                }
                Area polygon = new Area(canvasPane, appState, idShapeEditController);

                for (int i = 0; i < polygonOrds.length; i += 2) {
                    Coordinate c2 = new Coordinate(polygonOrds[i], polygonOrds[i+1]);
                    polygon.add(c2, shapes);
                }
                canvasShape = polygon.getShape();
                canvasShape.type = "POLYGON";
                break;
            case JGeometry.GTYPE_MULTIPOINT:
                double[] multiOrds = shape.getOrdinatesArray();
                MultiPoint multiPoint = new MultiPoint(canvasPane, appState, idShapeEditController);

                for (int i = 0; i < multiOrds.length; i += 2) {
                    Coordinate c3 = new Coordinate(multiOrds[i], multiOrds[i+1]);
                    multiPoint.add(c3, shapes);
                }
                canvasShape = multiPoint.getShape();
                canvasShape.type = "MULTIPOINT";
                break;
        }
        return canvasShape;
    }

    // checks if drawn object is rectangle or polygon
    public boolean isRectangle(double[] polygonOrds) {
        if (polygonOrds.length == 8) {
            for(int i = 0; i < polygonOrds.length; i+=2) {
                if (((polygonOrds[i] == polygonOrds[(i+2)%8]) || (polygonOrds[i] == polygonOrds[(i+6)%8]))) {
                    if ((polygonOrds[i+1] == polygonOrds[(i+3)%8]) || (polygonOrds[i+1] == polygonOrds[(i+7)%8])) {
                        continue;
                    }
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    // creates new double array of polygon corners from rectangle ordinates
    double[] computeRectangle(double[] polygonOrds) {
        if (polygonOrds.length == 4) {
            double[] rectangleCorners = {
                    polygonOrds[0], polygonOrds[1],
                    polygonOrds[2], polygonOrds[1],
                    polygonOrds[2], polygonOrds[3],
                    polygonOrds[0], polygonOrds[3],
                    polygonOrds[0], polygonOrds[1]
            };
            return rectangleCorners;
        }
        return polygonOrds;
    }

    // store existing jgeometry object
    public void setShape(JGeometry shape) {
        this.shape = shape;
    }

    // convert javafx shape to jgeometry
    // bugs: not validated in db
    // multipoint returns error with more than 2 points
    public void setShape(Shape canvasShape, String type) {
        switch (type) {
            case "POLYGON":
                double[] ordArray1 = canvasShape.getOrds();
                if (isRectangle(ordArray1)) {
                    this.shape = new JGeometry(ordArray1[0], ordArray1[1], ordArray1[4], ordArray1[5], 0);
                    break;
                }

                double[] reverse = canvasShape.getOrds();
                for(int i = 0; i < reverse.length/2; i+=2) {
                    double temp1 = reverse[i];
                    double temp2 = reverse[i+1];
                    reverse[i] = reverse[reverse.length-i-2];
                    reverse[i+1] = reverse[reverse.length-i-1];
                    reverse[reverse.length-i-2] = temp1;
                    reverse[reverse.length-i-1] = temp2;
                }

                this.reverseShape = JGeometry.createLinearPolygon(reverse, 2, 0);
                this.shape = JGeometry.createLinearPolygon(ordArray1, 2, 0);
                break;
            case "POINT":
                double[] coords = canvasShape.getOrds();
                this.shape = new JGeometry(coords[0], coords[1], 0);
                break;
            case "POLYLINE":
                int[] elemInfo2 = {1, 2, 1};
                double[] ordArray2 = canvasShape.getOrds();
                this.shape = new JGeometry(JGeometry.GTYPE_CURVE, 0, elemInfo2, ordArray2);
                break;
            case "MULTIPOINT":
                double[] ordArray3 = canvasShape.getOrds();
                int[] elemInfo3 = {1, 1, ordArray3.length/2};
                this.shape = new JGeometry(JGeometry.GTYPE_MULTIPOINT, 0, elemInfo3, ordArray3);
                break;
        }
    }
}
