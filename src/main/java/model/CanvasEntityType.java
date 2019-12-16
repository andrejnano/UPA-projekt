package main.java.model;

public enum CanvasEntityType {
    LAKE("Lake"),
    TREE("Tree"),
    STREET("Street"),
    BUSSTOP("Bus"),
    BUILDING("Building"),
    OTHER("Other");

    private String label;

    CanvasEntityType(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }

    public static CanvasEntityType getByLabel(String code){
        for(CanvasEntityType e : CanvasEntityType.values()){
            if(code.equals(e.label)) {
                return e;
            }
        }
        return null;
    }
}
