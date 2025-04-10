package is.hbv501g.lootapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class FavoritesManager {
    private static final String PREF_NAME = "favorites_prefs";
    private static final String KEY_FAVORITE = "favorite_card"; // single favorite key
    private SharedPreferences sharedPreferences;

    public FavoritesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Set a single favorite card
    public void addFavorite(String cardId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FAVORITE, cardId);
        editor.apply();
    }

    // Get the favorite card; returns null if none is set
    public String getFavorite() {
        return sharedPreferences.getString(KEY_FAVORITE, null);
    }

    // Remove favorite card
    public void removeFavorite() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_FAVORITE);
        editor.apply();
    }

    // Check if a card is currently set as favorite
    public boolean isFavorite(String cardId) {
        String favorite = getFavorite();
        return favorite != null && favorite.equals(cardId);
    }
}
