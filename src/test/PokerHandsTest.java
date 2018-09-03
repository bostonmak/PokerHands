package test;

import org.junit.Test;
import org.junit.Assert;
import main.Hand;
import main.HandTypes;
import main.PokerHands;
import java.util.Arrays;

public class PokerHandsTest {

    @Test
    public void getInstanceTest() throws Exception {
        PokerHands instance = PokerHands.getInstance();
        Assert.assertNotNull(instance);
        Assert.assertArrayEquals(new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"}, instance.getValidValues().toArray());
        Assert.assertArrayEquals(new String[]{"D", "C", "H", "S"}, instance.getValidSuits().toArray());
    }

    @Test
    public void valueOfTest() throws Exception {
        PokerHands instance = PokerHands.getInstance();
        Assert.assertTrue("7 should come before 8", instance.valueOf("7") < instance.valueOf("8"));
        Assert.assertTrue("Ace should be the 13th (0-indexed) and final element in the list", instance.valueOf("A") == 12);
        Assert.assertFalse("3 should be greater than 2", instance.valueOf("3") < instance.valueOf("2"));
        Assert.assertTrue("Cards should always resolve to the same value", instance.valueOf("Q") == instance.valueOf("Q"));
        Assert.assertTrue("No such value 1 exists", instance.valueOf("1") == -1);
        Assert.assertTrue("Card values should be case sensitive", instance.valueOf("j") == -1);
    }

    @Test
    public void getCardValueTest() throws Exception {
        PokerHands instance = PokerHands.getInstance();
        Assert.assertEquals("The card value should be the first part of the card", "2", instance.getCardValue("2D"));
        Assert.assertEquals("Length of card should not matter as long as format is maintained", "12345", instance.getCardValue("12345S"));
        Assert.assertNotEquals("Suits should be indicated by a single letter", "2", instance.getCardValue("2Diamonds"));
        Assert.assertNotEquals("Values should be indicated by a single character", "A", instance.getCardValue("AceS"));
    }

    @Test
    public void checkHandTest() throws Exception {
        PokerHands instance = PokerHands.getInstance();
        Hand hand = new Hand();
        Assert.assertEquals("New hand objects should not have any cards", "Invalid hand size of 0\n",  instance.checkHand(hand).getValue());

        hand.hand = Arrays.asList("2D", "3D", "4D", "5D");
        Assert.assertEquals("A valid hand requires 5 cards", "Invalid hand size of 4\n",  instance.checkHand(hand).getValue());

        hand.hand = Arrays.asList("2D", "3D", "4D", "5D", "6J");
        Assert.assertEquals("Valid suits are required", "Invalid card 6J\n",  instance.checkHand(hand).getValue());

        hand.hand = Arrays.asList("2D", "3D", "4D", "5D", "12D");
        Assert.assertEquals("Valid values are required", "Invalid card 12D\n",  instance.checkHand(hand).getValue());

        hand.hand = Arrays.asList("2D", "3D", "4D", "5D", "6 D");
        Assert.assertEquals("Valid suits card formatting required", "Invalid card 6 D\n",  instance.checkHand(hand).getValue());

        hand.hand = Arrays.asList("2d", "jd", "qC", "Ks", "AH");
        Assert.assertTrue("checkHand is not case sensitive", instance.checkHand(hand).getKey());

        hand.hand = Arrays.asList("ad", "aD", "Ad", "AD", "ad");
        Assert.assertTrue("checkHand does not check dupes", instance.checkHand(hand).getKey());
    }

    @Test
    public void checkDupesTest() throws Exception {
        PokerHands instance = PokerHands.getInstance();
        Hand hand1 = new Hand();
        Hand hand2 = new Hand();
        instance.setHand1(hand1);
        instance.setHand2(hand2);
        Assert.assertTrue("Empty hands have no dupes", instance.checkDupes().getKey());

        hand1.hand = Arrays.asList("2D", "2D", "2D", "2D", "2D");
        instance.setHand1(hand1);
        hand2.hand = Arrays.asList("3D", "3D", "3D", "3D", "3D");
        instance.setHand2(hand2);
        Assert.assertTrue("checkDupes does not check dupes within own hand", instance.checkDupes().getKey());

        hand1.hand = Arrays.asList("2D", "3D", "4D", "5D", "6D");
        instance.setHand1(hand1);
        hand2.hand = Arrays.asList("6D", "7D", "8D", "9D", "10D");
        instance.setHand2(hand2);
        Assert.assertEquals("checkDupes should catch dupes between hands", "Duplicate card 6D\n", instance.checkDupes().getValue());
    }

    @Test
    public void checkDupesInHandTest() throws Exception {
        PokerHands instance = PokerHands.getInstance();
        Hand hand = new Hand();
        Assert.assertEquals("Empty hands are invalid", "Duplicate cards in hand are not allowed", instance.checkDupesInHand(hand).getValue());

        hand.hand = Arrays.asList("2H", "2C", "2D", "2S", "2D");
        Assert.assertEquals("checkDupes in hand should catch dupes in the same hand", "Duplicate cards in hand are not allowed", instance.checkDupesInHand(hand).getValue());

        // Case sensitivity is handled before dupes are checked
        hand.hand = Arrays.asList("ah", "aH", "Ah", "AH", "2D");
        Assert.assertTrue("checkDupes is not case sensitive", instance.checkDupesInHand(hand).getKey());
    }

    @Test
    public void calculateHandTest() throws Exception {
        PokerHands instance = PokerHands.getInstance();
        Hand hand = new Hand();
        instance.calculateHand(hand);
        Assert.assertEquals("Empty hands should return default", HandTypes.HIGH_CARD, hand.type);
        Assert.assertEquals("Empty hands should return default", "", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("2D", "3D", "4D", "5D");
        instance.calculateHand(hand);
        Assert.assertEquals("Invalid hands should return default", HandTypes.HIGH_CARD, hand.type);
        Assert.assertEquals("Invalid hands should return default", "", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("10S", "JS", "QS", "KS", "AS");
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a straight flush", HandTypes.STRAIGHT_FLUSH, hand.type);
        Assert.assertEquals("High card should be A", "A", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("4S", "4H", "4D", "4C", "AS");
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a 4 of a kind", HandTypes.FOUR_OF_A_KIND, hand.type);
        Assert.assertEquals("Quad value should be 4", "4", hand.quadValue);
        Assert.assertEquals("Kicker card should be A", "A", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("4S", "4H", "8D", "8C", "8S");
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a full house", HandTypes.FULL_HOUSE, hand.type);
        Assert.assertEquals("Trip value should be 8", "8", hand.tripValue);
        Assert.assertEquals("Pair value should be 4", "4", hand.pair1Value);

        hand = new Hand();
        hand.hand = Arrays.asList("4S", "7S", "KS", "4S", "QS");
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a flush", HandTypes.FLUSH, hand.type);
        Assert.assertEquals("High card should be K", "K", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("9S", "8H", "7D", "JC", "10H");
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a straight", HandTypes.STRAIGHT, hand.type);
        Assert.assertEquals("High card should be J", "J", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("QS", "QH", "QD", "4C", "6S");
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a 3 of a kind", HandTypes.THREE_OF_A_KIND, hand.type);
        Assert.assertEquals("Trip value should be Q", "Q", hand.tripValue);
        Assert.assertEquals("High card should be 6", "6", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("4S", "5H", "4D", "5C", "AS");
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a two pair", HandTypes.TWO_PAIR, hand.type);
        Assert.assertEquals("Pair 1 value should be 4", "4", hand.pair1Value);
        Assert.assertEquals("Pair 2 value should be 5", "5", hand.pair2Value);
        Assert.assertEquals("Kicker card should be A", "A", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("7S", "3H", "7D", "5C", "JS");
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a pair", HandTypes.ONE_PAIR, hand.type);
        Assert.assertEquals("Pair 1 value should be 7", "7", hand.pair1Value);
        Assert.assertEquals("Pair 2 value should be J", "J", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("4S", "5H", "QD", "10C", "2D");
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a high card", HandTypes.HIGH_CARD, hand.type);
        Assert.assertEquals("High card value should be Q", "Q", hand.pair1Value);
    }
}
