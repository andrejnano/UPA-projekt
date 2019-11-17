package controllers.canvasShapes;

import javafx.scene.canvas.GraphicsContext;

public class Polygon extends Shape {
    double[] xPoints;
    double[] yPoints;
    int numberOfPoints;

    public Polygon(double[] xPoints, double[] yPoints, int numberOfPoints) {
        this.xPoints = xPoints;
        this.yPoints = yPoints;
        this.numberOfPoints = numberOfPoints;
        this.type = "polygon";
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public double[] getXPoints() {
        return xPoints;
    }

    public double[] getYPoints() {
        return yPoints;
    }

    public void draw(GraphicsContext gc) {
        gc.strokePolygon(xPoints, yPoints, numberOfPoints);
    }
}
