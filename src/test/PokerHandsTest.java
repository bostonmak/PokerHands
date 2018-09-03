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

        // hands are sorted before calculation
        hand = new Hand();
        hand.hand = Arrays.asList("2D", "3D", "4D", "5D");
        hand.sortHand();
        instance.calculateHand(hand);
        Assert.assertEquals("Invalid hands should return default", HandTypes.HIGH_CARD, hand.type);
        Assert.assertEquals("Invalid hands should return default", "", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("10S", "JS", "QS", "KS", "AS");
        hand.sortHand();
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a straight flush", HandTypes.STRAIGHT_FLUSH, hand.type);
        Assert.assertEquals("High card should be A", "A", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("4S", "4H", "4D", "4C", "AS");
        hand.sortHand();
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a 4 of a kind", HandTypes.FOUR_OF_A_KIND, hand.type);
        Assert.assertEquals("Quad value should be 4", "4", hand.quadValue);
        Assert.assertEquals("Kicker card should be A", "A", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("4S", "4H", "8D", "8C", "8S");
        hand.sortHand();
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a full house", HandTypes.FULL_HOUSE, hand.type);
        Assert.assertEquals("Trip value should be 8", "8", hand.tripValue);
        Assert.assertEquals("Pair value should be 4", "4", hand.pair1Value);

        hand = new Hand();
        hand.hand = Arrays.asList("4S", "7S", "KS", "4S", "QS");
        hand.sortHand();
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a flush", HandTypes.FLUSH, hand.type);
        Assert.assertEquals("High card should be K", "K", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("9S", "8H", "7D", "JC", "10H");
        hand.sortHand();
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a straight", HandTypes.STRAIGHT, hand.type);
        Assert.assertEquals("High card should be J", "J", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("QS", "QH", "QD", "4C", "6S");
        hand.sortHand();
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a 3 of a kind", HandTypes.THREE_OF_A_KIND, hand.type);
        Assert.assertEquals("Trip value should be Q", "Q", hand.tripValue);
        Assert.assertEquals("High card should be 6", "6", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("4S", "5H", "4D", "5C", "AS");
        hand.sortHand();
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a two pair", HandTypes.TWO_PAIR, hand.type);
        Assert.assertTrue("Pair 1 value should be 4 or 5", hand.pair1Value.equals("4") || hand.pair1Value.equals("5"));
        Assert.assertTrue("Pair 2 value should be 4 or 5", hand.pair2Value.equals("4") || hand.pair2Value.equals("5"));
        Assert.assertFalse("Pair 1 and pair 2 should not be the same value", hand.pair1Value.equals(hand.pair2Value));
        Assert.assertEquals("Kicker card should be A", "A", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("7S", "3H", "7D", "5C", "JS");
        hand.sortHand();
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a pair", HandTypes.ONE_PAIR, hand.type);
        Assert.assertEquals("Pair 1 value should be 7", "7", hand.pair1Value);
        Assert.assertEquals("Pair 2 value should be J", "J", hand.highCardValue);

        hand = new Hand();
        hand.hand = Arrays.asList("4S", "5H", "QD", "10C", "2D");
        hand.sortHand();
        instance.calculateHand(hand);
        Assert.assertEquals("Should result in a high card", HandTypes.HIGH_CARD, hand.type);
        Assert.assertEquals("High card value should be Q", "Q", hand.highCardValue);
    }

    @Test
    public void compareHandsTest() {
        PokerHands instance = PokerHands.getInstance();
        Hand hand1 = new Hand();
        Hand hand2 = new Hand();
        instance.setHand1(hand1);
        instance.setHand2(hand2);
        Assert.assertEquals("Empty hands technically should tie", "Player 1 and Player 2 have tied!\n", instance.compareHands());

        // Test straight flush
        hand1 = new Hand();
        hand1.hand = Arrays.asList("10S", "JS", "QS", "KS", "AS");
        hand1.sortHand();
        instance.calculateHand(hand1);
        instance.setHand1(hand1);
        hand2 = new Hand();
        hand2.hand = Arrays.asList("10H", "JH", "QH", "KH", "AH");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Equal straight flushes should tie", "Player 1 and Player 2 have tied!\n", instance.compareHands());

        hand2 = new Hand();
        hand2.hand = Arrays.asList("4H", "5H", "6H", "7H", "8H");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Higher straight flush should win", "Player 1 wins with a A-high STRAIGHT FLUSH!\n", instance.compareHands());

        hand2 = new Hand();
        hand2.hand = Arrays.asList("4S", "4H", "4D", "4C", "AS");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Player 1 should have a straight flush", HandTypes.STRAIGHT_FLUSH, hand1.type);
        Assert.assertEquals("Player 2 should have a four of a kind", HandTypes.FOUR_OF_A_KIND, hand2.type);
        Assert.assertEquals("Straight flush should beat four of a kind", "Player 1 wins with a STRAIGHT FLUSH!\n", instance.compareHands());

        // Test four of a kind
        hand1 = new Hand();
        hand1.hand = Arrays.asList("2H", "2S", "2C", "2D", "8H");
        hand1.sortHand();
        instance.calculateHand(hand1);
        instance.setHand1(hand1);
        Assert.assertEquals("Higher four of a kind should win", "Player 2 wins with a 4 FOUR OF A KIND!\n", instance.compareHands());

        hand1 = new Hand();
        hand1.hand = Arrays.asList("QS", "QH", "QD", "6C", "6S");
        hand1.sortHand();
        instance.calculateHand(hand1);
        instance.setHand1(hand1);
        Assert.assertEquals("Player 1 should have a full house", HandTypes.FULL_HOUSE, hand1.type);
        Assert.assertEquals("Player 2 should have a four of a kind", HandTypes.FOUR_OF_A_KIND, hand2.type);
        Assert.assertEquals("Four of a kind should beat full house", "Player 2 wins with a FOUR OF A KIND!\n", instance.compareHands());

        // Test full house
        hand2 = new Hand();
        hand2.hand = Arrays.asList("9H", "9D", "9S", "7H", "7C");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Higher full house should win", "Player 1 wins with a stronger FULL HOUSE!\n", instance.compareHands());

        hand2 = new Hand();
        hand2.hand = Arrays.asList("4S", "9S", "3S", "KS", "AS");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Player 1 should have a full house", HandTypes.FULL_HOUSE, hand1.type);
        Assert.assertEquals("Player 2 should have a flush", HandTypes.FLUSH, hand2.type);
        Assert.assertEquals("Straight flush should beat four of a kind", "Player 1 wins with a FULL HOUSE!\n", instance.compareHands());

        // Test flush
        hand1 = new Hand();
        hand1.hand = Arrays.asList("9D", "4D", "AD", "KD", "3D");
        hand1.sortHand();
        instance.calculateHand(hand1);
        instance.setHand1(hand1);
        Assert.assertEquals("Equal flushes should tie", "Player 1 and Player 2 have tied!\n", instance.compareHands());

        hand1 = new Hand();
        hand1.hand = Arrays.asList("4H", "7H", "2H", "KH", "JH");
        hand1.sortHand();
        instance.calculateHand(hand1);
        instance.setHand1(hand1);
        Assert.assertEquals("Higher flush should win", "Player 2 wins with a stronger FLUSH!\n", instance.compareHands());

        hand1 = new Hand();
        hand1.hand = Arrays.asList("7S", "8H", "9D", "10C", "JS");
        hand1.sortHand();
        instance.calculateHand(hand1);
        instance.setHand1(hand1);
        Assert.assertEquals("Player 1 should have a straight", HandTypes.STRAIGHT, hand1.type);
        Assert.assertEquals("Player 2 should have a flush", HandTypes.FLUSH, hand2.type);
        Assert.assertEquals("Flush should beat straight", "Player 2 wins with a FLUSH!\n", instance.compareHands());

        //Test straight
        hand2 = new Hand();
        hand2.hand = Arrays.asList("7D", "8C", "9H", "10S", "JC");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Equal straights should tie", "Player 1 and Player 2 have tied!\n", instance.compareHands());

        hand2 = new Hand();
        hand2.hand = Arrays.asList("2D", "5C", "3H", "4H", "6D");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Higher straight should win", "Player 1 wins with a J-high STRAIGHT!\n", instance.compareHands());

        hand2 = new Hand();
        hand2.hand = Arrays.asList("7S", "10H", "7D", "7C", "JS");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Player 1 should have a straight", HandTypes.STRAIGHT, hand1.type);
        Assert.assertEquals("Player 2 should have a three of a kind", HandTypes.THREE_OF_A_KIND, hand2.type);
        Assert.assertEquals("Straight should beat three of a kind", "Player 1 wins with a STRAIGHT!\n", instance.compareHands());

        // Test three of a kind
        hand1 = new Hand();
        hand1.hand = Arrays.asList("3S", "5H", "3D", "3C", "KS");
        hand1.sortHand();
        instance.calculateHand(hand1);
        instance.setHand1(hand1);
        Assert.assertEquals("Higher triple should win", "Player 2 wins with a THREE OF A KIND, 7s!\n", instance.compareHands());

        hand1 = new Hand();
        hand1.hand = Arrays.asList("KC", "JH", "KH", "4D", "JS");
        hand1.sortHand();
        instance.calculateHand(hand1);
        instance.setHand1(hand1);
        Assert.assertEquals("Player 1 should have a two pair", HandTypes.TWO_PAIR, hand1.type);
        Assert.assertEquals("Player 2 should have a three of a kind", HandTypes.THREE_OF_A_KIND, hand2.type);
        Assert.assertEquals("Three of a kind should beat two pair", "Player 2 wins with a THREE OF A KIND!\n", instance.compareHands());

        // Test two pair
        hand2 = new Hand();
        hand2.hand = Arrays.asList("KD", "JC", "KS", "4H", "JD");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Equal two pairs should tie", "Player 1 and Player 2 have tied!\n", instance.compareHands());

        hand2 = new Hand();
        hand2.hand = Arrays.asList("8D", "5C", "5H", "8H", "6D");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Higher two pair should win", "Player 1 wins with a stronger TWO PAIR!\n", instance.compareHands());

        hand2 = new Hand();
        hand2.hand = Arrays.asList("KD", "5C", "5H", "KS", "6D");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Higher two pair should win", "Player 1 wins with a stronger TWO PAIR!\n", instance.compareHands());

        hand2 = new Hand();
        hand2.hand = Arrays.asList("KD", "JC", "KS", "2D", "JD");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Higher two pair high card should win", "Player 1 wins with a stronger TWO PAIR!\n", instance.compareHands());

        hand2 = new Hand();
        hand2.hand = Arrays.asList("7S", "10H", "7D", "3C", "JS");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Player 1 should have a two pair", HandTypes.TWO_PAIR, hand1.type);
        Assert.assertEquals("Player 2 should have a one pair", HandTypes.ONE_PAIR, hand2.type);
        Assert.assertEquals("Two pair should beat one pair", "Player 1 wins with a TWO PAIR!\n", instance.compareHands());

        // Test pair
        hand1 = new Hand();
        hand1.hand = Arrays.asList("7D", "10D", "7C", "3H", "JH");
        hand1.sortHand();
        instance.calculateHand(hand1);
        instance.setHand1(hand1);
        Assert.assertEquals("Equal pairs should tie", "Player 1 and Player 2 have tied!\n", instance.compareHands());

        hand1 = new Hand();
        hand1.hand = Arrays.asList("3S", "5H", "3D", "9C", "KS");
        hand1.sortHand();
        instance.calculateHand(hand1);
        instance.setHand1(hand1);
        Assert.assertEquals("Higher pair should win", "Player 2 wins with a ONE PAIR of 7!\n", instance.compareHands());

        hand1 = new Hand();
        hand1.hand = Arrays.asList("7S", "7H", "2D", "10C", "JS");
        hand1.sortHand();
        instance.calculateHand(hand1);
        instance.setHand1(hand1);
        Assert.assertEquals("Higher pair with higher kickers should win", "Player 2 wins with a stronger ONE PAIR of 7!\n", instance.compareHands());

        hand1 = new Hand();
        hand1.hand = Arrays.asList("KC", "JH", "3H", "4D", "9S");
        hand1.sortHand();
        instance.calculateHand(hand1);
        instance.setHand1(hand1);
        Assert.assertEquals("Player 1 should have a high card", HandTypes.HIGH_CARD, hand1.type);
        Assert.assertEquals("Player 2 should have a one pair", HandTypes.ONE_PAIR, hand2.type);
        Assert.assertEquals("One pair should beat high card", "Player 2 wins with a ONE PAIR!\n", instance.compareHands());

        // Test high card
        hand2 = new Hand();
        hand2.hand = Arrays.asList("KS", "JD", "3C", "4H", "9D");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Equal high cards should tie", "Player 1 and Player 2 have tied!\n", instance.compareHands());

        hand2 = new Hand();
        hand2.hand = Arrays.asList("KS", "JD", "2C", "4H", "9D");
        hand2.sortHand();
        instance.calculateHand(hand2);
        instance.setHand2(hand2);
        Assert.assertEquals("Higher card should win", "Player 1 wins with a stronger card of 3!\n", instance.compareHands());
    }
}
