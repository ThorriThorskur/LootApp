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
import is.hbv501g.lootapp.models.Card;
import is.hbv501g.lootapp.models.InventoryCard;

public class ScanCardAdapter extends RecyclerView.Adapter<ScanCardAdapter.ScanCardViewHolder> {

    // FIXED: This should be a list of InventoryCard, not Card.
    private List<InventoryCard> scanList;
    private Context context;

    // FIXED: Match the type in the constructor param with the field type.
    public ScanCardAdapter(List<InventoryCard> scanList, Context context) {
        this.scanList = scanList;
        this.context = context;
    }

    @NonNull
    @Override
    public ScanCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scan_item_card, parent, false);
        return new ScanCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanCardViewHolder holder, int position) {
        // Get the InventoryCard at this position.
        InventoryCard invCard = scanList.get(position);
        // Get the actual Card object from the InventoryCard.
        Card card = invCard.getCard();

        // Populate views with card data.
        holder.textViewCardName.setText(card.getName());
        holder.textViewCardType.setText(card.getTypeLine());

        // Load the image if available, otherwise show placeholder.
        if (card.getImageUrl() != null && !card.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(card.getImageUrl())
                    .placeholder(R.drawable.placeholder_card)
                    .into(holder.imageViewCard);
        } else {
            holder.imageViewCard.setImageResource(R.drawable.placeholder_card);
        }

        // Show the current quantity from the InventoryCard.
        holder.textViewQuantity.setText(String.valueOf(invCard.getQuantity()));

        // Handle PLUS button logic.
        holder.buttonPlus.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION) {
                InventoryCard plusCard = scanList.get(adapterPos);
                plusCard.setQuantity(plusCard.getQuantity() + 1);
                // Update just this item’s display.
                notifyItemChanged(adapterPos);
            }
        });

        // Handle MINUS button logic.
        holder.buttonMinus.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION) {
                InventoryCard minusCard = scanList.get(adapterPos);
                int currentQuantity = minusCard.getQuantity();
                if (currentQuantity > 1) {
                    minusCard.setQuantity(currentQuantity - 1);
                    notifyItemChanged(adapterPos);
                } else {
                    // If quantity is 1, remove the card from the list entirely.
                    scanList.remove(adapterPos);
                    notifyItemRemoved(adapterPos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return scanList.size();
    }

    public static class ScanCardViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCard;
        TextView textViewCardName;
        TextView textViewCardType;
        TextView textViewQuantity;
        Button buttonPlus, buttonMinus;

        public ScanCardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCard = itemView.findViewById(R.id.imageViewCard);
            textViewCardName = itemView.findViewById(R.id.textViewCardName);
            textViewCardType = itemView.findViewById(R.id.textViewCardType);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
        }
    }
}
