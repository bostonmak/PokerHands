
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import javafx.util.Pair;

public class PokerHands {

    private List<String> p1Hand;
    private List<String> p2Hand;
    private static PokerHands instance = null;

    public static PokerHands getInstance() {
        if (instance == null) {
            instance = new PokerHands();
        }
        return instance;
    }

    public void init() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to PokerHands by Boston Mak.");
        System.out.println("Cards should be formatted as <Value><Suit>, such as 'AS' for Ace of Spades or '9H' for 9 of Hearts.");
        System.out.println("Hands should be formatted as a comma-separated list of cards, such as 'AS, 9H, KS, 4D, 5C'.");

        boolean success = false;
        // Keep requesting a hand until one is valid
        while (!success) {
            System.out.println("Please enter the hand for Player 1:");
            String hand = scan.nextLine();
            p1Hand = Arrays.asList(hand.split(","));
            Pair<Boolean, String> result = checkHand(p1Hand);
            success = result.getKey();
            System.out.println(result.getValue());
        }

        success = false;
        while (!success) {
            System.out.println("Please enter the hand for Player 2:");
            String hand = scan.nextLine();
            p2Hand = Arrays.asList(hand.split(","));
            Pair<Boolean, String> result = checkHand(p2Hand);
            success = result.getKey();
            System.out.println(result.getValue());
        }
    }

    public Pair<Boolean, String> checkHand(List<String> hand) {
        if (hand.size() != 5) {
            return new Pair<>(false, String.format("Invalid hand size of %d\n", hand.size()));
        }
        for (int i = 0; i < hand.size(); ++i) {
            String card = hand.get(i).trim();
            Pair<Boolean, String> result = checkCard(card);
            if (!result.getKey()) {
                return result;
            }
        }
        return new Pair<>(true, "");
    }

    public Pair<Boolean, String> checkCard(String card) {
        // Check for proper card notation
        if (card.length() == 2 &&
                "23456789JKQA".indexOf(card.toUpperCase().charAt(0)) != -1 &&
                "DCHS".indexOf(card.toUpperCase().charAt(1)) != -1) {
            return new Pair<>(true, "");
        }
        return new Pair<>(false, String.format("Invalid card %s\n", card));
    }
}
