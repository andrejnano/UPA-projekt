package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Offer {
    public ObjectProperty<OfferPropertyType> propertyType;
    public ObjectProperty<OfferTransactionType> transactionType;
    public SimpleStringProperty area;
    public SimpleStringProperty description;
    public SimpleStringProperty price;
    public int id;

    public Offer() {
        init();
    }

    public Offer(OffersDBO dbo) {
        init();
        id = dbo.getId();
        propertyType.setValue(OfferPropertyType.valueOf(dbo.getType()));
        transactionType.setValue(OfferTransactionType.valueOf(dbo.getTransaction()));
        area.setValue(dbo.getName());
        description.setValue(dbo.getDescription());
        price.setValue(((Integer) dbo.getPrice()).toString());
    }

    private void init() {
        id = -1;
        propertyType = new SimpleObjectProperty<>();
        transactionType = new SimpleObjectProperty<>();
        area = new SimpleStringProperty();
        description = new SimpleStringProperty();
        price = new SimpleStringProperty();
    }

    public boolean isValid() {
        if (propertyType.getValue() != null &&
                transactionType.getValue() != null &&
                !area.getValue().isEmpty() &&
                !description.getValue().isEmpty() &&
                !price.getValue().isEmpty()) {
            return true;
        }
        return false;
    }

    public OffersDBO toDBO() {
        OffersDBO dbo = new OffersDBO();
        dbo.init(-1, area.getValue(),
                description.getValue(),
                Integer.parseInt(price.getValue()),
                propertyType.getValue().toString(),
                transactionType.getValue().toString());
        return dbo;
    }
//    ComboBox transactionType;
//    Button location;
//    TextField areaField;
//    TextArea descriptionArea;
//    TextField priceField;
}
