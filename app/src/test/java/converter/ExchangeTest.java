package converter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Currency;

class ExchangeTest {
    
    @Test
    void testDatabaseLoad() {
        Exchange market = new Exchange("src/test/resources/config_correct.json");

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
                "There should be 5 records inside for each currency");
        }
    }

    @Test
    void testDatabaseLoadNoFile() {
        Exchange market = new Exchange("src/test/resources/config_nofile.json");

        assertFalse(
            market.refreshDatabase(),
            "The database file does not exist");
    }
}
