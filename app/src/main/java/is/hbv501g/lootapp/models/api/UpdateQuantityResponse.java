package is.hbv501g.lootapp.models.api;

public class UpdateQuantityResponse {
    private boolean success;
    private String message;

    public boolean isSuccess() {
        return message != null && message.contains("updated successfully");
    }


    public String getMessage() {
        return message;
    }
}