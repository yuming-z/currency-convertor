package converter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Currency;

class AdminTest {
    
    String path = "src/test/resources/config_correct.json";
    Exchange market = new Exchange(path, 0);
    User user = new Admin(market, "test user");

    @Test
    void testValidConversion() {

        double amount = 5;
        Currency to = Currency.getInstance("AUD");
        Currency from = Currency.getInstance("CNY");
        
        double result = user.convert(amount, to, from);
        double expected = amount * market.getRates().get(to).get(from);
        
        assertEquals(
            expected,
            result,
            "The conversion produces a wrong result. Expected: " + expected);
    }

    @Test
    void testConversionNegativeAmount() {
        
        double amount = -1;
        Currency to = Currency.getInstance("AUD");
        Currency from = Currency.getInstance("CNY");

        double result = user.convert(amount, to, from);

        assertEquals(
            -1,
            result,
            "The conversion should fail: Negative amount");
    }

    @Test
    void testConversionInvalidCurrency() {

        double amount = 5;

        Currency validFrom = Currency.getInstance("CNY");
        Currency invalidFrom = Currency.getInstance("NZD");
        Currency validTo = Currency.getInstance("AUD");
        Currency invalidTo = Currency.getInstance("NZD");

        // Invalid from
        double result = user.convert(amount, validTo, invalidFrom);

        assertEquals(
            -1,
            result,
            "The conversion should fail: Invalid FROM Currency");

        // Invalid To
        result = user.convert(amount, invalidTo, validFrom);

        assertEquals(
            -1,
            result,
            "The conversion should fail: Invalid TO currency");

        // invalid both currencies
        result = user.convert(amount, invalidTo, invalidFrom);

        assertEquals(
            -1,
            result,
            "The conversion should fail: Invalid FROM and TO currency");
    }
}
