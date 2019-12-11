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

    public Offer(int id) {
        this.id = id;
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
        dbo.init(-1, area.getValue(), description.getValue(), Integer.parseInt(price.getValue()), propertyType.toString(), transactionType.toString());
        return dbo;
    }
//    ComboBox transactionType;
//    Button location;
//    TextField areaField;
//    TextArea descriptionArea;
//    TextField priceField;
}
