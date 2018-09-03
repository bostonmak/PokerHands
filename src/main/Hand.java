package main;

import java.util.List;
import java.util.ArrayList;

public class Hand {
    public String quadValue, tripValue, pair1Value, pair2Value, highCardValue;
    public HandTypes type;
    public List<String> hand;

    public Hand() {
        quadValue = "";
        tripValue = "";
        pair1Value = "";
        pair2Value = "";
        highCardValue = "";
        type = HandTypes.HIGH_CARD;
        hand = new ArrayList<>();
    }

    public void sortHand() {
        List<String> sortedHand = new ArrayList<>();
        for (int i = 0; i < hand.size(); ++i) {
            String value = hand.get(i).substring(0, hand.get(i).length() - 1);
            int index = -1;
            for (int j = 0; j < sortedHand.size(); ++j) {
                if (PokerHands.getInstance().valueOf(value) <
                        PokerHands.getInstance().valueOf(sortedHand.get(j).substring(0, sortedHand.get(j).length() - 1))) {
                    index = j;
                    break;
                }
            }
            if (index == -1) index = sortedHand.size();
            sortedHand.add(index, hand.get(i));
        }
        hand = sortedHand;
    }
}
