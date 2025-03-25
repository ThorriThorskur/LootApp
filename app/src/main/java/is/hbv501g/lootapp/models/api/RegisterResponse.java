package is.hbv501g.lootapp.models.api;

import is.hbv501g.lootapp.models.User;

public class RegisterResponse {
    private String token;
    private User user;
    private String message;

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
