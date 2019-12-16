package main.java.controllers.canvasUtils;

import main.java.controllers.canvasShapes.*;
import javafx.scene.Node;
import oracle.spatial.geometry.JGeometry;

public interface ConvertSpatialObjects {

    // JGeometry -> Shape
    public static Node geometryToShape(JGeometry geometry) {

//        switch (geometry.getType()) {
//
//            case JGeometry.GTYPE_POINT:
//                // return new point with x, y set from point
//                return new Point(geometry.getPoint()[0], geometry.getPoint()[1]);
//
//            case JGeometry.GTYPE_POLYGON:
//            case JGeometry.GTYPE_CURVE:
//            case JGeometry.GTYPE_COLLECTION:
//            case JGeometry.GTYPE_MULTICURVE:
//            case JGeometry.GTYPE_MULTIPOLYGON:
//            case JGeometry.GTYPE_MULTIPOINT:
//                // TODO: implement
//                break;
//        }

        return null;
    }

    // Shape -> JGeometry
    public static JGeometry shapeToGeometry(Shape shape) {
        // TODO:
        return null;
    }
}
