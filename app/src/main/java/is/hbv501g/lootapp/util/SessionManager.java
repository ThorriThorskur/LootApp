package is.hbv501g.lootapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import is.hbv501g.lootapp.api.ApiClient;
import is.hbv501g.lootapp.models.api.RefreshRequest;
import is.hbv501g.lootapp.models.api.RefreshResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// SessionManager.java
public class SessionManager {
    private static final String PREF_NAME = "MagicInventoryPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_TOKEN_EXPIRY = "token_expiry";
    private static SharedPreferences sharedPreferences;
    private static SessionManager instance;

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveAuthToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();

        // Update API client
        ApiClient.setAuthToken(token);
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void saveAuthTokens(String token, String refreshToken, long expiryTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putLong(KEY_TOKEN_EXPIRY, expiryTime);
        editor.apply();
        ApiClient.setAuthToken(token);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public void refreshToken(Context context, final RefreshCallback callback) {
        String refreshToken = getRefreshToken();
        if (refreshToken == null) {
            callback.onRefreshFailed("No refresh token available");
            return;
        }

        // Create a Retrofit call to your refresh endpoint.
        // Assuming your ApiService has:
        // @POST("users/refresh_token")
        // Call<RefreshResponse> refreshToken(@Body RefreshRequest request);

        RefreshRequest req = new RefreshRequest(refreshToken);
        ApiClient.getApiService().refreshToken(req).enqueue(new Callback<RefreshResponse>() {
            @Override
            public void onResponse(Call<RefreshResponse> call, Response<RefreshResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Update tokens in SessionManager
                    String newToken = response.body().getToken();
                    String newRefreshToken = response.body().getRefreshToken();
                    long newExpiry = System.currentTimeMillis() + response.body().getExpiresIn();
                    saveAuthTokens(newToken, newRefreshToken, newExpiry);
                    callback.onRefreshSuccess(newToken);
                } else {
                    callback.onRefreshFailed("Failed to refresh token");
                }
            }
            @Override
            public void onFailure(Call<RefreshResponse> call, Throwable t) {
                callback.onRefreshFailed(t.getMessage());
            }
        });
    }

    // Define a callback interface:
    public interface RefreshCallback {
        void onRefreshSuccess(String newToken);
        void onRefreshFailed(String error);
    }


    public void saveUserDetails(int userId, String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getAuthToken() != null;
    }

    public void clearSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        ApiClient.setAuthToken(null);
    }

    public static void setDarkModeEnabled(boolean isDarkMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_DARK_MODE, isDarkMode);
        editor.apply();
    }

    public static boolean isDarkModeEnabled() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }
}
