package is.hbv501g.lootapp.api;

import is.hbv501g.lootapp.models.Card;
import is.hbv501g.lootapp.models.api.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    // Existing methods...
    @POST("users/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("users/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @GET("cards/search")
    Call<SearchResponse> searchCards(@Query("query") String query, @Query("page") int page);

    @GET("cards/cards/{cardId}")
    Call<Card> getCardById(@Path("cardId") String cardId);

    @GET("inventory")
    Call<InventoryResponse> getInventory();

    @POST("cards/add_card_to_inventory")
    Call<AddCardResponse> addCardToInventory(@Body AddCardRequest request);

    // New endpoints – note these map directly to your backend routes.
    @PUT("inventory/update_quantity")
    Call<UpdateQuantityResponse> updateCardQuantity(@Body UpdateQuantityRequest request);

    @DELETE("inventory/remove_card")
    Call<RemoveCardResponse> removeCard(@Query("cardId") String cardId);
}