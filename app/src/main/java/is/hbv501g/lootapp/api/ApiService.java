package is.hbv501g.lootapp.api;

// ApiService.java in new package: is.hbv501g.lootapp.api
public interface ApiService {
    // Authentication
    @POST("users/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("users/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    // Card Search
    @GET("cards/search")
    Call<SearchResponse> searchCards(@Query("query") String query, @Query("page") int page);

    @GET("cards/cards/{cardId}")
    Call<Card> getCardById(@Path("cardId") String cardId);

    // Inventory
    @GET("inventory")
    Call<InventoryResponse> getInventory();

    @POST("cards/add_card_to_inventory")
    Call<AddCardResponse> addCardToInventory(@Body AddCardRequest request);

    // More endpoints as needed...
}