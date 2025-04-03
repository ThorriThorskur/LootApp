package is.hbv501g.lootapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import is.hbv501g.lootapp.adapter.InventoryAdapter;
import is.hbv501g.lootapp.models.Card;
import is.hbv501g.lootapp.models.InventoryCard;

public class ImportActivity extends AppCompatActivity {

    private EditText editTextCsvInput;
    private Button buttonImport;
    private RecyclerView recyclerViewImported;
    private InventoryAdapter importedAdapter;
    private List<InventoryCard> importedCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        editTextCsvInput = findViewById(R.id.editTextCsvInput);
        buttonImport = findViewById(R.id.buttonImport);
        recyclerViewImported = findViewById(R.id.recyclerViewImported);

        importedCards = new ArrayList<>();
        importedAdapter = new InventoryAdapter(importedCards, this);
        recyclerViewImported.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewImported.setAdapter(importedAdapter);
        ImageButton buttonHome = findViewById(R.id.buttonHome);

        // Home button
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Restart the DashboardActivity
                Intent intent = new Intent(ImportActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String csvData = editTextCsvInput.getText().toString().trim();
                if (csvData.isEmpty()) {
                    Toast.makeText(ImportActivity.this, "Please paste the CSV data", Toast.LENGTH_SHORT).show();
                    return;
                }
                importCollection(csvData);
            }
        });
    }

    /**
     * Parses the CSV string and updates the RecyclerView.
     * Adjust the CSV format according to your export format.
     */
    private void importCollection(String csvData) {
        // Clear any previous data
        importedCards.clear();

        // Split into lines (assuming each line is one card)
        String[] lines = csvData.split("\n");
        // Assume the first line is a header. If not, remove this line.
        int startIndex = 0;
        if (lines.length > 0 && lines[0].contains("Card Name")) {
            startIndex = 1;
        }

        for (int i = startIndex; i < lines.length; i++) {
            String line = lines[i];
            // Split by comma (adjust delimiter if necessary)
            String[] parts = line.split(",");
            // Assuming CSV columns: Card Name, Card Type, Quantity
            if (parts.length >= 4) {
                String cardName = parts[0].trim();
                String cardType = parts[1].trim();
                int quantity = 1;
                try {
                    quantity = Integer.parseInt(parts[2].trim());
                } catch (NumberFormatException e) {
                    // Default quantity = 1 if parsing fails
                }
                String imageUrl = parts[3].trim();

                // Create a dummy Card object
                Card card = new Card();
                card.setName(cardName);
                card.setTypeLine(cardType);
                card.setImageUrl(imageUrl); // You need to add this setter if not already present.

                // Create an InventoryCard from the Card object
                InventoryCard invCard = new InventoryCard(card, quantity);
                importedCards.add(invCard);
            }

        }
        if (importedCards.isEmpty()) {
            Toast.makeText(this, "No valid cards found in CSV", Toast.LENGTH_SHORT).show();
        } else {
            // Show the RecyclerView if not already visible
            recyclerViewImported.setVisibility(View.VISIBLE);
            importedAdapter.notifyDataSetChanged();
        }
    }
}
