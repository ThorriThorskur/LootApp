package is.hbv501g.lootapp.models.api;

import is.hbv501g.lootapp.models.Card;
import is.hbv501g.lootapp.models.InventoryCard;

public class AddCardResponse {
    private String message;
    private Card card;
    private InventoryCard inventoryCard;

    public String getMessage() {
        return message;
    }

    public Card getCard() {
        return card;
    }

    public InventoryCard getInventoryCard() {
        return inventoryCard;
    }
}