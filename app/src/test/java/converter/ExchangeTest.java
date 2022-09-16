package converter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Currency;

class ExchangeTest {

    String pathPrefix = "src/test/resources/";
    Exchange market = new Exchange(pathPrefix + "config_correct.json", 0);

    @Test
    void testGetAttempts() {
        final int ATTEMPTS_ALLOWED = 3;

        market = new Exchange(pathPrefix + "sample.json", ATTEMPTS_ALLOWED);

        assertEquals(
            ATTEMPTS_ALLOWED,
            market.getATTEMPTS(),
            String.format("There are only %d allowable attempts being set.", ATTEMPTS_ALLOWED));
    }

    @Test
    void testCreateAdmin() {

        String admin = "admin";
        market.createUser(1, admin);
        assertNotNull(
            market.getUsers(),
            "There should be something in the user list");
        assertEquals(
            Admin.class,
            market.getUsers().get(0).getClass(),
            "The user creates should be an Admin");     
    }

    @Test
    void testCreateNormalUser() {

        String normalUser = "Normal User";
        market.createUser(2, normalUser);
        assertNotNull(
            market.getUsers(),
            "There should be something in the user list");
        assertEquals(
            NormalUser.class,
            market.getUsers().get(0).getClass(),
            "The user created should be a normal user");     
    }
    
    @Test
    void testGetUserNoUser() {

        User user = market.getUser("not existing user");
        assertNull(user, "There is no user in the system");
    }

    @Test
    void testGetUser() {

        String username = "test user";
        market.createUser(1, username);

        User user = market.getUser(username);
        assertNotNull(user, "There should be a user returned");
    }

    @Test
    void testGetInvalidUser() {

        market.createUser(1, "test user");

        String username = "invalid user";
        User user = market.getUser(username);
        assertNull(user, String.format("User \"%s\" does not exist in the system", username));
    }

    @Test
    void testDatabaseLoad() {
        market = new Exchange(pathPrefix + "config_correct.json", 3);

        int numberOfCurrencies = 6;

        // test whether the database is correctly loaded
        assertTrue(
            market.refreshDatabase(),
            "The database should be able to be loaded");
        
        // test currency collection
        assertNotNull(
            market.getCurrencies(),
            "There should be contents in the currency collection");
        assertEquals(
            numberOfCurrencies,
            market.getCurrencies().size(),
            "There should be 6 currencies loaded in the system");
        
        // test rates collection
        assertNotNull(
            market.getLatestRates(),
            "There should be some contents in the exchange rates collection");
        assertEquals(
            numberOfCurrencies,
            market.getLatestRates().size(),
            "There should be a total of 6 exchange rates record");
        
        for (Currency currency: market.getLatestRates().keySet()) {
            assertEquals(
                numberOfCurrencies - 1,
                market.getLatestRates().get(currency).size(),
                String.format("There should be %d records inside for each currency", numberOfCurrencies - 1));
        }
    }

    @Test
    void testDatabaseLoadNoFile() {
        market = new Exchange(pathPrefix + "config_nofile.json", 3);

        assertFalse(
            market.refreshDatabase(),
            "The database file does not exist");
    }

    @Test
    void testCurrencyValidationNoCurrency() {
        
        Currency currency = Currency.getInstance("AUD");

        assertFalse(
            market.validateCurrency(currency),
            "There is no currency in the system");
    }

    @Test
    void testValidCurrency() {

        // load database
        market.refreshDatabase();

        Currency currency = Currency.getInstance("AUD");

        assertTrue(
            market.validateCurrency(currency),
            String.format("Currency %s exists in the database", currency.toString()));
    }

    @Test
    void testInvalidCurrency() {

        // load database
        Currency currency = Currency.getInstance("NZD");

        assertFalse(
            market.validateCurrency(currency), "Currency " + currency.toString() + " does not exists in the database");
    }
}
