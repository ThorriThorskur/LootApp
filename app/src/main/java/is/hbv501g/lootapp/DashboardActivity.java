package is.hbv501g.lootapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import is.hbv501g.lootapp.api.ApiClient;
import is.hbv501g.lootapp.models.Card;
import is.hbv501g.lootapp.util.FavoritesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.SharedPreferences;
import com.google.gson.Gson;


public class DashboardActivity extends AppCompatActivity {

    private ImageView imageViewFavoriteCard;
    private FavoritesManager favoritesManager;
    private static final String PREF_DASHBOARD_CACHE = "dashboard_cache";
    private static final String KEY_FAVORITE_CARD = "favorite_card";
    private static final String KEY_CACHE_TIMESTAMP = "cache_timestamp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Navigation buttons
        ImageButton buttonHome = findViewById(R.id.buttonHome);
        ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        Button buttonSearch = findViewById(R.id.buttonSearch);
        Button buttonInventory = findViewById(R.id.buttonInventory);
        Button buttonScanner = findViewById(R.id.buttonScanner);
        Button buttonImport = findViewById(R.id.buttonImport);
        Button buttonWishlist = findViewById(R.id.buttonWishlist);
        Button buttonRuleBook = findViewById(R.id.buttonExtra);

        imageViewFavoriteCard = findViewById(R.id.imageViewFavoriteCard);
        favoritesManager = new FavoritesManager(this);

        // Load the favorite card if available
        loadFavoriteCard();

        // Home button: refresh dashboard
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Settings, Search, Inventory, Wishlist, Scanner, and Import button listeners
        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, settingsActivity.class);
            startActivity(intent);
        });
        buttonSearch.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SearchActivity.class);
            startActivity(intent);
        });
        buttonInventory.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, InventoryActivity.class);
            startActivity(intent);
        });
        buttonWishlist.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, WishlistActivity.class);
            startActivity(intent);
        });
        buttonScanner.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ScannerActivity.class);
            startActivity(intent);
        });
        buttonImport.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ImportActivity.class);
            startActivity(intent);
        });
        buttonRuleBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, RuleBookActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadFavoriteCard() {
        String favoriteCardId = favoritesManager.getFavorite(); // Using single favorite method
        if (favoriteCardId == null) {
            imageViewFavoriteCard.setImageResource(R.drawable.placeholder_card);
            return;
        }

        // Check cache first
        Card cachedCard = getCachedFavoriteCard();
        if (cachedCard != null && cachedCard.getId().equals(favoriteCardId)) {
            // Cache hit: load the image from the cached card data
            Glide.with(DashboardActivity.this)
                    .load(cachedCard.getImageUrl())
                    .placeholder(R.drawable.placeholder_card)
                    .into(imageViewFavoriteCard);
            return;
        }

        // No valid cached card; make the API call.
        ApiClient.getApiService().getCardById(favoriteCardId).enqueue(new Callback<Card>() {
            @Override
            public void onResponse(Call<Card> call, Response<Card> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Card favoriteCard = response.body();
                    // Cache the result for future loads
                    cacheFavoriteCard(favoriteCard);
                    Glide.with(DashboardActivity.this)
                            .load(favoriteCard.getImageUrl())
                            .placeholder(R.drawable.placeholder_card)
                            .into(imageViewFavoriteCard);
                } else {
                    Log.e("DashboardActivity", "Failed to get card details for favorite card");
                    Toast.makeText(DashboardActivity.this, "Favorite card details not found", Toast.LENGTH_SHORT).show();
                    imageViewFavoriteCard.setImageResource(R.drawable.placeholder_card);
                }
            }

            @Override
            public void onFailure(Call<Card> call, Throwable t) {
                Log.e("DashboardActivity", "Error fetching favorite card: " + t.getMessage());
                Toast.makeText(DashboardActivity.this, "Network error while fetching favorite card", Toast.LENGTH_SHORT).show();
                imageViewFavoriteCard.setImageResource(R.drawable.placeholder_card);
            }
        });
    }


    private void cacheFavoriteCard(Card card) {
        SharedPreferences prefs = getSharedPreferences(PREF_DASHBOARD_CACHE, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String cardJson = gson.toJson(card);
        editor.putString(KEY_FAVORITE_CARD, cardJson);
        editor.putLong(KEY_CACHE_TIMESTAMP, System.currentTimeMillis());
        editor.apply();
    }

    private Card getCachedFavoriteCard() {
        SharedPreferences prefs = getSharedPreferences(PREF_DASHBOARD_CACHE, MODE_PRIVATE);
        String cardJson = prefs.getString(KEY_FAVORITE_CARD, null);
        if (cardJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(cardJson, Card.class);
        }
        return null;
    }


}
