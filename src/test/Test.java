package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class Test {

    public static void main(String[] args) {
        JUnitCore juc = new JUnitCore();
        Result result = juc.run(BaseTest.class);
        System.out.printf("Tests ran: %s, Failed: %s%n", result.getRunCount(), result.getFailureCount());
        result = juc.run(PokerHandsTest.class);
        System.out.printf("Tests ran: %s, Failed: %s%n", result.getRunCount(), result.getFailureCount());
        result = juc.run(HandTest.class);
        System.out.printf("Tests ran: %s, Failed: %s%n", result.getRunCount(), result.getFailureCount());
    }
}
