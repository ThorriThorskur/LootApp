package is.hbv501g.lootapp.models;

public class WishlistCard {
    private int id;
    private String cardId;
    private String addedAt;
    private Card card;

    // Constructor
    public WishlistCard(Card card, String addedAt) {
        this.card = card;
        this.addedAt = addedAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getCardId() {
        return cardId;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public Card getCard() {
        return card;
    }

    // Setters if needed
    public void setId(int id) {
        this.id = id;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }
}