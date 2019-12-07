package controllers;

import controllers.EnumPtr;
import controllers.canvasShapes.Coord;
import controllers.canvasShapes.PointInsertor;
import controllers.canvasShapes.Shape;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javax.swing.text.StyleConstants.Background;

// some common data of all shapes / geometry objects
// this is the 'app representation' of a JGeometry object
public class ShapeEditController {
    int id;
    @FXML
    TextField nameField;
    @FXML
    TextField descriptionField;
    @FXML
    ColorPicker colorPicker;
    @FXML
    Button okButton;

    Shape shape;
    private boolean bound;
    private ObjectProperty<Paint> fill;
    private ObjectProperty<Paint> stroke;
    public boolean finished;

    public void bind(Shape shape) {

        System.out.println("!!!binding");
        finished = false;
        this.shape = shape;
        Bindings.bindBidirectional(nameField.textProperty(), shape.name);
        Bindings.bindBidirectional(descriptionField.textProperty(), shape.description);

        System.out.println(shape.visualObject);

        stroke = shape.visualObject.strokeProperty();
        shape.visualObject.setStrokeWidth(3);
        stroke.bind(Bindings.createObjectBinding(() -> {
            Color c = colorPicker.getValue();
            return c;
        }, colorPicker.valueProperty()));

        if (shape.type == StateEnum.Polygon) {
            fill = shape.visualObject.fillProperty();
            fill.bind(Bindings.createObjectBinding(() -> {
                Color c = colorPicker.getValue();
                return c.deriveColor(1, 1, 1, 0.4);
            }, colorPicker.valueProperty()));
        }
        bound = true;
    }

    public void unBind() {
        if (bound) {
            System.out.println("unbinding!!!");
            Bindings.unbindBidirectional(nameField.textProperty(), shape.name);
            Bindings.unbindBidirectional(descriptionField.textProperty(), shape.type);

            System.out.println(shape.visualObject);

            if (shape.type == StateEnum.Polygon)
                fill.unbind();
            stroke.unbind();
        }
        bound = false;
    }

    @FXML
    private void okButtonClicked() {
        if (shape.type != StateEnum.Polygon)
            this.finished = true;
    }
}

