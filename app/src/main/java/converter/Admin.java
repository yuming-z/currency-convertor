package converter;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
import java.util.*;

public class Admin extends User {
    
    public Admin(Exchange market, String username) {
        super(market, username);
    }

    public List<Double> getEntries(Date d1, Date d2, Currency c1, Currency c2) {
        List<Double> result = new ArrayList<Double>();

        String path = this.market.getJSONPath();
        
        //Parse the JSON and add all the relevant rates to the list.

        return result;
    }


    public double getAverage(List<Double> entries) {
        if (entries.size() == 0) {
            return -1;
        }
        
        double sum = 0.0;
        for(int i = 0; i < entries.size(); i++) {
            sum += entries.get(i);
        }

        double avg = sum / entries.size();
        
        return avg;
    }

    public double getMedian(List<Double> entries) {
        if (entries.size() == 0) {
            return -1;
        }
        
        Collections.sort(entries);

        if ((entries.size() % 2) == 0) {
            return (entries.get(entries.size() / 2) + entries.get((entries.size() / 2) - 1)) / 2;
        }
        else {
            return entries.get((entries.size() - 1) / 2);
        }
    }

    public double getStdDev(List<Double> entries) {
        return 0.0;
    }

    public double getMax(List<Double> entries) {
        if (entries.size() == 0) {
            return -1;
        }

        double max = -1;

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i) > max) {
                max = entries.get(i);
            }
        }
        
        return max;
    }

    public double getMin(List<Double> entries) {
        if (entries.size() == 0) {
            return -1;
        }

        double min = 1;

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i) < min) {
                min = entries.get(i);
            }
        }
        
        return min;
    }

    public HashMap<String, Double> getStats(Date d1, Date d2, Currency c1, Currency c2) {
        HashMap<String, Double> result = new HashMap<String, Double>();

        List<Double> entries = this.getEntries(d1, d2, c1, c2);

        double avg = getAverage(entries);
        double med = getMedian(entries);
        double std = getStdDev(entries);
        double max = getMax(entries);
        double min = getMin(entries);

        result.put("average", avg);
        result.put("median", med);
        result.put("standard deviation", std);
        result.put("maximum", max);
        result.put("minimum", min);

        return result;
    }

    public HashMap<Currency, HashMap<Currency, Double>> addCurrency(HashMap<Currency, HashMap<Currency,Double>> currentRates, Currency newCurrency, HashMap<Currency,Double> newRates) {
        HashMap<Currency, HashMap<Currency, Double>> result = currentRates;

        result.put(newCurrency, newRates);
        
        //modify existing entries

        

        return result;
    }


}
