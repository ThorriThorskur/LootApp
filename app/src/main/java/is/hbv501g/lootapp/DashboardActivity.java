package is.hbv501g.lootapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import is.hbv501g.lootapp.util.SessionManager;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Change type to ImageButton
        ImageButton buttonHome = findViewById(R.id.buttonHome);
        ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        Button buttonSearch = findViewById(R.id.buttonSearch);
        Button buttonInventory = findViewById(R.id.buttonInventory);
        Button buttonScanner = findViewById(R.id.buttonScanner);
        Button buttonImport = findViewById(R.id.buttonImport);
        Button buttonWishlist = findViewById(R.id.buttonWishlist);

        // Home button
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Restart the DashboardActivity
                Intent intent = new Intent(DashboardActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Settings button
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, settingsActivity.class);
                startActivity(intent);
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

        // Navigate to Wishlist
        buttonWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, WishlistActivity.class);
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


        buttonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, ImportActivity.class);
                startActivity(intent);
            }
        });

    }
}
