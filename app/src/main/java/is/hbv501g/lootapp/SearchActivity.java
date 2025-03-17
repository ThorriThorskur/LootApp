package is.hbv501g.lootapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import is.hbv501g.lootapp.adapter.CardAdapter;
import is.hbv501g.lootapp.models.Card;


public class SearchActivity extends AppCompatActivity {

    private Button buttonHome;
    private EditText editTextSearch;
    private Button buttonSearch;
    private RecyclerView recyclerViewCards;
    private CardAdapter cardAdapter;
    private List<Card> cardList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        buttonHome = findViewById(R.id.buttonHome);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        recyclerViewCards = findViewById(R.id.recyclerViewCards);
        progressBar = findViewById(R.id.progressBar);

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
        progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService().searchCards(query, 1).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                progressBar.setVisibility(View.GONE);

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
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SearchActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
