
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import javafx.util.Pair;


public class PokerHands {

    private List<String> p1Hand;
    private List<String> p2Hand;
    private static PokerHands instance = null;
    private List<String> validValues = new ArrayList<>(
                Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"));
    private List<String> validSuits = new ArrayList<>(Arrays.asList("D", "C", "H", "S"));

    private final int HAND_SIZE = 5;

    public static PokerHands getInstance() {
        if (instance == null) {
            instance = new PokerHands();
        }
        return instance;
    }

    public void run() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to PokerHands by Boston Mak.");
        System.out.println("Cards should be formatted as <Value><Suit>, such as 'AS' for Ace of Spades or '9H' for 9 of Hearts.");
        System.out.println("Hands should be formatted as a comma-separated list of cards, such as 'AS, 9H, KS, 4D, 5C'.");

        Hand hand1 = new Hand();
        Hand hand2 = new Hand();

        while(true) {
            // Keep polling until proper hands are provided
            while (!poll(scan, hand1, hand2)) ;

            calculateHand(hand1);
            calculateHand(hand2);

            System.out.println(compareHands(hand1, hand2));

            System.out.print("Continue? (y/n): ");
            String in = scan.nextLine();
            if (in.toUpperCase().equals("Y")) continue;
            else break;
        }
    }

    public boolean poll(Scanner scan, Hand p1Hand, Hand p2Hand) {
        boolean success = false;
        // Keep requesting a hand until one is valid
        while (!success) {
            System.out.println("Please enter the hand for Player 1:");
            String hand = scan.nextLine();
            p1Hand.hand = Arrays.asList(hand.split(","));
            Pair<Boolean, String> result = checkHand(p1Hand);
            success = result.getKey();
            if (!success) {
                System.out.println(result.getValue());
                continue;
            }
            result = checkDupesInHand(p1Hand);
            success = result.getKey();
            System.out.println(result.getValue());
        }

        success = false;
        while (!success) {
            System.out.println("Please enter the hand for Player 2:");
            String hand = scan.nextLine();
            p2Hand.hand = Arrays.asList(hand.split(","));
            Pair<Boolean, String> result = checkHand(p2Hand);
            success = result.getKey();
            if (!success) {
                System.out.println(result.getValue());
                continue;
            }
            result = checkDupesInHand(p2Hand);
            success = result.getKey();
            System.out.println(result.getValue());
        }

        Pair<Boolean, String> dupes = checkDupes(p1Hand, p2Hand);
        if (!checkDupes(p1Hand, p2Hand).getKey()) {
            System.out.println(dupes.getValue());
            return false;
        }
        return true;
    }

    public int valueOf(String value) {
        return validValues.indexOf(value);
    }

    public Pair<Boolean, String> checkHand(Hand hand) {
        if (hand.hand.size() != HAND_SIZE) {
            return new Pair<>(false, String.format("Invalid hand size of %d\n", hand.hand.size()));
        }
        for (int i = 0; i < hand.hand.size(); ++i) {
            String card = hand.hand.get(i).trim().toUpperCase();
            Pair<Boolean, String> result = checkCard(card);
            hand.hand.set(i, card);
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

    public Pair<Boolean, String> checkDupes(Hand hand1, Hand hand2) {
        for (String card : hand1.hand) {
            if (hand2.hand.indexOf(card) != -1) {
                return new Pair<>(false, String.format("Duplicate card %s\n", card));
            }
        }
        return new Pair<>(true, "");
    }

    public Pair<Boolean, String> checkDupesInHand(Hand hand) {
        Set set = new HashSet(hand.hand);
        if (set.size() != HAND_SIZE) {
            return new Pair<>(false, "Duplicate cards in hand are not allowed");
        }
        return new Pair<>(true, "");
    }

    public void calculateHand(Hand hand) {
        Map values = new HashMap<String, Integer>();
        String suit = "";
        boolean suited = true;
        for (int i = 0; i < hand.hand.size(); ++i) {
            String card = hand.hand.get(i);
            String value = card.substring(0, card.length() - 1);
            String cardSuit = card.substring(card.length() - 1);
            if (values.containsKey(value)) {
                values.put(value, ((Integer)values.get(value)) + 1);
            }
            else {
                values.put(value, 1);
            }
            if (suit.equals("")) {
                suit = cardSuit;
            }
            else if (!suit.equals(cardSuit)){
                suited = false;
            }
        }

        calculateHandFromCards(hand, values, suited);
    }

    public Hand calculateHandFromCards(Hand hand, Map<String, Integer> values, boolean suited) {
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
                    if (!hand.tripValue.equals("")) {
                        hand.type = HandTypes.FULL_HOUSE;
                    }
                    else if (hand.type.ordinal() < HandTypes.ONE_PAIR.ordinal()) {
                        hand.type = HandTypes.ONE_PAIR;
                    }
                }
                else {
                    hand.pair2Value = entry.getKey();
                    hand.type = HandTypes.TWO_PAIR;
                }
            }
            // Check high card
            else if (valueOf(hand.highCardValue) < valueOf(entry.getKey())) {
                hand.highCardValue = entry.getKey();
            }
        }

