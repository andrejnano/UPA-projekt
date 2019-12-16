package main.java.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import main.java.model.offerType.OfferPropertyType;
import main.java.model.offerType.OfferTransactionType;

public class Offer {
    public ObjectProperty<OfferPropertyType> propertyType;
    public ObjectProperty<OfferTransactionType> transactionType;
    public SimpleStringProperty name;
    public SimpleStringProperty description;
    public SimpleStringProperty price;
    public SimpleIntegerProperty spatialId;
    public int id;

    public Offer() {
        init();
    }

    public Offer(OffersDBO dbo) {
        init();
        id = dbo.getId();
        System.out.println(dbo.getType());
        OfferPropertyType ot = OfferPropertyType.getByLabel(dbo.getType());
        propertyType.setValue(ot);
        transactionType.setValue(OfferTransactionType.getByLabel(dbo.getTransaction()));
        name.setValue(dbo.getName());
        description.setValue(dbo.getDescription());
        price.setValue(((Integer) dbo.getPrice()).toString());
        spatialId.set(dbo.getSpatialId());
    }

    private void init() {
        id = -1;
        propertyType = new SimpleObjectProperty<>();
        transactionType = new SimpleObjectProperty<>();
        name = new SimpleStringProperty();
        description = new SimpleStringProperty();
        price = new SimpleStringProperty();
        spatialId = new SimpleIntegerProperty();
    }

    public boolean isValid() {
        if (propertyType.getValue() != null &&
                transactionType.getValue() != null &&
                !name.getValue().isEmpty() &&
                !description.getValue().isEmpty() &&
                !price.getValue().isEmpty()) {
            return true;
        }
        return false;
    }

    public OffersDBO toDBO() {
        OffersDBO dbo = new OffersDBO();
        dbo.init(-1, name.getValue(),
                description.getValue(),
                Integer.parseInt(price.getValue()),
                propertyType.getValue().toString(),
                transactionType.getValue().toString(),
                spatialId.intValue());
        return dbo;
    }
}
