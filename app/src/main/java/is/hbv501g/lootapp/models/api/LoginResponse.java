package is.hbv501g.lootapp.models.api;

import is.hbv501g.lootapp.models.User;

public class LoginResponse {
    private String message;
    private User user;
    private String token;

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}
