package is.hbv501g.lootapp.models.api;

public class UpdateQuantityRequest {
    private String cardId;
    private int quantity;

    public UpdateQuantityRequest(String cardId, int quantity) {
        this.cardId = cardId;
        this.quantity = quantity;
    }

    public String getCardId() {
        return cardId;
    }

    public int getQuantity() {
        return quantity;
    }
}