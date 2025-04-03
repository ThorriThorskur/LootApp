package is.hbv501g.lootapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import is.hbv501g.lootapp.api.ApiClient;
import is.hbv501g.lootapp.models.InventoryCard;
import is.hbv501g.lootapp.models.api.InventoryResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsActivity extends AppCompatActivity {

    private TextView textViewMostExpensive, textViewTotalCards, textViewAverageCost;
    private Button buttonBack, buttonHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Retrieve views
        textViewMostExpensive = findViewById(R.id.textViewMostExpensive);
        textViewTotalCards = findViewById(R.id.textViewTotalCards);
        textViewAverageCost = findViewById(R.id.textViewAverageCost);
        buttonBack = findViewById(R.id.buttonBack);
        buttonHome = findViewById(R.id.buttonHome);

        // Set click listeners
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to InventoryActivity
                Intent intent = new Intent(StatisticsActivity.this, InventoryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to DashboardActivity
                Intent intent = new Intent(StatisticsActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Load and compute statistics
        loadInventoryForStats();
    }

    private void loadInventoryForStats() {
        // Call your inventory API endpoint to get data
        ApiClient.getApiService().getInventory().enqueue(new Callback<InventoryResponse>() {
            @Override
            public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<InventoryCard> inventory = response.body().getInventory();
                    if (inventory.isEmpty()) {
                        Toast.makeText(StatisticsActivity.this, "No inventory found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    computeStatistics(inventory);
                } else {
                    Toast.makeText(StatisticsActivity.this, "Failed to load inventory", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InventoryResponse> call, Throwable t) {
                Toast.makeText(StatisticsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void computeStatistics(List<InventoryCard> inventory) {
        double totalValue = 0.0;
        int totalCards = 0;
        double maxPrice = 0.0;
        String mostExpensiveCardName = "N/A";

        for (InventoryCard ic : inventory) {
            if (ic.getCard() != null && ic.getCard().getUsd() != null && !ic.getCard().getUsd().isEmpty()) {
                try {
                    double price = Double.parseDouble(ic.getCard().getUsd());
                    totalValue += price * ic.getQuantity();
                    totalCards += ic.getQuantity();

                    if (price > maxPrice) {
                        maxPrice = price;
                        mostExpensiveCardName = ic.getCard().getName();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        double averageCost = totalCards > 0 ? totalValue / totalCards : 0.0;
        textViewMostExpensive.setText("Most Expensive Card: " + mostExpensiveCardName + " ($" + maxPrice + ")");
        textViewTotalCards.setText("Total Cards: " + totalCards);
        textViewAverageCost.setText("Average Card Cost: $" + String.format("%.2f", averageCost));
    }
}
