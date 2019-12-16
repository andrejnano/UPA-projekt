package controllers;

import controllers.canvasShapes.Shape;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
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

    Shape shape;
    private boolean bound;
    public boolean finishedEditingShape;

    public void init(CanvasController canvasController, AppState appState, ScrollPane scrollPane, Pane sideBar) {
        this.scrollPane = scrollPane;
        this.appState = appState;
        this.sideBar = sideBar;
        this.canvasController = canvasController;
        id = -1;
        curComboBox = null;
    }

    public void bind(Shape shape) {
//        finishedEditingShape = false;
        // assign new shape to the current ShapeEditorController
        this.shape = shape;
        id = shape.id;

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
                if (shape.spatialObjType != null)
                    curComboBox.setValue(PointType.getByLabel(shape.spatialObjType));
                curListener = new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<? extends Object> observable, Object oldVal, Object newVal) {
                        ((PointType)newVal).toColor(shape.visualObject, nameField.getText());
                        shape.spatialObjType = ((PointType)newVal).toString();
                    }
                };
                break;
            case "POLYLINE" :
                curComboBox = polylineType;
                if (shape.spatialObjType != null)
                    curComboBox.setValue(PolylineType.getByLabel(shape.spatialObjType));
                curListener = new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<? extends Object> observable, Object oldVal, Object newVal) {
                        ((PolylineType)newVal).toColor(shape.visualObject, nameField.getText());
                        shape.spatialObjType = ((PolylineType)newVal).toString();
                    }
                };
                break;
            case "POLYGON" :
                curComboBox = areaType;
                if (shape.spatialObjType != null)
                    curComboBox.setValue(AreaType.getByLabel(shape.spatialObjType));
                curListener = new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<? extends Object> observable, Object oldVal, Object newVal) {
                        ((AreaType)newVal).toColor(shape.visualObject, nameField.getText());
                        shape.spatialObjType = ((AreaType)newVal).toString();
                    }
                };
        }
        if (curComboBox != null) {
            curComboBox.setVisible(true);
            curComboBox.valueProperty().addListener(curListener);
        }
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
        id = -1;
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
        //unBind();
        //bind(shape);
        setSpatial();
        canvasController.refreshCanvas();
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

    private void setSpatial() {
        SpatialDBO object = new SpatialDBO();
        object.setName(shape.name.get());
        object.setDescription(shape.description.get());
        object.setId(shape.id);
        object.setShape(shape, shape.type);
        object.setSpatialType(shape.type);
        object.setType(curComboBox.getValue().toString());
        SpatialHandler spatialHandler = SpatialHandler.getInstance();
        if (id != -1) {
            object.setId(id);
            spatialHandler.updateObject(object);
        } else {
            id = spatialHandler.insertObject(object);
        }
    }

    @FXML
    private void deleteButtonClicked(ActionEvent actionEvent) {
        shape.delete();
        canvasController.viewMode();
        if (id != -1) {
            System.out.println("id "+id);
            SpatialHandler.getInstance().deleteObject(id);
        }
        id = -1;
        bound = false;
    }
}

