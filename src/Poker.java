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
            System.out.println();
            System.out.println("------NEXT HAND------");
            System.out.println();
        }

        endGame();
    }

    public void takeTurn() {
        int wager;
        do {
            System.out.println("\nYou have " + player.getChips() + " chip(s). How many will you wager? (Must wager 1-25 chips)");
            try {
                wager = in.nextInt();
            }
            catch (Exception e) {
                wager = 0;
                in.next();
            }
            if (wager > player.getChips()) {
                System.out.println("You don't have enough chips.");
            }
        } while (wager <= 0 || wager > player.getChips() || wager > 25);
        in.nextLine();
        player.addChips(-1 * wager);

        System.out.print("\nYour hand: ");
        System.out.println(player.getPlayerHand());

        int cardsToTrade;
        do {
            System.out.println("\nHow many cards would you like to trade? (0-3)");
            try {
                cardsToTrade = in.nextInt();
            }
            catch (Exception e) {
                cardsToTrade = -1;
                in.next();
            }
        } while (cardsToTrade < 0 || cardsToTrade > 3);
        in.nextLine();

        if (cardsToTrade > 0) System.out.println("\nWhich cards will you trade? (Choose " + cardsToTrade + ", choose number of the card, not card itself)");

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
                    if (indexPlusOne > 0 && indexPlusOne <= player.getPlayerHand().size()) {
                        indexes[i] = indexPlusOne;
                        System.out.println("Removed " + player.getPlayerHand().get(indexPlusOne - 1).toString() + ".");
                    }
                }
            } while (indexPlusOne <= 0 || indexPlusOne > player.getPlayerHand().size());
            in.nextLine();
        }

        for (int i = 0; i < cardsToTrade; i++) {
            player.setCard(indexes[i] - 1, new Card("X", "X"));
            player.deal(deck.get(0));
            deck.remove(0);
        }
        for (int i = 0; i < player.getPlayerHand().size(); i++) {
            if (player.getPlayerHand().get(i).getRank().matches("X")) {
                player.removeCard(i);
                i = -1;
            }
        }

        player.sortHand();

        if (cardsToTrade > 0) {
            System.out.print("\nYour new hand: ");
            System.out.println(player.getPlayerHand());
        }

        int payOutMultiplier = player.evaluateHand();
        switch (payOutMultiplier) {
            case 100:
                System.out.println("\nRoyal Flush! You win 100 times your wager.");
                break;
            case 50:
                System.out.println("\nStraight Flush. You win 50 times your wager.");
                break;
            case 25:
                System.out.println("\nFour of a Kind. You win 25 times your wager.");
                break;
            case 15:
                System.out.println("\nFull House. You win 15 times your wager.");
                break;
            case 10:
                System.out.println("\nFlush. You win 10 times your wager.");
                break;
            case 5:
                System.out.println("\nStraight. You win 5 times your wager.");
                break;
            case 3:
                System.out.println("\nThree of a Kind. You win 3 times your wager.");
                break;
            case 2:
                System.out.println("\nTwo Pair. You win 2 times your wager.");
                break;
            case 1:
                System.out.println("\nAPair of Jacks or Greater. Nothing gained or lost.");
                break;
            case 0:
                System.out.println("\nBad hand, no money gotten.");
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
        String endMessage = (player.getChips() == 0) ? "\nYou lost all your chips, so you're out of the game." : "\nNo more cards in deck, game ends.";
        System.out.println(endMessage);

        System.out.println("\nYou ended with " + player.getChips() + " chips.");

        player.clearHand();
        String playAgain;
        do {
            System.out.println("\nPlay Again? (y/n)");
            playAgain = in.nextLine().toLowerCase();
        } while (!playAgain.equals("y") && !playAgain.equals("n"));
        if (playAgain.equals("y")) {
            System.out.println("\nReshuffling Deck...");
            game();
        }
        else {
            in.close();
        }
    }

    public static void main(String[] args) {
        System.out.println("You will test your luck and skill in the card game poker. have fun!");
        new Poker().game();
    }
}