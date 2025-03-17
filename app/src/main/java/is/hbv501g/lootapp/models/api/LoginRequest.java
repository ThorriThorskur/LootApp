package is.hbv501g.lootapp.models.api;

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters required by Gson
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}