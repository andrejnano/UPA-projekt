package model;

public enum OfferTransactionType {
    RENT("Rent"),
    SELL("Sell"),
    SHARE("Share");

    private String label;

    OfferTransactionType(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}
