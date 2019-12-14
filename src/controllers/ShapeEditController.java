package controllers;

import controllers.canvasShapes.Shape;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import model.SpatialDBO;
import model.SpatialHandler;

// some common data of all shapes / geometry objects
// this is the 'app representation' of a JGeometry object
/*
|--------------------------------------------------------------------------
| ShapeEditController
|  - shape metadata editor and viewer, bound to the sidebar form
|--------------------------------------------------------------------------
*/
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
    @FXML
    Label shapeTypeLabel;
    @FXML
    ComboBox entityType;

    CanvasController canvasController;

    private ObjectProperty<Paint> fill;
    private ObjectProperty<Paint> stroke;

    private ScrollPane scrollPane;
    private Pane sideBar;

    public AppState appState;

    String DbType = "None";

    Shape shape;
    private boolean bound;

    public boolean finishedEditingShape;

    public void init(CanvasController canvasController, AppState appState, ScrollPane scrollPane, Pane sideBar) {
        this.scrollPane = scrollPane;
        this.appState = appState;
        this.sideBar = sideBar;
        this.canvasController = canvasController;
        this.entityType.getSelectionModel().selectLast();
    }

    public void setDbType(String value) { this.DbType = value; }

    public void bind(Shape shape) {
        finishedEditingShape = false;
        // assign new shape to the current ShapeEditorController
        this.shape = shape;

        shapeTypeLabel.setText(shape.type);
        // View < - > ViewModel bidirectional binding of Shape name and description
        Bindings.bindBidirectional(nameField.textProperty(), shape.name);
        Bindings.bindBidirectional(descriptionField.textProperty(), shape.description);

        // Color and stroke binding between VisualObject and ShapeEditorController form controls
        Color originalColor = (Color)shape.visualObject.getStroke();
        colorPicker.setValue(originalColor);
        if (this.DbType.equals("None")) {
            colorPicker.setValue(originalColor);
        } else {
            colorPicker.setValue(getTypeColor(this.DbType));
            this.DbType = "None";
        }

        // Bind color picker < - >
//        stroke.bind(Bindings.createObjectBinding(() -> { return (Color)colorPicker.getValue(); }, colorPicker.valueProperty()));
        stroke = shape.visualObject.strokeProperty();

        if (shape.visualObject.shape != null)
            shape.visualObject.shape.setStrokeWidth(5);

        stroke.bind(Bindings.createObjectBinding(() -> {
            Color c = colorPicker.getValue();
            return c;
        }, colorPicker.valueProperty()));

        if (shape.type.contains("POLYGON")) {
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

            if (shape.type.contains("POLYGON"))
                fill.unbind();
            stroke.unbind();
        }
        bound = false;
    }

    private void setDefaults() {
        if (!shape.type.contains("POLYGON")) {
            this.finishedEditingShape = true;
            shape.finish();
        }
        shape.anchorVisibility(false);
    }

    @FXML
    private void okButtonClicked() {
        setDefaults();
        unBind();
        bind(shape);
        setSpatial();
        canvasController.viewMode();
    }

    public void edit(Shape shape) {
        shape.anchorVisibility(false);
        if (finishedEditingShape && appState.canvas.getState().contains("EDIT")) {
            sideBar.setVisible(true);
            unBind();
            bind(shape);
            shape.anchorVisibility(true);
            sideBar.setVisible(true);
            this.scrollPane.setPannable(false);
            finishedEditingShape = true;
        }
    }

    public Color getTypeColor(String entityType) {
        switch (entityType) {
            case "Street":
                return Color.BLACK;
            case "Lake":
                return Color.BLUE;
            case "Tree":
                return Color.GREEN;
            case "Bus":
                return Color.RED;
            case "Building":
                return Color.GRAY;
            default:
                return Color.LIGHTGRAY;
        }
    }

    private void setSpatial() {
        SpatialDBO object = new SpatialDBO();
        object.setName(shape.name.get());
        object.setDescription(shape.description.get());
        object.setId(shape.id);
        object.setShape(shape, shape.type);
        object.setSpatialType(shape.type);
        object.setType(entityType.getSelectionModel().getSelectedItem().toString());
        int id = SpatialHandler.getInstance().insertObject(object);
    }

    @FXML
    private void deleteButtonClicked(ActionEvent actionEvent) {
        ((Pane) scrollPane.getContent()).getChildren().remove(shape.visualObject.shape);
        canvasController.viewMode();
    }
}

