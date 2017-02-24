package payment;

/**
 * Created by haseeb on 24/2/17.
 */
public enum CType {
    DEBIT("debit"),
    CREDIT("credit");

    private final String cardType;

    CType(String cardType) {
        this.cardType = cardType;
    }


    @Override
    public String toString() {
        return cardType;
    }
}