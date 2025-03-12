package is.hbv501g.lootapp.models.api;

public class RegisterRequest {
    private String username;
    private String password;

    public RegisterRequest(String username, String password) {
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