package is.hbv501g.lootapp.models;

// InventoryCard.java
public class InventoryCard {
    private int id;
    private String cardId;
    private int quantity;
    private int originalQuantity;
    private String condition;
    private String addedAt;
    private Card card;

    // Getters and setters

    public Card getCard() {
        return card;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    //constructor
    public InventoryCard(Card card, int quantity) {
        this.card = card;
        this.quantity = quantity;
        this.originalQuantity = quantity;
    }

    public int getOriginalQuantity() {
        return originalQuantity;
    }

    public void setOriginalQuantity(int originalQuantity) {
        this.originalQuantity = originalQuantity;

    }
}
