package is.hbv501g.lootapp.models.api;

import java.util.List;
import is.hbv501g.lootapp.models.InventoryCard;

public class InventoryResponse {
    private List<InventoryCard> inventory;

    public List<InventoryCard> getInventory() {
        return inventory;
    }
}
