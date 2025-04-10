package is.hbv501g.lootapp.models.api;

public class RefreshResponse {
    private boolean success;
    private String token;
    private String refreshToken;
    private long expiresIn; // typically in seconds

    public boolean isSuccess() {
        return success;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}
