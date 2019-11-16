package controllers;

import controllers.canvasUtils.Point;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.scene.paint.Color;

// This controller renders all spatial data objects to canvas & handles
// all events associated with them
public class CanvasController implements Initializable {

    @FXML
    Label canvasLabel;
    @FXML
    Canvas canvas;
    GraphicsContext gc;

    // 2D Points that should be rendered to canvas
    public ArrayList<Point> points;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // simple test to see if controller is correctly connected to view
        canvasLabel.setText("This text was set from the CanvasController");

        // initialize graphics context ~ API for drawing
        gc = canvas.getGraphicsContext2D();

        // set colors to give an example
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);

        // sample points
        points = new ArrayList<Point>();
        points.add(new Point(45.0, 32.0));
        points.add(new Point(50.0, 40.0));
        points.add(new Point(80.0, 50.0));

        // render
        drawPoints();
    }

    private void drawPoints() {
        for(int i=0; i < points.size(); i++) {
            points.get(i).draw(gc);
        }
    }
}
