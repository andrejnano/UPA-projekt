package controllers.canvasUtils;

import controllers.canvasShapes.*;
import oracle.spatial.geometry.JGeometry;

public interface ConvertSpatialObjects {

    // TODO: JGeometry -> Shape
    public static Shape geometryToShape(JGeometry geometry) {
        return null;
    }

    // TODO: Shape -> JGeometry
    public static JGeometry shapeToGeometry(Shape shape) {
        return null;
    }
}
