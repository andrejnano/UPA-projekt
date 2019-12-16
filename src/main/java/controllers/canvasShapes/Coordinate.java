package main.java.controllers.canvasShapes;

/*
|--------------------------------------------------------------------------
| Point coordinates representation [x,y]
|--------------------------------------------------------------------------
*/
public class Coordinate {
    private double x;
    private double y;

    public Coordinate() {
        x = 0;
        y = 0;
    }

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(double x, double y, double gridLockCellSize) {
        this.x = Math.round((x / gridLockCellSize)) * gridLockCellSize;
        this.y = Math.round((y / gridLockCellSize)) * gridLockCellSize;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setX(double x, double gridLockCellSize) {
        this.x = Math.round((x / gridLockCellSize)) * gridLockCellSize;
    }

    public double getX() {
        return this.x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setY(double y, double gridLockCellSize) {
        this.y = Math.round((y / gridLockCellSize)) * gridLockCellSize;
    }

    public double getY() {
        return this.y;
    }

}
