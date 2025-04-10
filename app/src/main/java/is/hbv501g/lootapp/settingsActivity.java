package is.hbv501g.lootapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import is.hbv501g.lootapp.util.SessionManager;
import is.hbv501g.lootapp.util.ThemePreference;  // Import your new helper

public class settingsActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply the saved theme before setting the layout
        if (ThemePreference.isDarkModeEnabled(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sessionManager = SessionManager.getInstance(this);

        // Back button to return to dashboard
        ImageButton buttonBackToDashboard = findViewById(R.id.buttonBackToDashboard);
        buttonBackToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Log out button
        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.clearSession();
                Intent intent = new Intent(settingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Dark Mode switch
        Switch switchDarkMode = findViewById(R.id.switchDarkMode);
        // Set switch state based on saved preference
        boolean isDarkMode = ThemePreference.isDarkModeEnabled(this);
        switchDarkMode.setChecked(isDarkMode);

        // Listen for changes on the switch
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the new setting in SharedPreferences
            ThemePreference.setDarkModeEnabled(this, isChecked);

            // Apply the chosen mode
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            // Optionally, recreate the activity to immediately reflect the change
            recreate();
        });
    }
}
