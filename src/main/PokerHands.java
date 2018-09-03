package main;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import javafx.util.Pair;


public class PokerHands {

    private Hand hand1;
    private Hand hand2;
    private static PokerHands instance = null;
    private List<String> validValues;
    private List<String> validSuits;

    private final int HAND_SIZE = 5;

    private PokerHands() {
        validValues = new ArrayList<>(Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"));
        validSuits = new ArrayList<>(Arrays.asList("D", "C", "H", "S"));
    }

    // auto-generated getters/setters
    public List<String> getValidValues() {
        return validValues;
    }

    public List<String> getValidSuits() {
        return validSuits;
    }

    public void setHand1(Hand hand1) {
        this.hand1 = hand1;
    }

    public void setHand2(Hand hand2) {
        this.hand2 = hand2;
    }

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

        while(true) {
            hand1 = new Hand();
            hand2 = new Hand();
            // Keep polling until proper hands are provided
            while (!poll(scan)) ;

            calculateHand(hand1);
            calculateHand(hand2);

            System.out.println(compareHands());

            System.out.print("Continue? (y/n): ");
            String in = scan.nextLine();
            if (in.toUpperCase().equals("Y")) continue;
            else break;
        }
    }

    public boolean poll(Scanner scan) {
        boolean success = false;
        // Keep requesting a hand until one is valid
        while (!success) {
            System.out.println("Please enter the hand for Player 1:");
            String hand = scan.nextLine();
            hand1.hand = Arrays.asList(hand.split(","));
            Pair<Boolean, String> result = checkHand(hand1);
            success = result.getKey();
            if (!success) {
                System.out.println(result.getValue());
                continue;
            }
            result = checkDupesInHand(hand1);
            success = result.getKey();
            System.out.println(result.getValue());
            hand1.sortHand();
        }

        success = false;
        while (!success) {
            System.out.println("Please enter the hand for Player 2:");
            String hand = scan.nextLine();
            hand2.hand = Arrays.asList(hand.split(","));
            Pair<Boolean, String> result = checkHand(hand2);
            success = result.getKey();
            if (!success) {
                System.out.println(result.getValue());
                continue;
            }
            result = checkDupesInHand(hand2);
            success = result.getKey();
            System.out.println(result.getValue());
            hand2.sortHand();
        }

        Pair<Boolean, String> dupes = checkDupes();
        if (!dupes.getKey()) {
            System.out.println(dupes.getValue());
            return false;
        }
        return true;
    }

