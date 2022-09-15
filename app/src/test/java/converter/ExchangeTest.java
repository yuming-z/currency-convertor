package converter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Currency;

class ExchangeTest {

    @Test
    void testGetAttempts() {
        final int ATTEMPTS_ALLOWED = 3;

        Exchange market = new Exchange("src/test/resources/sample.json", ATTEMPTS_ALLOWED);

        assertEquals(
            ATTEMPTS_ALLOWED,
            market.getATTEMPTS(),
            String.format("There are only %d allowable attempts being set.", ATTEMPTS_ALLOWED));
    }
    
    @Test
    void testDatabaseLoad() {
        Exchange market = new Exchange("src/test/resources/config_correct.json", 3);

        int numberOfCurrencies = 6;

        // test whether the database is correctly loaded
        assertTrue(
            market.refreshDatabase(),
            "The database should be able to be loaded");
        
        // test currency collection
        assertNotNull(
            market.getCurrencies(),
            "There should be contents in the currency coolection");
        assertEquals(
            numberOfCurrencies,
            market.getCurrencies().size(),
            "There should be 6 currencies loaded in the system");
        
        // test rates collection
        assertNotNull(
            market.getRates(),
            "There should be some contents in the exchange rates collection");
        assertEquals(
            numberOfCurrencies,
            market.getRates().size(),
            "There should be a total of 6 exchange rates record");
        
        for (Currency currency: market.getRates().keySet()) {
            assertEquals(
                numberOfCurrencies - 1,
                market.getRates().get(currency).size(),
                String.format("There should be %d records inside for each currency", numberOfCurrencies - 1));
        }
    }

    @Test
    void testDatabaseLoadNoFile() {
        Exchange market = new Exchange("src/test/resources/config_nofile.json", 3);

        assertFalse(
            market.refreshDatabase(),
            "The database file does not exist");
    }
}
