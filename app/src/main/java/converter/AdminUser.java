package converter;

import java.util.*;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileWriter;
import java.io.IOException;


public class AdminUser implements User{

    private Exchange exchange;

    private String date;

    private boolean datePassed;


    private Map<String, HashMap<String, String>> currency = new HashMap<String, HashMap<String, String>>();

    private List<HashMap<String,HashMap<String,String>>> listOfMaps = new ArrayList<>();
    private HashMap<String, HashMap<String, String>> chosenExchange = new HashMap<String, HashMap<String, String>>();
    //6 currencies
    public AdminUser(Exchange exchange){
        this.exchange = exchange;
        this.setup();
    }

    public boolean setup() {
        this.currency = exchange.getHashMap();
        return true;
    }

    public boolean addCurrencyDaily(){
        Scanner scan = new Scanner(System.in);
        JSONArray rates = new JSONArray();
        JSONObject rate = new JSONObject();
        JSONArray currencies = new JSONArray;
        JSONObject currency = new JSONObject();

        rates.add(rate);
        JSONObject date = new JSONObject();
        System.out.println("Whats the date today? in form 00/00/00");
        String dateNow = scan.nextLine();
        if (dateNow == this.date){
            System.out.println("Currencies have already been entered for today");
        }
        rate.put("date", dateNow);
        JSONArray exchanges = new JSONArray;
        JSONObject exchange = new JSONObject();

        for(int i = 1;  i <= 6;  i++) {
            System.out.println("" + i + " currency to alter rates of is?(use symbol)");
            String currentCurr = scan.nextLine();
            currency.put("curr", currentCurr);
            this.currency.clear();
            for (int j = 0;  j <= 5;  j++){
                System.out.println("exchange rate for currency" + currentCurr + "(e.g 0.000):");
                String exchRate = scan.nextLine();
                exchange.put("exch", exchRate);
                System.out.println("what country? for rate " + exchRate + "(e.g AUS):");
                String country = scan.nextLine();

                exchange.put("country", country);
                this.currency.put(currentCurr,null);
                this.currency.get(currentCurr).put(country,exchRate);
            }

        }
        currencies.add(currency);
        rate.put(currencies, currency);
        System.out.println("Limit of currencies have been reached.");
        try (FileWriter file = new FileWriter("config.json")) {
            file.write(rates.toJSONString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }

    public boolean calcStatistics(String firstCurrency, String secondCurrency){
        for (HashMap<String,HashMap<String,String>> maps : listOfMaps){
            if (maps.containsKey(firstCurrency)){
                ArrayList<Double> exchangeRates1 = new ArrayList<>();
                for(String value: maps.get(firstCurrency).values()) {
                    Double rate = Double.parseDouble(value);
                    exchangeRates1.add(rate);
                }
            if (maps.containsKey(secondCurrency)){
                ArrayList<Double> exchangeRates2 = new ArrayList<>();
                for(String value: maps.get(secondCurrency).values()) {
                    Double rate = Double.parseDouble(value);
                    exchangeRates2.add(rate);
                }
            // dont know what it means when find conversion rates, average, median, maximum, minimum and standard deviation of the conversion rate


        }

        return true;
    }

    public boolean parseRates(Object rates, String dateFrom, String dateTo){
        this.listOfMaps.clear();
        this.chosenExchange.clear();
        JSONObject rate = (JSONObject) rates;
        String date = (String) rate.get("date");

        if (date == dateFrom ){
            this.datePassed = true;
            JSONObject currencies = (JSONObject) rate.get("currencies");
            JSONArray currency = (JSONArray) currencies.get("currency");
            for (Object o : currency){
                JSONObject ob = (JSONObject) o;
                String curr = (String) ob.get("curr");
                JSONObject exchanges = (JSONObject) ob.get("exchanges");
                JSONArray exchange = (JSONArray) exchanges.get("exchange");
                List<String> exchangeRates = new ArrayList<>();
                for (Object obj : exchange){
                    JSONObject object = (JSONObject) obj;
                    String exch  = (String) object.get("exch");
                    String country = (String) object.get("country");
                    this.chosenExchange.put(curr, null);
                    this.chosenExchange.get(curr).put(country, exch);

                }

            }
            this.listOfMaps.add(chosenExchange);
            return true;



        }
        else if (date == dateTo ){
            this.datePassed = false;
            return false;
        }
        else{
            return false;
        }



    }





    @Override
    public double convertCurrency(double amount, String currency1, String currency2) {
        if (currency.containsKey(currency1)) {
            if (currency.get(currency1).containsKey(currency2)) {

                String rate = currency.get(currency1).get(currency2);
                double dRate = Double.parseDouble(rate);
                double newAmount = dRate * amount;
                return newAmount;


            }
            return -0.00;
        }
        return -0.00;
    }

    @Override
    public boolean getDisplayTable() {
        String Row = "";
        String Column = "";
        for (String row : this.currency.keySet()) {
            if (Double.compare(Double.parseDouble(row), Double.valueOf(0.0)) > 0 ){
                Row += "\n" + row + "(I)";
            }
            else {
                Row += "\n" + row + "(D)";
            }
            for (String column : this.currency.get(row).keySet()) {
                Column += column + "\t";
                Row += "\t" + this.currency.get(row).get(column);
            }
        }
        System.out.println("\t" + Column + "\n");
        System.out.println(Row);
        return true;
    }
}