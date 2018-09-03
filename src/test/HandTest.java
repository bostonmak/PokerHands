package test;

import org.junit.Test;
import org.junit.Assert;
import main.Hand;
import main.HandTypes;
import java.util.Arrays;

public class HandTest {

    @Test
    public void handTest() {
        Hand hand = new Hand();
        Assert.assertTrue("hand.quadValue should initialize to \"\"", hand.quadValue.equals(""));
        Assert.assertTrue("hand.tripValue should initialize to \"\"", hand.tripValue.equals(""));
        Assert.assertTrue("hand.pair1Value should initialize to \"\"", hand.pair1Value.equals(""));
        Assert.assertTrue("hand.pair2Value should initialize to \"\"", hand.pair2Value.equals(""));
        Assert.assertTrue("hand.highCardValue should initialize to \"\"", hand.highCardValue.equals(""));
        Assert.assertTrue("hand.type should initialize to HIGH_CARD", hand.type == HandTypes.HIGH_CARD);
        Assert.assertTrue("hand.hand.size should initialize to an empty list", hand.hand.size() == 0);
    }

    @Test
    public void sortHandTest() throws Exception {
        Hand hand = new Hand();
        hand.hand = Arrays.asList("5D", "10S", "JC", "KH", "AS");
        hand.sortHand();
        Assert.assertArrayEquals(hand.hand.toArray(), new String[]{"5D", "10S", "JC", "KH", "AS"});

        hand.hand = Arrays.asList("10S", "7D", "5C", "4H", "2D");
        hand.sortHand();
        Assert.assertArrayEquals(hand.hand.toArray(), new String[]{"2D", "4H", "5C", "7D", "10S"});
    }
}
