package is.hbv501g.lootapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import is.hbv501g.lootapp.InventoryActivity;
import is.hbv501g.lootapp.R;
import is.hbv501g.lootapp.api.ApiClient;
import is.hbv501g.lootapp.models.InventoryCard;
import is.hbv501g.lootapp.models.api.RemoveCardRequest;
import is.hbv501g.lootapp.models.api.RemoveCardResponse;
import is.hbv501g.lootapp.models.api.UpdateQuantityRequest;
import is.hbv501g.lootapp.models.api.UpdateQuantityResponse;
import is.hbv501g.lootapp.util.FavoritesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<InventoryCard> inventoryList;
    private Context context;
    private FavoritesManager favoritesManager;

    public InventoryAdapter(List<InventoryCard> inventoryList, Context context) {
        this.inventoryList = inventoryList;
        this.context = context;
        // Initialize the FavoritesManager for local storage handling of favorite states.
        this.favoritesManager = new FavoritesManager(context);
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory_card, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryCard inventoryCard = inventoryList.get(position);

        // Ensure originalQuantity is set (for comparing changes)
        if (inventoryCard.getOriginalQuantity() == 0) {
            inventoryCard.setOriginalQuantity(inventoryCard.getQuantity());
        }

        // Bind basic data
        holder.textViewCardName.setText(inventoryCard.getCard().getName());
        holder.textViewCardType.setText(inventoryCard.getCard().getTypeLine());
        holder.textViewQuantity.setText("Qty: " + inventoryCard.getQuantity());

        // Load the card image using Glide
        if (inventoryCard.getCard().getImageUrl() != null && !inventoryCard.getCard().getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(inventoryCard.getCard().getImageUrl())
                    .placeholder(R.drawable.placeholder_card)
                    .into(holder.imageViewCard);
        } else {
            holder.imageViewCard.setImageResource(R.drawable.placeholder_card);
        }

        // Set the favorite star icon based on local storage
        if (favoritesManager.isFavorite(inventoryCard.getCard().getId())) {
            holder.buttonFavorite.setImageResource(R.drawable.ic_star_filled);
        } else {
            holder.buttonFavorite.setImageResource(R.drawable.ic_star_outline);
        }
        // Toggle favorite state on star button click
        holder.buttonFavorite.setOnClickListener(v -> {
            String cardId = inventoryCard.getCard().getId();
            if (favoritesManager.isFavorite(cardId)) {
                favoritesManager.removeFavorite();
            } else {
                favoritesManager.addFavorite(cardId);
            }
            notifyDataSetChanged();
        });

        // Initially hide the check button unless a change is made.
        updateCheckVisibility(holder, inventoryCard);

        // Plus button increases quantity.
        holder.buttonPlus.setOnClickListener(v -> {
            int newQuantity = inventoryCard.getQuantity() + 1;
            inventoryCard.setQuantity(newQuantity);
            holder.textViewQuantity.setText("Qty: " + newQuantity);
            updateCheckVisibility(holder, inventoryCard);
            Log.d("InventoryAdapter", "Increased quantity to " + newQuantity + " for " + inventoryCard.getCard().getName());
        });

        // Minus button decreases quantity if quantity > 0.
        holder.buttonMinus.setOnClickListener(v -> {
            int currentQuantity = inventoryCard.getQuantity();
            if (currentQuantity > 0) {
                int newQuantity = currentQuantity - 1;
                inventoryCard.setQuantity(newQuantity);
                holder.textViewQuantity.setText("Qty: " + newQuantity);
                updateCheckVisibility(holder, inventoryCard);
                Log.d("InventoryAdapter", "Decreased quantity to " + newQuantity + " for " + inventoryCard.getCard().getName());
            }
        });

        // Check button to confirm changes.
        holder.buttonCheck.setOnClickListener(v -> {
            int newQuantity = inventoryCard.getQuantity();
            if (newQuantity == 0) {
                // Send a backend call to remove the card.
                RemoveCardRequest req = new RemoveCardRequest(inventoryCard.getCard().getId());
                ApiClient.getApiService().removeCard(req).enqueue(new Callback<RemoveCardResponse>() {
                    @Override
                    public void onResponse(Call<RemoveCardResponse> call, Response<RemoveCardResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            int pos = holder.getAdapterPosition();
                            if (pos != RecyclerView.NO_POSITION) {
                                inventoryList.remove(pos);
                                notifyItemRemoved(pos);
                                Toast.makeText(context, "Card removed", Toast.LENGTH_SHORT).show();
                                ((InventoryActivity) context).updateTotalValue();
                            }
                        } else {
                            Toast.makeText(context, "Failed to remove card", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<RemoveCardResponse> call, Throwable t) {
                        Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Update card quantity via backend call.
                UpdateQuantityRequest req = new UpdateQuantityRequest(inventoryCard.getCard().getId(), newQuantity);
                ApiClient.getApiService().updateCardQuantity(req).enqueue(new Callback<UpdateQuantityResponse>() {
                    @Override
                    public void onResponse(Call<UpdateQuantityResponse> call, Response<UpdateQuantityResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(context, "Quantity updated", Toast.LENGTH_SHORT).show();
                            // Update originalQuantity to reflect the confirmed change.
                            inventoryCard.setOriginalQuantity(newQuantity);
                            holder.buttonCheck.setVisibility(View.GONE);
                            ((InventoryActivity) context).updateTotalValue();
                        } else {
                            Toast.makeText(context, "Failed to update quantity", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<UpdateQuantityResponse> call, Throwable t) {
                        Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * Show the check button only if the quantity has changed relative to the original.
     */
    private void updateCheckVisibility(InventoryViewHolder holder, InventoryCard inventoryCard) {
        if (inventoryCard.getQuantity() == inventoryCard.getOriginalQuantity()) {
            holder.buttonCheck.setVisibility(View.GONE);
        } else {
            holder.buttonCheck.setVisibility(View.VISIBLE);
            holder.buttonCheck.setEnabled(true);
        }
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCard;
        TextView textViewCardName;
        TextView textViewCardType;
        TextView textViewQuantity;
        Button buttonPlus, buttonMinus, buttonCheck;
        ImageButton buttonFavorite;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCard = itemView.findViewById(R.id.imageViewCard);
            textViewCardName = itemView.findViewById(R.id.textViewCardName);
            textViewCardType = itemView.findViewById(R.id.textViewCardType);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            buttonCheck = itemView.findViewById(R.id.buttonCheck);
            buttonFavorite = itemView.findViewById(R.id.buttonFavorite);
        }
    }
}
