package converter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.InvalidClassException;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Currency;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert.assertEquals;
import java.lang.System.*;
import java.io.ByteArrayInputStream;
import static org.junit.Assert.*;
import org.skyscreamer.jsonassert.JSONAssert;

class AdminTest {
    
    static final String PATH = "src/test/resources/config_correct.json";
    Exchange market = new Exchange(PATH, 0);
    User user = new Admin(market, "test user");

    String date = LocalDateTime.now().toString();

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

    @Test
    void testWriteFile(){
        boolean actual = user.writeFile(PATH);
        assertTrue(actual, "the file was not written on");

    }


    @Test
    void testAddRates(){
        String actual = String.format("0.97643",
                System.lineSeparator(),
                System.lineSeparator());
        ByteArrayInputStream in = new ByteArrayInputStream(actual.getBytes());
        System.setIn(in);

        Currency to = Currency.getInstance("AUD");
        Currency from = Currency.getInstance("CNY");
        market.setCurrency(to);
        market.setCurrency(from);
        JSONObject addedRates = user.addRates(to);



        String expected = "0.97643";
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(b);
        System.setOut(printStream);

        String[] lines = b.toString().split(System.lineSeparator());
        String inputGot = lines[lines.length-1];


        JSONObject actualUsed = new JSONObject();
        actualUsed.put("date", LocalDateTime.now().toString());
        actualUsed.put("CNY","0.97643");



        JSONAssert.assertEquals(actualUsed, addedRates, true);

    }

    @Test
    void testCopyRates(){
        JSONObject json = new JSONObject();
        json.put("AUD","0.97643");
        Currency c = Currency.getInstance("AUD");
        Currency from = Currency.getInstance("CNY");
        this.market.setCurrency(from);
        JSONObject  expected = copyRates(c, json);
        JSONObject newRates = new JSONObject();
        json.put("date",LocalDateTime.now().toString() );
        json.put("CNY","0.97643");

        JSONAssert.assertEquals(expected, newRates, true);


    }

    @Test
    void testAddRatesFromNotExist(){
        String actual = String.format("0.97643",
                System.lineSeparator(),
                System.lineSeparator());
        ByteArrayInputStream in = new ByteArrayInputStream(actual.getBytes());
        System.setIn(in);

        Currency to = Currency.getInstance("AUD");
        Currency from = Currency.getInstance("CNY");
        market.setCurrency(to);
        JSONObject addedRates = user.addRates(to);



        String expected = "0.97643";
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(b);
        System.setOut(printStream);

        String[] lines = b.toString().split(System.lineSeparator());
        String inputGot = lines[lines.length-1];


        JSONObject actualUsed = new JSONObject();
        actualUsed.put("date", LocalDateTime.now().toString());
        actualUsed.put("CNY","0.97643");



        JSONAssert.assertEquals(actualUsed, addedRates, false);



    }

    @Test
    void testAddRatesDoesNotExist() {
        String expected = String.format("",
                System.lineSeparator(),
                System.lineSeparator());
        ByteArrayInputStream in = new ByteArrayInputStream(expected.getBytes());
        System.setIn(in);

        Currency to = Currency.getInstance("AUD");
        Currency from = Currency.getInstance("CNY");
        market.setCurrency(to);
        market.setCurrency(from);
        JSONObject addedRates = user.addRates(to);


        String actual = "";
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(b);
        System.setOut(printStream);

        String[] lines = b.toString().split(System.lineSeparator());
        String inputGot = lines[lines.length - 1];


        JSONObject actualUsed = new JSONObject();
        actualUsed.put("date", LocalDateTime.now().toString());
        actualUsed.put("CNY","0.97643");



        JSONAssert.assertEquals(actualUsed, addedRates, true);

    }



        @Test
    void testIncorrectWriteFile(){
        boolean actual = user.writeFile("src/test/resources/config_incorrect.json");
        assertTrue(actual, "the file was written on");

    }

    @Test
    void getDatabasePath() {

        String path = "src/test/resources/config_correct.json";

        assertTrue(
                market.getDATABASE_PATH().equals(path), "The database path is incorrect"
        );
    }
}