        // Check straight
        boolean isStraight = false;
        if (values.keySet().size() == HAND_SIZE) {
            isStraight = true;
            TreeSet<String> keySet = new TreeSet<>(values.keySet());
            int prevIndex = -1;
            for (String value : keySet) {
                if (prevIndex == -1 || prevIndex + 1 == valueOf(value)) {
                    prevIndex = valueOf(value);
                }
                else {
                    isStraight = false;
                    break;
                }
            }
            if (isStraight) {
                hand.type = HandTypes.STRAIGHT;
            }
        }

        // Check flush
        if (suited && isStraight) {
            hand.type = HandTypes.STRAIGHT_FLUSH;
        }
        else if (suited) {
            hand.type = HandTypes.FLUSH;
        }

        return hand;
    }

    public String compareHands(Hand h1, Hand h2) {
        if (h1.type.ordinal() > h2.type.ordinal()) {
            String win = h1.type.name().replace("_", " ");
            return String.format("Player 1 wins with a %s!\n", win);
        }
        else if (h1.type.ordinal() < h2.type.ordinal()) {
            String win = h2.type.name().replace("_", " ");
            return String.format("Player 2 wins with a %s!\n", win);
        }
        else {
            if (h1.type == HandTypes.STRAIGHT_FLUSH) {
                if (valueOf(h1.highCardValue) > valueOf(h2.highCardValue)) {
                    String win = h1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a %s-high %s!\n", h1.highCardValue, win);
                }
                else if (valueOf(h1.highCardValue) < valueOf(h2.highCardValue)) {
                    String win = h2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a %s-high %s!\n", h2.highCardValue, win);
                }
            }
            else if (h1.type == HandTypes.FOUR_OF_A_KIND) {
                if (valueOf(h1.quadValue) > valueOf(h2.quadValue)) {
                    String win = h1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a %s %s!\n", h1.quadValue, win);
                }
                else {
                    String win = h2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a %s %s!\n", h2.quadValue, win);
                }
            }
            else if (h1.type == HandTypes.FULL_HOUSE) {
                if (valueOf(h1.tripValue) > valueOf(h2.tripValue)) {
                    String win = h1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a stronger %s!\n", win);
                }
                else if (valueOf(h1.tripValue) < valueOf(h2.tripValue)) {
                    String win = h2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a stronger %s!\n", win);
                }
                else if (valueOf(h1.tripValue) == valueOf(h2.tripValue) &&
                         valueOf(h1.pair1Value) > valueOf(h2.pair1Value)) {
                    String win = h1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a stronger %s!\n", win);
                }
                else if (valueOf(h1.tripValue) == valueOf(h2.tripValue) &&
                        valueOf(h1.pair1Value) < valueOf(h2.pair1Value)) {
                    String win = h2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a stronger %s!\n", win);
                }
            }
            else if (h1.type == HandTypes.FLUSH) {
                // Compare each card from high to low, hand should already be sorted from low to high
                for (int i = h1.hand.size() - 1; i >= 0; --i) {
                    if (valueOf(h1.hand.get(i)) > valueOf(h2.hand.get(i))) {
                        String win = h1.type.name().replace("_", " ");
                        return String.format("Player 1 wins with a stronger %s!\n", win);
                    }
                    else if (valueOf(h1.hand.get(i)) < valueOf(h2.hand.get(i))) {
                        String win = h2.type.name().replace("_", " ");
                        return String.format("Player 2 wins with a stronger %s!\n", win);
                    }
                }
            }
            else if (h1.type == HandTypes.STRAIGHT) {
                if (valueOf(h1.highCardValue) > valueOf(h2.highCardValue)) {
                    String win = h1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a %s-high %s!\n", h1.highCardValue, win);
                }
                else if (valueOf(h1.highCardValue) < valueOf(h2.highCardValue)) {
                    String win = h2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a stronger %s!\n", h2.highCardValue, win);
                }
            }
            else if (h1.type == HandTypes.THREE_OF_A_KIND) {
                if (valueOf(h1.tripValue) > valueOf(h2.tripValue)) {
                    String win = h1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a %s, %ss!\n", win, h1.tripValue);
                }
                else if (valueOf(h1.tripValue) < valueOf(h2.tripValue)) {
                    String win = h2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a %s, %ss!\n", win, h2.tripValue);
                }
                else {
                    // Comparing hands from high to low will determine who has higher matched cards
                    for (int i = h1.hand.size() - 1; i >= 0; --i) {
                        if (valueOf(h1.hand.get(i)) > valueOf(h2.hand.get(i))) {
                            String win = h1.type.name().replace("_", " ");
                            return String.format("Player 1 wins with a stronger %s, %ss!\n", win, h1.tripValue);
                        }
                        else if (valueOf(h1.hand.get(i)) < valueOf(h2.hand.get(i))) {
                            String win = h2.type.name().replace("_", " ");
                            return String.format("Player 2 wins with a stronger %s, %ss!\n", win, h2.tripValue);
                        }
                    }
                }
            }
            else if (h1.type == HandTypes.TWO_PAIR) {
                int h1High, h1Low, h2High, h2Low;
                int h1p1v = valueOf(h1.pair1Value);
                int h1p2v = valueOf(h1.pair2Value);
                int h2p1v = valueOf(h2.pair1Value);
                int h2p2v = valueOf(h2.pair2Value);
                if (h1p1v > h1p2v) {
                    h1High = h1p1v;
                    h1Low = h1p2v;
                }
                else {
                    h1High = h1p2v;
                    h1Low = h1p1v;
                }
                if (h2p1v > h2p2v) {
                    h2High = h2p1v;
                    h2Low = h2p2v;
                }
                else {
                    h2High = h2p2v;
                    h2Low = h2p1v;
                }

                if (h1High > h2High) {
                    String win = h1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a stronger %s!\n", win);
                }
                else if (h1High < h2High) {
                    String win = h2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a stronger %s!\n", win);
                }
                else {
                    if (h1Low > h2Low) {
                        String win = h1.type.name().replace("_", " ");
                        return String.format("Player 1 wins with a stronger %s!\n", win);
                    }
                    else if (h1Low < h2Low) {
                        String win = h2.type.name().replace("_", " ");
                        return String.format("Player 2 wins with a stronger %s!\n", win);
                    }
                    else {
                        if (valueOf(h1.highCardValue) > valueOf(h2.highCardValue)) {
                            String win = h1.type.name().replace("_", " ");
                            return String.format("Player 1 wins with a stronger %s!\n", win);
                        }
                        else if (valueOf(h1.highCardValue) < valueOf(h2.highCardValue)) {
                            String win = h2.type.name().replace("_", " ");
                            return String.format("Player 2 wins with a stronger %s!\n", win);
                        }
                    }
                }
            }
            else if (h1.type == HandTypes.ONE_PAIR) {
                if (valueOf(h1.pair1Value) > valueOf(h2.pair1Value)) {
                    String win = h1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a %s of %s!\n", win, h1.pair1Value);
                }
                else if (valueOf(h1.pair1Value) < valueOf(h2.pair1Value)) {
                    String win = h2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a %s of %s!\n", win, h2.pair1Value);
                }
                else {
                    for (int i = h1.hand.size() - 1; i >= 0; --i) {
                        if (valueOf(h1.hand.get(i)) > valueOf(h2.hand.get(i))) {
                            String win = h1.type.name().replace("_", " ");
                            return String.format("Player 1 wins with a stronger %s of %s!\n", win, h1.pair1Value);
                        }
                        else if (valueOf(h1.hand.get(i)) < valueOf(h2.hand.get(i))) {
                            String win = h2.type.name().replace("_", " ");
                            return String.format("Player 2 wins with a stronger %s or %s!\n", win, h2.pair1Value);
                        }
                    }
                }
            }
            else if (h1.type == HandTypes.HIGH_CARD) {
                for (int i = h1.hand.size(); i >= 0; --i) {
                    if (valueOf(h1.hand.get(i)) > valueOf(h2.hand.get(i))) {
                        return String.format("Player 1 wins with a stronger card of %s!\n", h1.hand.get(i));
                    }
                    else if (valueOf(h1.hand.get(i)) < valueOf(h2.hand.get(i))) {
                        return String.format("Player 2 wins with a stronger card of %s!\n", h2.hand.get(i));
                    }
                }
            }
            else {
                String win = h1.type.name().replace("_", " ");
                return String.format("Invalid hand type found: %s", win);
            }
        }
        return "Player 1 and Player 2 have tied!";
    }
}
