package is.hbv501g.lootapp.adapter;

// CardAdapter.java
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<Card> cardList;
    private Context context;
    private OnCardClickListener listener;

    public interface OnCardClickListener {
        void onCardClick(Card card);
        void onAddToInventoryClick(Card card);
    }

    public CardAdapter(List<Card> cardList, Context context) {
        this.cardList = cardList;
        this.context = context;

        // If context implements the listener, set it up
        if (context instanceof OnCardClickListener) {
            this.listener = (OnCardClickListener) context;
        }
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cardList.get(position);

        holder.textViewCardName.setText(card.getName());
        holder.textViewCardType.setText(card.getTypeLine());

        // Load card image if available
        if (card.getImageUrl() != null && !card.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(card.getImageUrl())
                    .placeholder(R.drawable.placeholder_card)
                    .into(holder.imageViewCard);
        } else {
            holder.imageViewCard.setImageResource(R.drawable.placeholder_card);
        }

        // Set up click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCardClick(card);
            }
        });

        holder.buttonAddToInventory.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddToInventoryClick(card);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCard;
        TextView textViewCardName;
        TextView textViewCardType;
        Button buttonAddToInventory;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCard = itemView.findViewById(R.id.imageViewCard);
            textViewCardName = itemView.findViewById(R.id.textViewCardName);
            textViewCardType = itemView.findViewById(R.id.textViewCardType);
            buttonAddToInventory = itemView.findViewById(R.id.buttonAddToInventory);
        }
    }
}