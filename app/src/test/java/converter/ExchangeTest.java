package converter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

class ExchangeTest {
    
    @Test
    void testNoUser() {
        Exchange market = new Exchange();
        
        // Simulate user input
        String input = "hello\nhello\nhello\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());

        Scanner scan = new Scanner(in);
        assertNull(market.getUser(scan), "There is no user in the system.");
    }
}
