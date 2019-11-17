package controllers;

import controllers.canvasShapes.Point;
import controllers.canvasShapes.Polygon;
import controllers.canvasUtils.ConvertSpatialObjects;
import javafx.event.EventHandler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.scene.paint.Color;

// This controller renders all spatial data objects to canvas & handles
// all events associated with them
public class CanvasController implements Initializable, ConvertSpatialObjects {

    @FXML
    Button connectPointsButton;
    @FXML
    Button drawShapesButton;
    @FXML
    Button clearCanvasButton;
    @FXML
    Button logShapesButton;
    @FXML
    Label canvasLabel;
    @FXML
    Canvas canvas;

    GraphicsContext gc;

    // size of canvas
    double canvasWidth;
    double canvasHeight;

    // 2D Points that should be rendered to canvas
    public ArrayList<Point> points;
    public ArrayList<Polygon> polygons;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // simple test to see if controller is correctly connected to view
        canvasLabel.setText("This text was set from the CanvasController");
        canvasWidth = 600;
        canvasHeight = 560;

        // set mouse handler
        canvas.setOnMousePressed(mouseHandler);

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

        polygons = new ArrayList<Polygon>();
        polygons.add(new Polygon(new double[]{40, 55, 60, 85, 250}, new double[]{70, 90, 40, 50, 60}, 5));
        polygons.add(new Polygon(new double[]{40, 55, 60, 85, 250}, new double[]{170, 190, 140, 150, 160}, 5));


        drawShapes();
    }

    private void selectPointOnCanvas() {

    }

    private void drawPoints() {
        for(int i=0; i < points.size(); i++) {
            points.get(i).draw(gc);
        }
    }

    private void drawPolygons() {
        for(int i=0; i < polygons.size(); i++) {
            polygons.get(i).draw(gc);
        }
    }

    @FXML
    private void clearCanvas() {
        // repaint
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        // remove old objects
        points.clear();
        polygons.clear();
    }

    @FXML
    private void logShapes() {
        // points
        for(int i=0; i < points.size(); i++) {
            System.out.println("point["+points.get(i).getX()+", "+points.get(i).getY()+"]");
        }

        // polygons
        for(int i=0; i < polygons.size(); i++) {
            System.out.println("polygon["+polygons.get(i).getXPoints()+", "+polygons.get(i).getYPoints()+", "+polygons.get(i).getNumberOfPoints()+"]");
        }
    }

    @FXML
    private void drawShapes() {
        // render
        drawPoints();
        drawPolygons();
    }

    // Connects all points with a new Polygon shape
    @FXML
    private void connectPoints() {
        double[] xPoints = new double[points.size()];
        double[] yPoints = new double[points.size()];
        for (int i=0; i < points.size(); i++) {
            xPoints[i] = points.get(i).getX();
            yPoints[i] = points.get(i).getY();
        }
        polygons.add(new Polygon(xPoints, yPoints, points.size()));
        drawShapes();
    }

    private boolean drawShape = true;
    private double mouseX;
    private double mouseY;

    EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                System.out.println("mouse x:" + mouseEvent.getX() + ", y: "+ mouseEvent.getY());
                points.add(new Point(mouseEvent.getX(), mouseEvent.getY()));
                drawShapes();
            }
        }
    };
}
