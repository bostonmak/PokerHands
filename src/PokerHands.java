
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import javafx.util.Pair;


public class PokerHands {

    private List<String> p1Hand;
    private List<String> p2Hand;
    private static PokerHands instance = null;
    private List<String> validValues = new ArrayList<>(
                Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"));
    private List<String> validSuits = new ArrayList<>(Arrays.asList("D", "C", "H", "S"));

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
            String card = hand.get(i).trim().toUpperCase();
            Pair<Boolean, String> result = checkCard(card);
            hand.set(i, card);
            if (!result.getKey()) {
                return result;
            }
        }
        return new Pair<>(true, "");
    }

    public Pair<Boolean, String> checkCard(String card) {
        // Check for proper card notation
        if ((validValues.contains(card.substring(0, card.length() - 1)) &&
                validSuits.contains(card.substring(card.length() - 1)))) {
            return new Pair<>(true, "");
        }
        return new Pair<>(false, String.format("Invalid card %s\n", card));
    }

    public Hand calculateHand(List<String> hand) {
        Map values = new HashMap<String, Integer>();
        Map suits = new HashMap<String, Integer>();
        for (int i = 0; i < hand.size(); ++i) {
            String card = hand.get(i);
            String value = card.substring(0, card.length() - 1);
            String suit = card.substring(card.length() - 1);
            if (values.containsKey(value)) {
                values.put(value, ((Integer)values.get(value)) + 1);
            }
            else {
                values.put(value, 1);
            }
            if (suits.containsKey(suit)) {
                suits.put(suit, ((Integer)suits.get(suit)) + 1);
            }
            else {
                suits.put(suit, 1);
            }
        }

        return calculateHandFromCards(values, suits);
    }

    public Hand calculateHandFromCards(Map<String, Integer> values, Map<String, Integer> suits) {
        Hand hand = new Hand();
        // Check matching values
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            // Check four of a kind
            if (entry.getValue() == 4) {
                hand.quadValue = entry.getKey();
                hand.type = HandTypes.FOUR_OF_A_KIND;
            }
            // Check triples and full house
            else if (entry.getValue() == 3) {
                hand.tripValue = entry.getKey();
                if (hand.pair1Value.equals("")) {
                    hand.type = HandTypes.THREE_OF_A_KIND;
                }
                else {
                    hand.type = HandTypes.FULL_HOUSE;
                }
            }
            // Check pairs
            else if (entry.getValue() == 2) {
                if (hand.pair1Value.equals("")) {
                    hand.pair1Value = entry.getKey();
                    if (hand.type.ordinal() < HandTypes.ONE_PAIR.ordinal()) {
                        hand.type = HandTypes.ONE_PAIR;
                    }
                }
                else {
                    hand.pair2Value = entry.getKey();
                    hand.type = HandTypes.TWO_PAIR;
                }
            }
            // Check high card
            else if (values.get(hand.highCardValue) < values.get(entry.getKey())) {
                hand.highCardValue = entry.getKey();
            }
        }

        // Check hands based on suits


        return result;
    }
}
