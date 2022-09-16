
package converter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Scanner;


class AdminUserTest {

    AdminUserTest(){

    }

    @Test
    void testSetup() {
        Exchange exchange = new Exchange();
        AdminUser test = new AdminUser(exchange);
        boolean output = test.setup();
        Assertions.assertTrue(output);
    }

    @Test
    void testGetDate() {
        Exchange exchange = new Exchange();
        AdminUser test = new AdminUser(exchange);
        test.setDate("2020-09-05T19:50:00.000Z");
        String output = test.getDate();
        Assertions.assertEquals("2020-09-05T19:50:00.000Z", output);
    }
    @Test
    void testParseRates(){


    }




        // Simulate user input


}