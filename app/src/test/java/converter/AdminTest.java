package converter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Currency;

class AdminTest {

    String path = "src/test/resources/config_correct.json";
    Exchange market = new Exchange(path, 0);
    User user = new Admin(market, "test user");

    String currentDate = "2022-18-14T10:53:00.000Z"

    List<Double> entries = new  ArrayList<>();
    entries.add(0.94777517);
    entries.add(0.67413249);
    entries.add(0.58585807);
    entries.add(0.67542652);
    entries.add(4.6561706);

    HashMap<Currency, Double> newRates = HashMap<>();
    Currency uk = Currency.getInstance(Locale.UK);
    Currency fr = Currency.getInstance(Locale.France);
    Currency us = Currency.getInstance(Locale.US);
    Currency ger = Currency.getInstance(Locale.GERMANY);
    Currency jp = Currency.getInstance("JPY");
    Currency aus = Currency.getInstance("AUD");
    newRates.put(uk,0.95777517);
    newRates.put(fr,0.59585807);
    newRates.put(ger,0.68713249);
    newRates.put(us, 0.69542652);
    newRates.put(jp,5.6561706);

    HashMap<Currency, HashMap<Currency,Double>> currentRates = new HashMap<Currency, HashMap<Currency,Double>>();
    currentRates.put(aus, null);
    currentRates.get(aus).put(uk,0.94777517);
    currentRates.get(aus).put(fr,0.58585807);
    currentRates.get(aus).put(ger,0.67413249);
    currentRates.get(aus).put(us, 0.67542652);
    currentRates.get(aus).put(jp,4.6561706);

    @Test
    void testAddCurrency() {
        HashMap<Currency, HashMap<Currency,Double>> result = user.addCurrency(currentRates, fr, newRates, currentDate);
        HashMap<Currency, HashMap<Currency,Double>> exampleRates = new HashMap<Currency, HashMap<Currency,Double>>();
        exampleRates.put(aus, null);
        exampleRates.get(aus).put(uk,0.94777517);
        exampleRates.get(aus).put(fr,0.58585807);
        exampleRates.get(aus).put(ger,0.67413249);
        exampleRates.get(aus).put(us, 0.67542652);
        exampleRates.get(aus).put(jp,4.6561706);
        exampleRates.put(fr, null);
        exampleRates.get(aus).put(uk,0.95777517);
        exampleRates.get(aus).put(fr,0.59585807);
        exampleRates.get(aus).put(ger,0.68713249);
        exampleRates.get(aus).put(us, 0.69542652);
        exampleRates.get(aus).put(jp,5.6561706);
        HashMap<Currency, HashMap<Currency,Double>> expected = exampleRates;
        assertEquals(
                expected,
                result,
                "The currency was not added. Expected: " + expected);
        )
    }

    @Test
    void testGetEntries() {
        ArrayList<Double> testEntries = new ArrayList<>();
        testEntries.add(0.94777517);
        double result = user.getEntries("AUD", "SGD", "2022-09-14T10:53:00.000Z", "2022-09-14T13:08:00.000Z");
        double expected = testEntries;
        assertEquals(
                expected,
                result,
                "The entries result is wrong. Expected: " + expected);
        )
    }

    @Test
    void testGetEntries() {
        ArrayList<Double> testEntries = new ArrayList<>();
        double result = user.getEntries("JY", "FR", "2022-09-14T10:53:00.000Z", "2022-09-14T13:08:00.000Z");
        double expected = testEntries;
        assertEquals(
                expected,
                result,
                "The entries result is wrong. Expected: " + expected);
        )
    }

    @Test
    void testGetAverage() {

        double result = user.getAverage(entries);
        double expected = (0.94777517+ 0.67413249 + 0.58585807 + 0.67542652 + 4.6561706) / 5
        assertEquals(
                expected,
                result,
                "The average result is wrong. Expected: " + expected);
        )

    }

    @Test
    void testGetAverageNotValid() {

        List<Double> badEntries = new ArrayList<>();
        double result = user.getAverage(badEntries);
        assertEquals(
                -1,
                result,
                "The average result is wrong. Expected: " + expected);
        )
    }

    @Test
    void testGetMedian() {

        double result = user.getMedian(entries);
        double expected = 0.67542652
        assertEquals(
                expected,
                result,
                "The median result is wrong. Expected: " + expected);
        )

    }

    @Test
    void testGetMedianNotValid() {

        List<Double> badEntries = new ArrayList<>();
        double result = user.getMedian(badEntries);
        assertEquals(
                -1,
                result,
                "The median result is wrong. Expected: " + expected);
        )
    }

    @Test
    void testGetMin() {

        double result = user.getMin(entries);
        double expected = 0.58585807
        assertEquals(
                expected,
                result,
                "The minimum result is wrong. Expected: " + expected);
        )

    }

    @Test
    void testGetMinNotValid() {

        List<Double> badEntries = new ArrayList<>();
        double result = user.getMin(badEntries);
        assertEquals(
                -1,
                result,
                "The minimum result is wrong. Expected: " + expected);
        )
    }

    @Test
    void testGetMax() {

        double result = user.getMax(entries);
        double expected = 4.6561706
        assertEquals(
                expected,
                result,
                "The maximum result is wrong. Expected: " + expected);
        )

    }

    @Test
    void testGetMaxNotValid() {

        List<Double> badEntries = new ArrayList<>();
        double result = user.getMax(badEntries);
        assertEquals(
                -1,
                result,
                "The maxmimum result is wrong. Expected: " + expected);
        )
    }


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