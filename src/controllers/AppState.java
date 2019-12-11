package controllers;

//
// !WIP
//

import java.util.Arrays;
import java.util.List;

/*
|--------------------------------------------------------------------------
| AppState -- Application data model
| - contains 2 component classes: User & Canvas
|--------------------------------------------------------------------------
*/
// API Usage
// AppState appState = new AppState("ADMIN", "EDIT", "NONE")
// appState.getUserState() -> returns User.currentState such as "ADMIN" or "SEARCH"
// appState.setUserState("ADMIN") -> sets User.currentState to "ADMIN" and returns true if success
// appState.getCanvasState() -> returns Canvas.currentState such as "CREATE" or "EDIT"
// appState.setCanvasState("EDIT") -> sets Canvas.currentState to "EDIT" and returns true if success
// appState.getCanvasShapeState() -> returns Canvas.currentShapeState such as "POLYGON" or "POINT"
// appState.setCanvasShapeState("POLYGON") -> sets Canvas.currentShapeState to "POLYGON" and returns true if success
public class AppState {

    class User {
        private String currentState;

        // allowed user states
        private List<String> possibleStates = Arrays.asList(
                "ADMIN", // editing whole map
                "GUEST", // viewing map
                "USER"   // adding new offers
        );

        public User(String initialState) {
            this.currentState = (possibleStates.contains(initialState)) ? initialState : "";
        }

        public String getState() {
            return this.currentState;
        };

        public boolean setState(String newState) {
            if (!possibleStates.contains(newState)) {
                return false;
            }
            this.currentState = newState;
            return true;
        }
    }

    class Canvas {
        private String currentState;
        private String currentShapeState;

        // allowed canvas states
        private List<String> possibleStates = Arrays.asList(
                "EDIT",
                "CREATE",
                "VIEW",
                "FINISHED",
                "DELETE"
        );

        // allowed canvas shape states
        private List<String> possibleShapeStates = Arrays.asList(
                "POLYGON",
                "POINT",
                "MULTIPOINT",
                "POLYLINE",
                "NONE"
        );

        public Canvas(String initialState, String initialShapeState) {
            this.currentState = (possibleStates.contains(initialState)) ? initialState : "VIEW";
            this.currentShapeState = (possibleShapeStates.contains(initialShapeState)) ? initialShapeState : "NONE";
        }

        public String getState() {
            return this.currentState;
        };

        public String getShapeState() {
            return this.currentShapeState;
        };

        public boolean setState(String newState) {
            if (!this.possibleStates.contains(newState)) {
                return false;
            }
            this.currentState = newState;
            return true;
        }

        public boolean setShapeState(String newShapeState) {
            if (!possibleStates.contains(newShapeState)) {
                return false;
            }
            this.currentShapeState = newShapeState;
            return true;
        }
    }

    public AppState.User user;
    public AppState.Canvas canvas;

    public AppState(String initialUserState, String initialCanvasState, String initialCanvasShapeState) {
        this.user = new User(initialUserState);
        this.canvas = new Canvas(initialCanvasState, initialCanvasShapeState);
    }

    // parent object scope getters and setters
    public String getUserState() {
        return this.user.getState();
    }
    public String getCanvasState() {
        return this.canvas.getState();
    }
    public String getCanvasShapeState() {
        return this.canvas.getShapeState();
    }
    public boolean setUserState(String newUserState) {
        return this.user.setState(newUserState);
    }
    public boolean setCanvasState(String newCanvasState) {
        return this.canvas.setState(newCanvasState);
    }
    public boolean setCanvasShapeState(String newCanvasShapeState) {
        return this.canvas.setShapeState(newCanvasShapeState);
    }
}
