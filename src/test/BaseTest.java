package test;

import org.junit.Test;
import org.junit.Assert;

public class BaseTest {
    @Test
    public void arithmeticTest() {
        Assert.assertEquals(0, 0);
        Assert.assertEquals(2+2, 4);
    }
}
