package main.java.model.offerType;

public enum OfferPropertyType {
    FLAT("Flat"),
    LAND("Land"),
    DETACHED("Detached house"),
    TOWNHOUSE("Townhouse"),
    BUNGALOW("Bungalow"),
    COTTAGE("Cottage"),
    OTHER("Other");

    private String label;

    OfferPropertyType(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }

    public static OfferPropertyType getByLabel(String code){
        for(OfferPropertyType e : OfferPropertyType.values()){
            if(code.equals(e.label)) {
                return e;
            }
        }
        return null;
    }
}
