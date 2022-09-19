package converter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Admin extends User {



    public Admin(Exchange market, String username) {
        super(market, username);
        this.actions = new String[]{"Display most popular currencies",
                "Get currency statistics",
                "Convert from one currency to another",
                "Add exchange rate information"};
    }

    public HashMap<Currency, HashMap<Currency, Double>> addCurrency(HashMap<Currency, HashMap<Currency,Double>> currentRates,
                                                                    Currency newCurrency, HashMap<Currency,Double> newRates,
                                                                    String currentDate) {
        HashMap<Currency, HashMap<Currency, Double>> result = currentRates;

        for (Map.Entry<Currency, HashMap<Currency, Double>> entry : currentRates.entrySet()) {
            Currency cu = entry.getKey();
            HashMap<Currency, Double> hm = entry.getValue();

            Double reverseRate = 1 / newRates.get(cu);
            hm.put(newCurrency, reverseRate);
            result.put(cu, hm);
        }

        result.put(newCurrency, newRates);

        // Write to the file
        JSONArray database = new JSONArray();
        JSONObject obj = new JSONObject();
        database.add(obj);
        obj.put("currency", newCurrency);
        JSONObject date = new JSONObject();
        JSONArray rates = new JSONArray();
        JSONObject rate = new JSONObject();
        rates.add(rate);
        obj.put("rates",rates);
        rate.put(date, currentDate);
        for (Map.Entry<Currency, Double> set :
                newRates.entrySet()) {
            rate.put(set.getKey().toString(), set.getValue());
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(market.getJSONPath()))) {
            writer.write(database.toJSONString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
