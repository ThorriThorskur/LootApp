package is.hbv501g.lootapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
import retrofit2.Response;

//InventoryActivity.java
public class InventoryActivity extends AppCompatActivity {

    private InventoryAdapter inventoryAdapter;
    private List<InventoryCard> inventoryList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        Button buttonHome = findViewById(R.id.buttonHome);
        RecyclerView recyclerViewInventory = findViewById(R.id.recyclerViewInventory);
        progressBar = findViewById(R.id.progressBar);

        // Set up RecyclerView
        inventoryList = new ArrayList<>();
        inventoryAdapter = new InventoryAdapter(inventoryList, this);
        recyclerViewInventory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewInventory.setAdapter(inventoryAdapter);

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
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
}
