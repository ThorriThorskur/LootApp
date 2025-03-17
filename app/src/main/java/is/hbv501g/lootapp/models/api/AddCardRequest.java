package is.hbv501g.lootapp.models.api;

public class AddCardRequest {
    private String cardId;

    public AddCardRequest(String cardId) {
        this.cardId = cardId;
    }

    public String getCardId() {
        return cardId;
    }
}
