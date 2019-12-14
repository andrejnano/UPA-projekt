package controllers;

import controllers.canvasShapes.Shape;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import model.CanvasEntityType;
import model.SpatialDBO;
import model.SpatialHandler;
import model.spatialObjType.AreaType;
import model.spatialObjType.PointType;
import model.spatialObjType.PolylineType;

import java.util.function.Function;

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
    Button okButton;
    @FXML
    Label shapeTypeLabel;
    @FXML
    ComboBox entityType;

    @FXML
    ComboBox polylineType;
    @FXML
    ComboBox areaType;
    @FXML
    ComboBox pointType;


    CanvasController canvasController;

    private ObjectProperty<Paint> fill;
    private ObjectProperty<Paint> stroke;

    private ChangeListener<Object> curListener;
    private ComboBox curComboBox;
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
        curComboBox = null;
    }

    public void setDbType(String value) { this.DbType = value; }

    public void bind(Shape shape) {
//        finishedEditingShape = false;
        // assign new shape to the current ShapeEditorController
        this.shape = shape;

        shapeTypeLabel.setText(shape.type);
        // View < - > ViewModel bidirectional binding of Shape name and description
        Bindings.bindBidirectional(nameField.textProperty(), shape.name);
        Bindings.bindBidirectional(descriptionField.textProperty(), shape.description);

        shapeTypeHide();
        stroke = shape.visualObject.strokeProperty();
        int width = 5;
        switch (shape.type) {
            case "POINT" :
            case "MULTIPOINT" :
                curComboBox = pointType;
                curListener = new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<? extends Object> observable, Object oldVal, Object newVal) {
                        ((PointType)newVal).toColor(shape.visualObject);
                    }
                };
                break;
            case "POLYLINE" :
                curComboBox = polylineType;
                curListener = new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<? extends Object> observable, Object oldVal, Object newVal) {
                        ((PolylineType)newVal).toColor(shape.visualObject);
                    }
                };
                break;
            case "POLYGON" :
                curComboBox = areaType;
                curListener = new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<? extends Object> observable, Object oldVal, Object newVal) {
                        ((AreaType)newVal).toColor(shape.visualObject);
                    }
                };
        }
        if (curComboBox != null) {
            curComboBox.setVisible(true);
            curComboBox.valueProperty().addListener(curListener);
        }
        // Bind color picker < - >
        if (shape.visualObject.shape != null)
            shape.visualObject.shape.setStrokeWidth(width);
        bound = true;
    }

    private void shapeTypeHide() {
        pointType.setVisible(false);
        polylineType.setVisible(false);
        areaType.setVisible(false);
    }

    public void unBind() {
        if (bound) {
            setDefaults();
            shape.anchorVisibility(false);

            Bindings.unbindBidirectional(nameField.textProperty(), shape.name);
            Bindings.unbindBidirectional(descriptionField.textProperty(), shape.description);

            if (curComboBox != null) {
                curComboBox.valueProperty().removeListener(curListener);
            }
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

