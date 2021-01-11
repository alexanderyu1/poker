public class Card {

    private final String rank;
    private final String suit;

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return rank + suit;     // allows for easy printing of a card
    }

    public static int getOrderedRank(String rank) {
        try {
            return Integer.parseInt(rank);
        } catch (NumberFormatException e) {
            switch (rank) {
                case "T": return 10;    // for 10s, Jacks, Queens, Kings,
                case "J": return 11;    // and Aces, we need to apply a
                case "Q": return 12;    // numeric value to simplify the
                case "K": return 13;    // sorting of hands and books.
                case "A": return 14;
            }
        }

        return -1;
    }

    public static int getOrderedSuit(String suit) {
        switch (suit) {
            case "C": return 1;     // we give each suit a numeric
            case "D": return 2;     // value to simplify the sorting
            case "H": return 3;     // of hands.
            case "S": return 4;
        }

        return -1;
    }
}