package is.hbv501g.lootapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemePreference {
    private static final String PREF_NAME = "theme_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    // Retrieve the dark mode setting; default is false (light mode)
    public static boolean isDarkModeEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    // Save the dark mode setting
    public static void setDarkModeEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_DARK_MODE, enabled);
        editor.apply();
    }
}
