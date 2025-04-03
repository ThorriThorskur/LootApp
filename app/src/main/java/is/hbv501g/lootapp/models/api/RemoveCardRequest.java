package is.hbv501g.lootapp.models.api;

public class RemoveCardRequest {
    private String cardId;

    public RemoveCardRequest(String cardId) {
        this.cardId = cardId;
    }

    public String getCardId() {
        return cardId;
    }
}
