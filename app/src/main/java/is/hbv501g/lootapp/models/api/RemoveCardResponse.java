package is.hbv501g.lootapp.models.api;

public class RemoveCardResponse {
    private boolean success;
    private String message;

    public boolean isSuccess() {
        return message != null && message.contains("Card removed from inventory successfully");
    }

    public String getMessage() {
        return message;
    }
}