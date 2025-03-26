package is.hbv501g.lootapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton; // Change this import
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import is.hbv501g.lootapp.util.SessionManager;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Change type to ImageButton
        ImageButton buttonHome = findViewById(R.id.buttonHome);  // This now returns an ImageButton
        Button buttonSearch = findViewById(R.id.buttonSearch);
        Button buttonInventory = findViewById(R.id.buttonInventory);
        Button buttonScanner = findViewById(R.id.buttonScanner);
        Button buttonLogout = findViewById(R.id.buttonLogout);

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

        // Logout: clear session and return to LoginActivity
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear session data
                SessionManager.getInstance(DashboardActivity.this).clearSession();

                // Navigate to LoginActivity
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
