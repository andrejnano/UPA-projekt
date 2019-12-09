package controllers;

import controllers.canvasShapes.Shape;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

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
    private ScrollPane scrollPane;
    private EnumPtr state;
    private Pane sideBar;

    public void init(ScrollPane scrollPane, EnumPtr state, Pane sideBar) {
        this.scrollPane = scrollPane;
        this.state = state;
        this.sideBar = sideBar;
    }

    public void bind(Shape shape) {
        finished = false;
        this.shape = shape;
        Bindings.bindBidirectional(nameField.textProperty(), shape.name);
        Bindings.bindBidirectional(descriptionField.textProperty(), shape.description);

        System.out.println(shape.visualObject);
        Color originalColor = (Color)shape.visualObject.getStroke();
        colorPicker.setValue(originalColor);
        stroke = shape.visualObject.strokeProperty();
        if (shape.visualObject.shape != null)
            shape.visualObject.shape.setStrokeWidth(5);
        stroke.bind(Bindings.createObjectBinding(() -> {
            Color c = colorPicker.getValue();
            return c;
        }, colorPicker.valueProperty()));

        if (shape.type == StateEnum.Polygon) {
            fill = shape.visualObject.shape.fillProperty();
            fill.bind(Bindings.createObjectBinding(() -> {
                Color c = colorPicker.getValue();
                return c.deriveColor(1, 1, 1, 0.4);
            }, colorPicker.valueProperty()));
        }
        bound = true;
    }

    public void unBind() {
        if (bound) {
            setDefaults();
            shape.anchorVisibility(false);
            Bindings.unbindBidirectional(nameField.textProperty(), shape.name);
            Bindings.unbindBidirectional(descriptionField.textProperty(), shape.description);

//            if (shape.visualObject.shape == null) {
//                bound = false;
//                return;
//            }

            if (shape.type == StateEnum.Polygon)
                fill.unbind();
            stroke.unbind();
        }
        bound = false;
    }

    private void setDefaults() {
        if (shape.type != StateEnum.Polygon) {
            this.finished = true;
            shape.finish();
        }
        scrollPane.setPannable(true);
        shape.anchorVisibility(false);
    }
    @FXML
    private void okButtonClicked() {
        setDefaults();
        sideBar.setVisible(false);
        state.value = StateEnum.mouseDrag;
    }

    public void edit(Shape shape) {
        shape.anchorVisibility(false);
        if (finished && state.value == StateEnum.edit) {
            sideBar.setVisible(true);
            unBind();
            bind(shape);
            shape.anchorVisibility(true);
            sideBar.setVisible(true);
            this.scrollPane.setPannable(false);
            finished = true;
        }
    }
}

