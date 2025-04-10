package is.hbv501g.lootapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import is.hbv501g.lootapp.R;
import is.hbv501g.lootapp.models.WishlistCard;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private List<WishlistCard> wishlist;
    private Context context;

    public WishlistAdapter(List<WishlistCard> wishlist, Context context) {
        this.wishlist = wishlist;
        this.context = context;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        WishlistCard wishlistCard = wishlist.get(position);
        // Use wishlistCard.getCard() to get card details
        if (wishlistCard.getCard() != null) {
            holder.textViewCardName.setText(wishlistCard.getCard().getName());
            holder.textViewCardType.setText(wishlistCard.getCard().getTypeLine());

            if (wishlistCard.getCard().getImageUrl() != null && !wishlistCard.getCard().getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(wishlistCard.getCard().getImageUrl())
                        .placeholder(R.drawable.placeholder_card)
                        .into(holder.imageViewCard);
            } else {
                holder.imageViewCard.setImageResource(R.drawable.placeholder_card);
            }
        }
    }

    @Override
    public int getItemCount() {
        return wishlist.size();
    }

    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCard;
        TextView textViewCardName;
        TextView textViewCardType;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCard = itemView.findViewById(R.id.imageViewCard);
            textViewCardName = itemView.findViewById(R.id.textViewCardName);
            textViewCardType = itemView.findViewById(R.id.textViewCardType);
        }
    }
}