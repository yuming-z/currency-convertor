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

import java.text.SimpleDateFormat;
import java.util.*;

public abstract class User {
    
    protected Exchange market;
    protected String username;
    protected String[] actions;

    public User(Exchange market, String username) {
        this.market = market;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public double convert(double amount, Currency to, Currency from) {

        if (!this.market.refreshDatabase()) {
            return -1;
        }

        if (amount < 0) {
            System.err.println("Amount cannot be negative");
            return -1;
        }

        if (!(this.market.validateCurrency(from) && this.market.validateCurrency(to))) {
            System.err.println("Currency is not supported");
            return -1;
        }

        double rate = this.market.getRates().get(to).get(from);

        return amount * rate;
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
                JSONObject curr = (JSONObject)database.get(i);
                String strCurrency  = (String)curr;
                if (Currency.getInstance(strCurrency) == c1){
                    JSONArray rates = (JSONArray)curr.get("rates");
                    if (rates.get(0) == d2){
                        count = 0;
                    }
                    if (rates.get(0) == d1) {
                        count = 1;
                    }
                    if (count == 1){
                        for (int i = 1; i < rates.size(); i++){
                            JSONObject o = (JSONObject) obj;
                            String country = (String) obj;
                            if (Currency.getInstance(country) == c2){
                                String str = (String)o.get(i);
                                Double exchRate = Double.parseDouble(str);
                                result.add(exchRate);
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

    public boolean action(int id) {
        Scanner scan = new Scanner(System.in);
        
        if (id == 1) {
            //display most popular currencies
        }
        else if (id == 2) {
            try {
                System.out.print("Enter first date dd/mm/yyyy: ");
                SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
                Date d1 = formatter.parse(scan.nextLine());

                System.out.print("Enter second date dd/mm/yyyy: ");
                Date d2 = formatter.parse(scan.nextLine());

                System.out.print("Enter first currency: ");
                Currency c1 = Currency.getInstance(scan.nextLine());

                System.out.print("Enter second currency: ");
                Currency c2 = Currency.getInstance(scan.nextLine());

                HashMap<String, Double> stats = getStats(d1, d2, c1, c2);
                for (Map.Entry<String, Double> entry : stats.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue().toString());
                }
            }
            catch (ParseException e) {
                System.out.println("Error while parsing input!");
            }
            
        }
        else if (id == 3) {
            System.out.print("Enter first currency: ");
            Currency c1 = Currency.getInstance(scan.nextLine());

            System.out.print("Enter second currency: ");
            Currency c2 = Currency.getInstance(scan.nextLine());

            System.out.print("Enter amount of first currency to convert to second currency: ");
            Double amount = UserInterface.getDouble(scan.nextLine());

            Double result = this.convert(amount, c1, c2);

            System.out.println("Result: " + result.toString());
        }
        else if (id == 4) {
            //add currency
        }

        scan.close();

        return true;
    }
}