    public int valueOf(String value) {
        return validValues.indexOf(value);
    }
    public String getCardValue(String card) {
        return card.substring(0, card.length() - 1);
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

    public Pair<Boolean, String> checkDupes() {
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
        if (hand.hand.size() != HAND_SIZE) {
            return;
        }
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

    public void calculateHandFromCards(Hand hand, Map<String, Integer> values, boolean suited) {
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
            int prevIndex = -1;
            for (String card : hand.hand) {
                String value = card.substring(0, card.length() - 1);
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
    }

    public String compareHands() {
        if (hand1.type.ordinal() > hand2.type.ordinal()) {
            String win = hand1.type.name().replace("_", " ");
            return String.format("Player 1 wins with a %s!\n", win);
        }
        else if (hand1.type.ordinal() < hand2.type.ordinal()) {
            String win = hand2.type.name().replace("_", " ");
            return String.format("Player 2 wins with a %s!\n", win);
        }
        else {
            if (hand1.type == HandTypes.STRAIGHT_FLUSH) {
                if (valueOf(hand1.highCardValue) > valueOf(hand2.highCardValue)) {
                    String win = hand1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a %s-high %s!\n", hand1.highCardValue, win);
                }
                else if (valueOf(hand1.highCardValue) < valueOf(hand2.highCardValue)) {
                    String win = hand2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a %s-high %s!\n", hand2.highCardValue, win);
                }
            }
            else if (hand1.type == HandTypes.FOUR_OF_A_KIND) {
                if (valueOf(hand1.quadValue) > valueOf(hand2.quadValue)) {
                    String win = hand1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a %s %s!\n", hand1.quadValue, win);
                }
                else {
                    String win = hand2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a %s %s!\n", hand2.quadValue, win);
                }
            }
            else if (hand1.type == HandTypes.FULL_HOUSE) {
                if (valueOf(hand1.tripValue) > valueOf(hand2.tripValue)) {
                    String win = hand1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a stronger %s!\n", win);
                }
                else if (valueOf(hand1.tripValue) < valueOf(hand2.tripValue)) {
                    String win = hand2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a stronger %s!\n", win);
                }
                else if (valueOf(hand1.tripValue) == valueOf(hand2.tripValue) &&
                         valueOf(hand1.pair1Value) > valueOf(hand2.pair1Value)) {
                    String win = hand1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a stronger %s!\n", win);
                }
                else if (valueOf(hand1.tripValue) == valueOf(hand2.tripValue) &&
                        valueOf(hand1.pair1Value) < valueOf(hand2.pair1Value)) {
                    String win = hand2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a stronger %s!\n", win);
                }
            }
            else if (hand1.type == HandTypes.FLUSH) {
                // Compare each card from high to low, hand should already be sorted from low to high
                for (int i = hand1.hand.size() - 1; i >= 0; --i) {
                    if (valueOf(getCardValue(hand1.hand.get(i))) > valueOf(getCardValue(hand2.hand.get(i)))) {
                        String win = hand1.type.name().replace("_", " ");
                        return String.format("Player 1 wins with a stronger %s!\n", win);
                    }
                    else if (valueOf(getCardValue(hand1.hand.get(i))) < valueOf(getCardValue(hand2.hand.get(i)))) {
                        String win = hand2.type.name().replace("_", " ");
                        return String.format("Player 2 wins with a stronger %s!\n", win);
                    }
                }
            }
            else if (hand1.type == HandTypes.STRAIGHT) {
                if (valueOf(hand1.highCardValue) > valueOf(hand2.highCardValue)) {
                    String win = hand1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a %s-high %s!\n", hand1.highCardValue, win);
                }
                else if (valueOf(hand1.highCardValue) < valueOf(hand2.highCardValue)) {
                    String win = hand2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a stronger %s!\n", hand2.highCardValue, win);
                }
            }
            else if (hand1.type == HandTypes.THREE_OF_A_KIND) {
                if (valueOf(hand1.tripValue) > valueOf(hand2.tripValue)) {
                    String win = hand1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a %s, %ss!\n", win, hand1.tripValue);
                }
                else if (valueOf(hand1.tripValue) < valueOf(hand2.tripValue)) {
                    String win = hand2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a %s, %ss!\n", win, hand2.tripValue);
                }
                // this case shouldnt even happen in standard 5-card poker
                else {
                    // Comparing hands from high to low will determine who has higher matched cards
                    for (int i = hand1.hand.size() - 1; i >= 0; --i) {
                        if (valueOf(getCardValue(hand1.hand.get(i))) > valueOf(getCardValue(hand2.hand.get(i)))) {
                            String win = hand1.type.name().replace("_", " ");
                            return String.format("Player 1 wins with a stronger %s, %ss!\n", win, hand1.tripValue);
                        }
                        else if (valueOf(getCardValue(hand1.hand.get(i))) < valueOf(getCardValue(hand2.hand.get(i)))) {
                            String win = hand2.type.name().replace("_", " ");
                            return String.format("Player 2 wins with a stronger %s, %ss!\n", win, hand2.tripValue);
                        }
                    }
                }
            }
            else if (hand1.type == HandTypes.TWO_PAIR) {
                int h1High, h1Low, h2High, h2Low;
                int h1p1v = valueOf(hand1.pair1Value);
                int h1p2v = valueOf(hand1.pair2Value);
                int h2p1v = valueOf(hand2.pair1Value);
                int h2p2v = valueOf(hand2.pair2Value);
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
                    String win = hand1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a stronger %s!\n", win);
                }
                else if (h1High < h2High) {
                    String win = hand2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a stronger %s!\n", win);
                }
                else {
                    if (h1Low > h2Low) {
                        String win = hand1.type.name().replace("_", " ");
                        return String.format("Player 1 wins with a stronger %s!\n", win);
                    }
                    else if (h1Low < h2Low) {
                        String win = hand2.type.name().replace("_", " ");
                        return String.format("Player 2 wins with a stronger %s!\n", win);
                    }
                    else {
                        if (valueOf(hand1.highCardValue) > valueOf(hand2.highCardValue)) {
                            String win = hand1.type.name().replace("_", " ");
                            return String.format("Player 1 wins with a stronger %s!\n", win);
                        }
                        else if (valueOf(hand1.highCardValue) < valueOf(hand2.highCardValue)) {
                            String win = hand2.type.name().replace("_", " ");
                            return String.format("Player 2 wins with a stronger %s!\n", win);
                        }
                    }
                }
            }
            else if (hand1.type == HandTypes.ONE_PAIR) {
                if (valueOf(hand1.pair1Value) > valueOf(hand2.pair1Value)) {
                    String win = hand1.type.name().replace("_", " ");
                    return String.format("Player 1 wins with a %s of %s!\n", win, hand1.pair1Value);
                }
                else if (valueOf(hand1.pair1Value) < valueOf(hand2.pair1Value)) {
                    String win = hand2.type.name().replace("_", " ");
                    return String.format("Player 2 wins with a %s of %s!\n", win, hand2.pair1Value);
                }
                else {
                    for (int i = hand1.hand.size() - 1; i >= 0; --i) {
                        if (valueOf(getCardValue(hand1.hand.get(i))) > valueOf(getCardValue(hand2.hand.get(i)))) {
                            String win = hand1.type.name().replace("_", " ");
                            return String.format("Player 1 wins with a stronger %s of %s!\n", win, hand1.pair1Value);
                        }
                        else if (valueOf(getCardValue(hand1.hand.get(i))) < valueOf(getCardValue(hand2.hand.get(i)))) {
                            String win = hand2.type.name().replace("_", " ");
                            return String.format("Player 2 wins with a stronger %s of %s!\n", win, hand2.pair1Value);
                        }
                    }
                }
            }
            else if (hand1.type == HandTypes.HIGH_CARD) {
                for (int i = hand1.hand.size() - 1; i >= 0; --i) {
                    if (valueOf(getCardValue(hand1.hand.get(i))) > valueOf(getCardValue(hand2.hand.get(i)))) {
                        return String.format("Player 1 wins with a stronger card of %s!\n", getCardValue(hand1.hand.get(i)));
                    }
                    else if (valueOf(getCardValue(hand1.hand.get(i))) < valueOf(getCardValue(hand2.hand.get(i)))) {
                        return String.format("Player 2 wins with a stronger card of %s!\n", getCardValue(hand2.hand.get(i)));
                    }
                }
            }
            else {
                String win = hand1.type.name().replace("_", " ");
                return String.format("Invalid hand type found: %s\n", win);
            }
        }
        return "Player 1 and Player 2 have tied!\n";
    }
}
