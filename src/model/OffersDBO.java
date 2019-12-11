package model;

public class OffersDBO {

    private static final int defaultId = 0;

    private int id, price;
    private String name, description, type, transaction;

    public OffersDBO() {
        this.id = defaultId;
        this.name = "";
        this.description = "";
        this.type = "";
        this.transaction = "";
        this.price = 0;
    }

    // quick init
    public void init(int id, String name, String description, int price, String type, String transaction) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.transaction = transaction;
    }

    public void setId(int id) { this.id = id; }
    public void setPrice(int price) { this.price = price; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    public void setTransaction(String transaction) { this.transaction = transaction; }

    public int getId() { return this.id; }
    public int getPrice() { return this.price; }
    public String getName() { return this.name; }
    public String getDescription() { return this.description; }
    public String getType() { return this.type; }
    public String getTransaction() { return this.transaction; }
}
