package converter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExchangeTest {
    
    @Test
    void getEmptyUsers() {
        Exchange market = new Exchange();
        String username = "sample";

        assertNull(market.getUser(username), "There is no existing user in the exchange market");
    }
}
