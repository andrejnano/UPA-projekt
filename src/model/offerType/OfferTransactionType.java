package model.offerType;

public enum OfferTransactionType {
    RENT("Rent"),
    SELL("Sell"),
    SHARE("Share");

    private String label;

    OfferTransactionType(String label) {
        this.label = label;
    }

    public static OfferTransactionType getByLabel(String transaction) {
        for(OfferTransactionType e : OfferTransactionType.values()){
            if(transaction.equals(e.label)) return e;
        }
        return null;
    }

    public String toString() {
        return label;
    }
}
