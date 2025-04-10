package is.hbv501g.lootapp.adapter;

import android.content.Context;
import android.util.Log;
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
import is.hbv501g.lootapp.models.WishlistCard;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {
    private List<WishlistCard> wishlistList;
    private Context context;
    private OnWishlistActionListener listener;

    public interface OnWishlistActionListener {
        void onRemoveFromWishlist(WishlistCard wishlistCard);
    }

    public WishlistAdapter(List<WishlistCard> wishlistList, Context context) {
        this.wishlistList = wishlistList;
        this.context = context;
        if (context instanceof OnWishlistActionListener) {
            this.listener = (OnWishlistActionListener) context;
        }
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist_card, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        WishlistCard wishlistCard = wishlistList.get(position);

        // Bind other data (card name, type, image, etc.)
        holder.textViewCardName.setText(wishlistCard.getCard().getName());
        holder.textViewCardType.setText(wishlistCard.getCard().getTypeLine());

        // Load image if available (assuming card holds the image)
        // For example, using Glide:
        Glide.with(context)
                .load(wishlistCard.getCard().getImageUrl())
                .placeholder(R.drawable.placeholder_card)
                .into(holder.imageViewCard);

        // Set up the remove button listener
        holder.buttonRemoveFromWishlist.setOnClickListener(v -> {
            // Optional: add a log to see if the click is registered
            Log.d("WishlistAdapter", "Remove button clicked for cardId: " + wishlistCard.getCardId());
            if (listener != null) {
                listener.onRemoveFromWishlist(wishlistCard);
            }
        });
    }


    @Override
    public int getItemCount() {
        return wishlistList.size();
    }

    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCard;
        TextView textViewCardName;
        TextView textViewCardType;
        Button buttonRemoveFromWishlist;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCard = itemView.findViewById(R.id.imageViewCard);
            textViewCardName = itemView.findViewById(R.id.textViewCardName);
            textViewCardType = itemView.findViewById(R.id.textViewCardType);
            buttonRemoveFromWishlist = itemView.findViewById(R.id.buttonRemoveFromWishlist);
        }
    }
}
