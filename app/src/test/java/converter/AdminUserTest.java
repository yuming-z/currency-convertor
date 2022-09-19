package converter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Currency;

class AdminTest {
    
    String path = "src/test/resources/config_correct.json";
    Exchange market = new Exchange(path, 0);
    User user = new Admin(market, "test user");

    String currentDate = "2022-18-14T10:53:00.000Z"

    @Before
    public AdminTest() {
        HashMap<Currency, Double> newRates = HashMap <>();
        Currency uk = Currency.getInstance(Locale.UK);
        Currency fr = Currency.getInstance(Locale.France);
        Currency us = Currency.getInstance(Locale.US);
        Currency ger = Currency.getInstance(Locale.GERMANY);
        Currency jp = Currency.getInstance("JPY");
        Currency aus = Currency.getInstance("AUD");
        newRates.put(uk, 0.95777517);
        newRates.put(fr, 0.59585807);
        newRates.put(ger, 0.68713249);
        newRates.put(us, 0.69542652);
        newRates.put(jp, 5.6561706);

        HashMap<Currency, HashMap<Currency, Double>> currentRates = new HashMap<Currency, HashMap<Currency, Double>>();
        currentRates.put(aus, null);
        currentRates.get(aus).put(uk, 0.94777517);
        currentRates.get(aus).put(fr, 0.58585807);
        currentRates.get(aus).put(ger, 0.67413249);
        currentRates.get(aus).put(us, 0.67542652);
        currentRates.get(aus).put(jp, 4.6561706);

    }

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
}
