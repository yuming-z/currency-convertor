package converter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.InvalidClassException;
import java.time.LocalDate;
import java.util.*;

public class UserTest {
    String path = "src/test/resources/config_correct.json";
    Exchange market = new Exchange(path, 0);
    User user = new Admin(market, "test user");

    @Test
    void testInvalidMarketConvert() {
        Exchange dummymarket = new Exchange("dummystring", 0);
        User dummyuser = new NormalUser(dummymarket, "dummyuser");

        Currency c1 = Currency.getInstance("AUD");
        Currency c2 = Currency.getInstance("CNY");
        double result = dummyuser.convert(5, c1, c2);
        double expected = -1;

        assertEquals(expected, result, "Conversion should not be carried out when the market path is invalid!");

    }

    @Test
    void testSummary() {
        Boolean refresh = market.refreshDatabase();
        assertTrue(refresh, "Market unable to refresh");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        System.setOut(printStream);

        LocalDate d1 = LocalDate.of(2022, 9, 14);
        LocalDate d2 = d1;

        Currency c1 = Currency.getInstance("AUD");
        Currency c2 = Currency.getInstance("SGD");

        Boolean success = user.getSummary(d1, d2, c1, c2);
        
        // call method

        String [] expectedLines = {"Average rate: 1.06", "Median: 1.06", "Maximum rate: 1.06", "Minimum rate: 1.06", "Standard deviation: 0.00", "Average rate: 1.06", "Median: 1.06", "Maximum rate: 1.06", "Minimum rate: 1.06", "Standard deviation: 0.00"};

        assertTrue(success, "Get Summary did not return true");

        Exchange market2 = new Exchange("src/test/resources/config2.json", 0);
        User user2 = new NormalUser(market2, "user2");
        refresh = market2.refreshDatabase();
        assertTrue(refresh, "Market unable to refresh");

        Boolean success2 = user2.getSummary(d1, d2, c1, c2);
        String[] actualLines = baos.toString().split(System.lineSeparator());

        assertTrue(success2, "Get Summary did not return true");
        assertEquals(actualLines.length, expectedLines.length);
        
        for (int i = 0; i < actualLines.length; i++) {
            assertEquals(actualLines[i], expectedLines[i], String.format("Line %d is incorrect", i));
        }
    }

    @Test
    void testDisplay() {
        String[] expectedLines = {"From/To\tAUD\tSGD\tUSD\tEUR", "AUD\t1.00\t1.06\t1.49\t1.48", "SGD\t0.95\t1.00\t1.41\t1.40", "USD\t0.67\t0.71\t1.00\t1.00", "EUR\t0.68\t0.71\t1.00\t1.00"};

        Currency[] currencies = new Currency[]{
            Currency.getInstance("AUD"),
            Currency.getInstance("SGD"),
            Currency.getInstance("USD"),
            Currency.getInstance("EUR")
        };

        Boolean gotCurrencies = false;
        try {
            gotCurrencies = user.setPopularCurrencies(currencies);

        } catch (InvalidClassException e) {
            e.printStackTrace();
        }
        assertTrue(gotCurrencies, "Unit test cannot run without popular currencies set");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        System.setOut(printStream);

        Boolean success = user.display();
        String[] actualLines = baos.toString().split("\n");

        assertTrue(success, "Did not display");
        assertEquals(actualLines.length, expectedLines.length);
        
        for (int i = 0; i < actualLines.length; i++) {
            assertEquals(actualLines[i], expectedLines[i], String.format("Line %d is incorrect", i));
        }
    }
}
