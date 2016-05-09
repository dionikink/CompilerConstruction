package pp.block2.cc.test;

import org.junit.Test;
import pp.block2.cc.antlr.Calculator;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dion on 9-5-2016.
 */
public class ArithmeticTest {
    private Calculator calculator = new Calculator();

    @Test
    public void testAddition() {
        String expression = "5 + 6 + 3";
        assertEquals(calculator.compute(expression), new BigInteger("14"));

        expression = "(3 + 8) + 28";
        assertEquals(calculator.compute(expression), new BigInteger("39"));
    }

    @Test
    public void testSubtraction() {
        String expression = "5 - 6 - 3";
        assertEquals(calculator.compute(expression), new BigInteger("-4"));

        expression = "(3 - 8) - 28";
        assertEquals(calculator.compute(expression), new BigInteger("-33"));
    }

    @Test
    public void testMultiplication() {
        String expression = "5 * 6 * 3";
        assertEquals(calculator.compute(expression), new BigInteger("90"));

        expression = "(3 * 8) * 28";
        assertEquals(calculator.compute(expression), new BigInteger("672"));
    }

    @Test
    public void testPower() {
        String expression = "5^2";
        assertEquals(calculator.compute(expression), new BigInteger("25"));

        expression = "5^2^3";
        assertEquals(calculator.compute(expression), new BigInteger("390625"));

        expression = "5^(2 + 1)";
        assertEquals(calculator.compute(expression), new BigInteger("125"));
    }

    @Test
    public void testNegation() {
        String expression = "--5";
        assertEquals(calculator.compute(expression), new BigInteger("5"));

        expression = "6 + -8";
        assertEquals(calculator.compute(expression), new BigInteger("-2"));

        expression = "6 + --8";
        assertEquals(calculator.compute(expression), new BigInteger("14"));
    }

    @Test
    public void testLargeExpression() {
        String expression = "(5*10) + (21 - 5^2) + -(12*2)";
        assertEquals(calculator.compute(expression), new BigInteger("22"));
    }
}
