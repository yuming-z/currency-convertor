package converter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.InvalidClassException;
import java.util.Currency;

class AdminTest {
    
    static final String PATH = "src/test/resources/config_correct.json";
    Exchange market = new Exchange(PATH, 0);
    User user = new Admin(market, "test user");

    @Test
    void testValidConversion() {

        double amount = 5;
        Currency to = Currency.getInstance("AUD");
        Currency from = Currency.getInstance("CNY");
        
        double result = user.convert(amount, to, from);
        double expected = amount * market.getLatestRates().get(to).get(from);
        
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

    @Test
    void testSetPopularCurrencies() {

        Currency[] currencies = new Currency[]{
            Currency.getInstance("AUD"),
            Currency.getInstance("SGD"),
            Currency.getInstance("USD"),
            Currency.getInstance("EUR")
        };

        boolean status = false;

        try {
            status = user.setPopularCurrencies(currencies);

        } catch (InvalidClassException e) {
            e.printStackTrace();
        }

        assertTrue(status,
        "The popular currencies setting process should be successful.");
        
        for (int i = 0; i < market.getPopularCurrencies().length; i++) {

            assertEquals(
                currencies[i],
                market.getPopularCurrencies()[i],
                String.format(
                    "The popular currencies list should include %s",
                    currencies[i].toString()
                )
            );
        }
    }

    @Test
    void testUnsupportedPopularCurrencies() {

        // One unsupported currency
        Currency[] currencies = new Currency[]{
            Currency.getInstance("AUD"),
            Currency.getInstance("SGD"),
            Currency.getInstance("USD"),
            Currency.getInstance("NZD")
        };

        boolean status = false;

        try {
            status = user.setPopularCurrencies(currencies);

        } catch (InvalidClassException e) {
            e.printStackTrace();
        }

        assertFalse(status, "There is 1 currency not supported");

        // 2 unsupported currencies
        currencies = new Currency[]{
            Currency.getInstance("AUD"),
            Currency.getInstance("USD"),
            Currency.getInstance("NZD"),
            Currency.getInstance("JPY")
        };

        try {
            status = user.setPopularCurrencies(currencies);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }

        assertFalse(status, "There are 2 unsupported currencies");

        // 3 unsupported currencies
        currencies = new Currency[]{
            Currency.getInstance("AUD"),
            Currency.getInstance("NZD"),
            Currency.getInstance("JPY"),
            Currency.getInstance("KRW")
        };

        try {
            status = user.setPopularCurrencies(currencies);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }

        assertFalse(status, "There are 3 unsupported currencies");

        // 4 unsupported currencies
        currencies = new Currency[]{
            Currency.getInstance("CAD"),
            Currency.getInstance("NZD"),
            Currency.getInstance("JPY"),
            Currency.getInstance("KRW")
        };

        try {
            status = user.setPopularCurrencies(currencies);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }

        assertFalse(status, "There are 4 unsupported currencies");
    }
}
