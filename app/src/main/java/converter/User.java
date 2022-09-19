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
import java.util.Currency;

import java.text.SimpleDateFormat;
import java.util.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
public abstract class User {

    protected Exchange market;

    protected LocalDate date;

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
            FileReader reader = new FileReader(market.getJSONPath());

            JSONArray database = (JSONArray)parser.parse(reader);

            for (int i = 0; i < database.size(); i++) {
                JSONObject currency = (JSONObject)database.get(i);
                String currencyCode = (String)currency.get("currency");
                Currency base = Currency.getInstance(currencyCode);

                if (base == c1){
                    JSONArray rates = (JSONArray)currency.get("rates");
                    if (rates.get(0) == d2){
                        count = 0;
                    }
                    if (rates.get(0) == d1) {
                        count = 1;
                    }
                    if (count == 1){
                        for (int j = 1; j < rates.size(); j++){
                            JSONObject curr = (JSONObject)rates.get(j);
                            String currCode = (String)curr.get("currency");
                            Currency base2 = Currency.getInstance(currCode);
                            if (base2 == c2){
                                HashMap<Currency, Double> rate = market.loadRate(curr, base2);

                                result.add(rate.get(curr));
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

        double standardDeviation;

        Double average = this.getAverage(entries);
        for (Double d : entries){
            standardDeviation += Math.pow(d - average, 2);

        }
        double result = Math.sqrt(standardDeviation/entries.size());

        return result;
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

    public static boolean displayTable(Exchange exchange){
        String horizontal = "";
        String vertical = "";
        ArrayList<Currency> curr1 = new ArrayList<Currency>();
        ArrayList<Currency> curr2 = new ArrayLIst<Currency>();
        ArrayList<Double> rates = new ArrayLIst<Double>();
        for(Currency c : exchange.getRates().keySet()){
            curr1.add(c);
            for (Currency cu : exchange.getRates().get(c).keySet()) {
                curr2.add(cu);
                for (Double r : exchange.getRates().get(c).get(cu).keySet()) {
                    rates.add(r);
                }
            }
        }


        for (Currency one : curr1) {

            string one = row.toString();
            if (exchange.getPopularCurrencies().contains(one)) {
                if (Double.compare(Double.parseDouble(curr1), Double.valueOf(0.0)) > 0) {
                    horizontal += "\n" + curr1 + "(I)";
                } else {
                    horizontal += "\n" + curr1 + "(D)";
                }
                for (Currency two : curr2) {
                    string curr2 = column.toString();
                    if (exchange.getPopularCurrencies().contains(curr2)) {
                        vertical += curr2 + "\t";

                        horizontal += "\t" + exchange.getRates().get(row).get(curr2);
                    }
                }
                for (Double d : rates){

                }
            }
        }
        System.out.println("\t" + vertical + "\n");
        System.out.println(horizontal);
        return true;

    }

    public boolean action(int id) {
        Scanner scan = new Scanner(System.in);

        if (id == 1) {
            ths.displayTable();
            return true;
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
            Date dateAndtime = java.time.LocalDateTime.now();
            LocalDate date = java.time.LocalDate.now();
            if(date == this.date){
                System.out.print("Currency has already been added today! ");
                return true;
            }
            this.date = date;

            System.out.print("Enter currency you would like to add: ");
            Currency c1 = Currency.getInstance(scan.nextLine());
            System.out.print("You can add five rates to this currency: ");

            HashMap<Currency,Double> newRates = new HashMap<Currency,Double>();

            for (int i = 0; i< 5; i++){
                int count = i+1;
                System.out.print("Enter" +count+ "currency you would like to convert to: ");
                Currency c2 = Currency.getInstance(scan.nextLine());
                System.out.print("Enter" +count+ "rate for this currency from" + c1 +": ");
                String rate = scan.nextLine();
                Double dRate = Double.parseDouble(rate);
                newRates.put(c2,dRate);

            }

            adminUser.addCurrency(exchange.getRates(),c1, newRates, dateAndtime);
            //add currency
        }

        scan.close();

        return true;
    }
}
