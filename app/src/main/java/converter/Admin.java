package converter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Admin extends User {

    private String DATABASE_PATH;

    public Admin(Exchange market, String username) {
        super(market, username);
    }

    public Currency loadCurrency(JSONObject currency) {

        String currencyCode = (String)currency.get("currency");
        return Currency.getInstance(currencyCode);
    }


    public List<Double> getEntries(Date d1, Date d2, Currency c1, Currency c2) {
        JSONParser parser = new JSONParser();
        List<Double> result = new ArrayList<Double>();

        String path = this.market.getJSONPath();
        int count = 0;

        try {
            // Write to the file
            FileReader reader = new FileReader(this.DATABASE_PATH);

            JSONArray database = (JSONArray)parser.parse(reader);

            for (int i = 0; i < database.size(); i++) {
                Currency curr = this.loadCurrency((JSONObject)database.get(i));
                if (curr == c1){
                    JSONArray rates = (JSONArray)curr.get("rates");
                    if (rates.get(0) == d2){
                        count = 0;
                    }
                    if (rates.get(0) == d1) {
                        count = 1;
                    }
                    if (count == 1){
                        for (Object obj : rates){
                            JSONObject o = (JSONObject) obj;
                            String country = (String) obj;
                            if (Currency.getInstance(country) == c2){
                                result.add(o.get(i));
                            }
                        }
                    }

                }
            }

            reader.close();
            return result;
        } catch (IOException e) {
            System.err.println("The database cannot be loaded.");
            return result;
        }


        //Parse the JSON and add all the relevant rates to the list.

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

    public HashMap<Currency, HashMap<Currency, Double>> addCurrency(HashMap<Currency, HashMap<Currency,Double>> currentRates,
                                                                    Currency newCurrency, HashMap<Currency,Double> newRates,
                                                                    String currentDate) {
        HashMap<Currency, HashMap<Currency, Double>> result = currentRates;

        result.put(newCurrency, newRates);
        JSONParser parser = new JSONParser();


            // Write to the file
        JSONArray database = new JSONArray();
        JSONObject obj = new JSONObject();
        databse.add(obj)
        obj.put("currency", newCurrency);
        JSONObject date = new JSONObject();
        JSONArray rates = new JSONArray();
        JSONObject rate = new JSONObject();
        rates.add(rate);
        obj.put("rates",rates)
        rate.put(date, currentDate);
        for (Map.Entry<Currency, Double> set :
                newRates.entrySet()) {
            rate.put(set.getKey().toString(), set.getValue());
        }
       try (PrintWriter writer = new PrintWriter(new FileWriter((this.DATABASE_PATH))) {
                writer.write(databse.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                file.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
    }



        return result;
    }


}