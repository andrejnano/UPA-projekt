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

    public Offer() {
        propertyType = new SimpleObjectProperty<>();
        transactionType = new SimpleObjectProperty<>();
        area = new SimpleStringProperty();
        description = new SimpleStringProperty();
        price = new SimpleStringProperty();
    }
//    ComboBox transactionType;
//    Button location;
//    TextField areaField;
//    TextArea descriptionArea;
//    TextField priceField;
}
