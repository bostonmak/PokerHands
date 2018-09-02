

public class Hand {
    public String fiveValue, quadValue, tripValue, pair1Value, pair2Value, highCardValue;
    public HandTypes type;

    public Hand() {
        fiveValue = "";
        quadValue = "";
        tripValue = "";
        pair1Value = "";
        pair2Value = "";
        highCardValue = "";
        type = HandTypes.HIGH_CARD;
    }
}
