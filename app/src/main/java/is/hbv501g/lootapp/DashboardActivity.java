package is.hbv501g.lootapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton; // Change this import
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private ImageButton buttonHome;  // Change type to ImageButton
    private Button buttonSearch, buttonInventory, buttonScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        buttonHome = findViewById(R.id.buttonHome);  // This now returns an ImageButton
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonInventory = findViewById(R.id.buttonInventory);
        buttonScanner = findViewById(R.id.buttonScanner);

        // Home button: since this is the dashboard, you might refresh or do nothing.
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Restart the DashboardActivity
                Intent intent = new Intent(DashboardActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Navigate to SearchActivity
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to InventoryActivity
        buttonInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, InventoryActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to ScannerActivity
        buttonScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, ScannerActivity.class);
                startActivity(intent);
            }
        });
    }
}
