package is.hbv501g.lootapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import is.hbv501g.lootapp.R;
import is.hbv501g.lootapp.api.ApiClient;
import is.hbv501g.lootapp.models.InventoryCard;
import is.hbv501g.lootapp.models.api.RemoveCardRequest;
import is.hbv501g.lootapp.models.api.RemoveCardResponse;
import is.hbv501g.lootapp.models.api.UpdateQuantityRequest;
import is.hbv501g.lootapp.models.api.UpdateQuantityResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<InventoryCard> inventoryList;
    private Context context;

    public InventoryAdapter(List<InventoryCard> inventoryList, Context context) {
        this.inventoryList = inventoryList;
        this.context = context;
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

        // If originalQuantity is not set (i.e., 0), initialize it.
        if (inventoryCard.getOriginalQuantity() == 0) {
            inventoryCard.setOriginalQuantity(inventoryCard.getQuantity());
        }

        holder.textViewCardName.setText(inventoryCard.getCard().getName());
        holder.textViewCardType.setText(inventoryCard.getCard().getTypeLine());
        holder.textViewQuantity.setText("Qty: " + inventoryCard.getQuantity());

        // Load card image if available.
        if (inventoryCard.getCard().getImageUrl() != null && !inventoryCard.getCard().getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(inventoryCard.getCard().getImageUrl())
                    .placeholder(R.drawable.placeholder_card)
                    .into(holder.imageViewCard);
        } else {
            holder.imageViewCard.setImageResource(R.drawable.placeholder_card);
        }

        // Initially, if no changes have been made, hide the check button.
        holder.buttonCheck.setVisibility(View.GONE);

        // Plus button
        holder.buttonPlus.setOnClickListener(v -> {
            int newQuantity = inventoryCard.getQuantity() + 1;
            inventoryCard.setQuantity(newQuantity);
            holder.textViewQuantity.setText("Qty: " + newQuantity);
            updateCheckVisibility(holder, inventoryCard);
            Log.d("InventoryAdapter", "Increased quantity to " + newQuantity + " for " + inventoryCard.getCard().getName());
        });

        // Minus button
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

        // Check button (confirm changes)
        holder.buttonCheck.setOnClickListener(v -> {
            int newQuantity = inventoryCard.getQuantity();
            if (newQuantity == 0) {
                // Remove card via backend call using a request body.
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
                // Update quantity via backend call.
                UpdateQuantityRequest req = new UpdateQuantityRequest(inventoryCard.getCard().getId(), newQuantity);
                ApiClient.getApiService().updateCardQuantity(req).enqueue(new Callback<UpdateQuantityResponse>() {
                    @Override
                    public void onResponse(Call<UpdateQuantityResponse> call, Response<UpdateQuantityResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(context, "Quantity updated", Toast.LENGTH_SHORT).show();
                            // Update originalQuantity to reflect confirmed change
                            inventoryCard.setOriginalQuantity(newQuantity);
                            holder.buttonCheck.setVisibility(View.GONE);
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

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCard = itemView.findViewById(R.id.imageViewCard);
            textViewCardName = itemView.findViewById(R.id.textViewCardName);
            textViewCardType = itemView.findViewById(R.id.textViewCardType);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            buttonCheck = itemView.findViewById(R.id.buttonCheck);
        }
    }
}
