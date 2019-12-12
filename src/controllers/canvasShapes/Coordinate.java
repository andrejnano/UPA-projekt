package controllers.canvasShapes;

/*
|--------------------------------------------------------------------------
| Point coordinates representation [x,y]
|--------------------------------------------------------------------------
*/
public class Coordinate {
    public double x;
    public double y;

    public Coordinate() {
        x = 0;
        y = 0;
    }

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(double x, double y, double gridLockCellSize) {
        double gridLockedX = Math.round((x / gridLockCellSize)) * gridLockCellSize;
        double gridLockedY = Math.round((y / gridLockCellSize)) * gridLockCellSize;
        this.x = gridLockedX;
        this.y = gridLockedY;
    }
}
