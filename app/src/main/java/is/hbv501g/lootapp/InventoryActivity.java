package is.hbv501g.lootapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import is.hbv501g.lootapp.adapter.InventoryAdapter;
import is.hbv501g.lootapp.api.ApiClient;
import is.hbv501g.lootapp.models.InventoryCard;
import is.hbv501g.lootapp.models.api.InventoryResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryActivity extends AppCompatActivity {

    private InventoryAdapter inventoryAdapter;
    private List<InventoryCard> inventoryList;
    private ProgressBar progressBar;
    private TextView textViewTotalValue;  // New TextView for total value and update message

    // SharedPreferences key for yesterday's total (for end-of-day update)
    private static final String PREFS_NAME = "InventoryPrefs";
    private static final String KEY_YESTERDAY_TOTAL = "yesterday_total";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        ImageButton buttonHome = findViewById(R.id.buttonHome);
        ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        RecyclerView recyclerViewInventory = findViewById(R.id.recyclerViewInventory);
        progressBar = findViewById(R.id.progressBar);
        textViewTotalValue = findViewById(R.id.textViewTotalValue); // Ensure this is defined in your XML

        // Set up RecyclerView
        inventoryList = new ArrayList<>();
        inventoryAdapter = new InventoryAdapter(inventoryList, this);
        recyclerViewInventory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewInventory.setAdapter(inventoryAdapter);

        // Home button
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Restart the DashboardActivity
                Intent intent = new Intent(InventoryActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Settings button
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, settingsActivity.class);
                startActivity(intent);
            }
        });

        Button buttonExport = findViewById(R.id.buttonExport);
        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportCollection();
            }
        });

        Button buttonStats = findViewById(R.id.buttonStats);
        buttonStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });

        // Load inventory when activity starts
        loadInventory();
    }

    private void loadInventory() {
        progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService().getInventory().enqueue(new Callback<InventoryResponse>() {
            @Override
            public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    inventoryList.clear();
                    inventoryList.addAll(response.body().getInventory());
                    inventoryAdapter.notifyDataSetChanged();

                    // Update total value and end-of-day update message
                    updateTotalValue();

                    if (inventoryList.isEmpty()) {
                        // Show empty state
                        findViewById(R.id.textViewEmptyInventory).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.textViewEmptyInventory).setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(InventoryActivity.this, "Failed to load inventory", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InventoryResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InventoryActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Computes the total value of stored cards and updates a TextView with an end-of-day update.
     */
    public void updateTotalValue() {
        double totalValue = 0.0;
        for (InventoryCard ic : inventoryList) {
            if (ic.getCard() != null && ic.getCard().getUsd() != null && !ic.getCard().getUsd().isEmpty()) {
                try {
                    double price = Double.parseDouble(ic.getCard().getUsd());
                    totalValue += price * ic.getQuantity();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        // Retrieve yesterday's total value from SharedPreferences (if available)
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        double yesterdayTotal = Double.longBitsToDouble(prefs.getLong(KEY_YESTERDAY_TOTAL, Double.doubleToLongBits(totalValue)));

        double diff = totalValue - yesterdayTotal;
        String updateMessage;
        if (diff > 0) {
            updateMessage = "Your collection increased by $" + String.format("%.2f", diff);
        } else if (diff < 0) {
            updateMessage = "Your collection dropped by $" + String.format("%.2f", Math.abs(diff));
        } else {
            updateMessage = "No change in collection value";
        }

        textViewTotalValue.setText("Total Value: $" + String.format("%.2f", totalValue) + "\n" + updateMessage);

        // Save today's total as yesterday's value for the next update.
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_YESTERDAY_TOTAL, Double.doubleToLongBits(totalValue));
        editor.apply();
    }


    private String generateCsv(List<InventoryCard> inventory) {
        StringBuilder csv = new StringBuilder();
        // Header row
        csv.append("Card Name,Card Type,Quantity,Image URL\n");
        for (InventoryCard invCard : inventory) {
            if (invCard.getCard() != null) {
                csv.append(invCard.getCard().getName()).append(",");
                csv.append(invCard.getCard().getTypeLine()).append(",");
                csv.append(invCard.getQuantity()).append(",");
                // Append the image URL if available
                String imageUrl = invCard.getCard().getImageUrl();
                csv.append(imageUrl != null ? imageUrl : "").append("\n");
            }
        }
        return csv.toString();
    }

    private void exportCollection() {
        // Assuming inventoryList is your list of InventoryCard objects
        String csvData = generateCsv(inventoryList);

        // Create a share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/csv"); // You can also use "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My MTG Collection");
        shareIntent.putExtra(Intent.EXTRA_TEXT, csvData);
        startActivity(Intent.createChooser(shareIntent, "Share your collection via"));
    }






}