package is.hbv501g.lootapp.models.api;

import java.util.List;
import is.hbv501g.lootapp.models.WishlistCard;

public class WishlistResponse {
    private List<WishlistCard> wishlist;

    public List<WishlistCard> getWishlist() {
        return wishlist;
    }
}