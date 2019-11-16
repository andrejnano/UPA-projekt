package controllers.canvasUtils;

import javafx.scene.canvas.GraphicsContext;

public class Polygon {
    double[] xPoints;
    double[] yPoints;
    int numberOfPoints;

    public Polygon(double[] xPoints, double[] yPoints, int numberOfPoints) {
        this.xPoints = xPoints;
        this.yPoints = yPoints;
        this.numberOfPoints = numberOfPoints;
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
