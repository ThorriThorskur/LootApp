package is.hbv501g.lootapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import is.hbv501g.lootapp.R;
import is.hbv501g.lootapp.models.InventoryCard;

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

        if (inventoryCard.getCard() != null) {
            holder.textViewCardName.setText(inventoryCard.getCard().getName());
            holder.textViewCardType.setText(inventoryCard.getCard().getTypeLine());
            holder.textViewQuantity.setText("Qty: " + inventoryCard.getQuantity());

            // Load card image if available
            if (inventoryCard.getCard().getImageUrl() != null && !inventoryCard.getCard().getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(inventoryCard.getCard().getImageUrl())
                        .placeholder(R.drawable.placeholder_card)
                        .into(holder.imageViewCard);
            } else {
                holder.imageViewCard.setImageResource(R.drawable.placeholder_card);
            }
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

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCard = itemView.findViewById(R.id.imageViewCard);
            textViewCardName = itemView.findViewById(R.id.textViewCardName);
            textViewCardType = itemView.findViewById(R.id.textViewCardType);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
        }
    }
}