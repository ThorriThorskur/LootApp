package is.hbv501g.lootapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import is.hbv501g.lootapp.adapter.CardAdapter;
import is.hbv501g.lootapp.api.ApiClient;
import is.hbv501g.lootapp.models.Card;
import is.hbv501g.lootapp.models.api.AddCardRequest;
import is.hbv501g.lootapp.models.api.AddCardResponse;
import is.hbv501g.lootapp.models.api.SearchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchActivity extends AppCompatActivity implements CardAdapter.OnCardClickListener {

    private Button buttonHome;
    private EditText editTextSearch;
    private Button buttonSearch;
    private RecyclerView recyclerViewCards;
    private CardAdapter cardAdapter;
    private List<Card> cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        buttonHome = findViewById(R.id.buttonHome);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        recyclerViewCards = findViewById(R.id.recyclerViewCards);

        // Set up RecyclerView
        cardList = new ArrayList<>();
        cardAdapter = new CardAdapter(cardList, this);
        recyclerViewCards.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCards.setAdapter(cardAdapter);

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = editTextSearch.getText().toString().trim();
                if (!TextUtils.isEmpty(query)) {
                    searchCards(query);
                }
            }
        });
    }

    private void searchCards(String query) {

        ApiClient.getApiService().searchCards(query, 1).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    cardList.clear();
                    cardList.addAll(response.body().getCards());
                    cardAdapter.notifyDataSetChanged();

                    if (cardList.isEmpty()) {
                        Toast.makeText(SearchActivity.this, "No cards found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SearchActivity.this, "Search failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Implement the listener method for "Add" button
    @Override
    public void onAddToInventoryClick(Card card) {
        addCardToInventory(card);
    }
    @Override
    public void onCardClick(Card card) {
        // card click card details).
    }

    // This function makes the API call to add the card to inventory
    private void addCardToInventory(Card card) {
        // Create an AddCardRequest using the card's id (ensure your Card model has a proper getId() method)
        AddCardRequest request = new AddCardRequest(card.getId());
        ApiClient.getApiService().addCardToInventory(request).enqueue(new Callback<AddCardResponse>() {
            @Override
            public void onResponse(Call<AddCardResponse> call, Response<AddCardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SearchActivity.this, "Card added to inventory!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(SearchActivity.this, "Failed to add card: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(SearchActivity.this, "Failed to add card.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AddCardResponse> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
