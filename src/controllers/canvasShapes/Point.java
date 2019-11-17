package controllers.canvasShapes;

import javafx.scene.canvas.GraphicsContext;

// simple point in 2D
public class Point extends Shape {
    double x;
    double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        this.type = "point";
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void draw(GraphicsContext gc) {
        gc.fillOval(x, y, 3, 3);
    }
}
