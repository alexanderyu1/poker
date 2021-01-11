import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Poker {
    private final String[] SUITS = { "C", "D", "H", "S" };
    private final String[] RANKS = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K" };

    private final Player player;
    private List<Card> deck;
    private final Scanner in;

    public Poker() {
        this.player = new Player();
        this.in = new Scanner(System.in);
    }

    public void game() {
        shuffle();

        short chipsToBuy;
        do {
            System.out.println("\nYou have " + player.getChips() + " chip(s). How many chips would you like to buy?");
            try {
                chipsToBuy = in.nextShort();
            }
            catch (Exception e) {
                chipsToBuy = 0;
                in.next();
            }
        } while (chipsToBuy <= 0);
        in.nextLine();
        player.addChips(chipsToBuy);

        while (deck.size() >= 8 && player.getChips() > 0) {
            for (int i = 0; i < 5; i++) {
                player.deal(deck.get(0));
                deck.remove(0);
            }
            player.sortHand();

            takeTurn();

            player.clearHand();
            System.out.println("\n#####################");
            System.out.println("#### -NEXT HAND- ####");
            System.out.println("#####################");
        }

        endGame();
    }

    public void takeTurn() {
        int wager;
        do {
            System.out.println("\nYou currently have " + player.getChips() + " chip(s). How many will you wager? (Must wager at least 1, up to 25 chips)");
            try {
                wager = in.nextInt();
            }
            catch (Exception e) {
                wager = 0;
                in.next();
            }
            if (wager > player.getChips()) {
                System.out.println("You don't have that many chips.");
            }
        } while (wager <= 0 || wager > player.getChips() || wager > 25);
        in.nextLine();
        player.addChips(-1 * wager);

        System.out.print("\nYour hand: ");
        System.out.println(player.getHand());

        int cardsToTrade;
        do {
            System.out.println("\nHow many cards would you like to trade? (Any number from 0 - 3)");
            try {
                cardsToTrade = in.nextInt();
            }
            catch (Exception e) {
                cardsToTrade = -1;
                in.next();
            }
        } while (cardsToTrade < 0 || cardsToTrade > 3);
        in.nextLine();

        if (cardsToTrade > 0) System.out.println("\nWhich cards will you trade? (Choose " + cardsToTrade + ", choose the numbered position of the card, not the card itself)");

        int[] indexes = new int[cardsToTrade];
        for (int i = 0; i < cardsToTrade; i++) {
            indexes[i] = 0;
        }
        for (int i = 0; i < cardsToTrade; i++) {
            int indexPlusOne = -1;
            do {
                System.out.println("\nPick card " + (i + 1) + " / " + cardsToTrade + ".");
                try {
                    indexPlusOne = in.nextInt();
                }
                catch (Exception e) {
                    indexPlusOne = -1;
                    in.next();
                }
                finally {
                    for (int j = 0; j < cardsToTrade; j++) {
                        if (indexPlusOne == indexes[j]) {
                            indexPlusOne = 0;
                            break;
                        }
                    }
                    if (indexPlusOne > 0 && indexPlusOne <= player.getHand().size()) {
                        indexes[i] = indexPlusOne;
                        System.out.println("Removed " + player.getHand().get(indexPlusOne - 1).toString() + ".");
                    }
                }
            } while (indexPlusOne <= 0 || indexPlusOne > player.getHand().size());
            in.nextLine();
        }

        for (int i = 0; i < cardsToTrade; i++) {
            player.setCard(indexes[i] - 1, new Card("X", "X"));
            player.deal(deck.get(0));
            deck.remove(0);
        }
        for (int i = 0; i < player.getHand().size(); i++) {
            if (player.getHand().get(i).getRank().matches("X")) {
                player.removeCard(i);
                i = -1;
            }
        }

        player.sortHand();

        if (cardsToTrade > 0) {
            System.out.print("\nYour new hand: ");
            System.out.println(player.getHand());
        }

        int payOutMultiplier = player.evaluateHand();
        switch (payOutMultiplier) {
            case 100:
                System.out.println("\nThat's a Royal Flush! It's your lucky day! You win 100 times your original wager!");
                break;
            case 50:
                System.out.println("\nA Straight Flush! You win 50 times your original wager.");
                break;
            case 25:
                System.out.println("\nA Four of a Kind! You win 25 times your original wager.");
                break;
            case 15:
                System.out.println("\nA Full House! You win 15 times your original wager.");
                break;
            case 10:
                System.out.println("\nA Flush! You win 10 times your original wager.");
                break;
            case 5:
                System.out.println("\nA Straight. You win 5 times your original wager.");
                break;
            case 3:
                System.out.println("\nA Three of a Kind. You win 3 times your original wager.");
                break;
            case 2:
                System.out.println("\nA Two Pair. You win 2 times your original wager.");
                break;
            case 1:
                System.out.println("\nA Pair of Jacks or Greater. You break even.");
                break;
            case 0:
                System.out.println("\nLooks like you got a bad hand there... No money for you.");
                break;
        }
        player.addChips(payOutMultiplier * wager);
    }

    public void shuffle() {
        deck = new ArrayList<>(52);

        for (String suit : SUITS) {
            for (String rank : RANKS) {
                deck.add(new Card(rank, suit));
            }
        }

        Collections.shuffle(deck);
    }

    public void endGame() {
        String endMessage = (player.getChips() == 0) ? "\nYou lost all your chips. Looks like you can't play anymore." : "\nThe deck is empty! The game has ended.";
        System.out.println(endMessage);

        System.out.println("\nYou ended with " + player.getChips() + " chips.");

        player.clearHand();
        String playAgain;
        do {
            System.out.println("\nPlay Again? (Yes or No)");
            playAgain = in.nextLine().toLowerCase();
        } while (!playAgain.equals("yes") && !playAgain.equals("no"));
        if (playAgain.equals("yes")) {
            System.out.println("\nReshuffling Deck...");
            game();
        }
        else {
            in.close();
        }
    }

    public static void main(String[] args) {
        System.out.println("You will face against a CPU player in the card game poker. Good luck!");
        new Poker().game();
    }
}