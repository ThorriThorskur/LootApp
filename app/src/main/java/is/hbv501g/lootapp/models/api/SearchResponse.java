package is.hbv501g.lootapp.models.api;

import java.util.List;
import is.hbv501g.lootapp.models.Card;

public class SearchResponse {
    private List<Card> cards;
    private PaginationInfo pagination;

    public List<Card> getCards() {
        return cards;
    }

    public PaginationInfo getPagination() {
        return pagination;
    }

    public static class PaginationInfo {
        private int totalCards;
        private int totalPages;
        private int currentPage;
        private int cardsPerPage;

        // Getters
        public int getTotalCards() {
            return totalCards;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getCardsPerPage() {
            return cardsPerPage;
        }
    }
}