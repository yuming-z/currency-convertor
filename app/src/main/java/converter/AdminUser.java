package converter;

import java.util.*;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileWriter;
import java.io.IOException;


public class AdminUser implements UserInterface{

    private Exchange exchange;

    private String date;

    private boolean datePassed;


    private Map<String, HashMap<String, String>> currency = new HashMap<String, HashMap<String, String>>();
    private Map<String, HashMap<String, String>> popCurrencies = new HashMap<String, HashMap<String, String>>();

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

    public boolean addCurrencyDaily(String dateNow){
        this.date = dateNow;
        Scanner scan = new Scanner(System.in);
        JSONArray rates = new JSONArray();
        JSONObject rate = new JSONObject();
        JSONArray currencies = new JSONArray();
        JSONObject currency = new JSONObject();
        rates.add(rate);
        rate.put("date", dateNow);
        JSONArray exchanges = new JSONArray();
        JSONObject exchange = new JSONObject();
        System.out.println("enter 4 mot popular currencies first then another two currencies");
        System.out.println("do you want to add currency? (say yes, if you are done say no):");
        String yesOrno = scan.nextLine();
        int i = 0;
        while(yesOrno == "yes") {

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
                if (i <= 4){
                    this.popCurrencies.put(currentCurr,null);
                    this.currency.get(currentCurr).put(country,exchRate);
                    i++;
                }
            }
            yesOrno = scan.nextLine();

        }
        rate.put(exchanges, exchange);
        currencies.add(currency);
        rate.put(currencies, currency);

        try (FileWriter file = new FileWriter("config.json")) {
            file.write(rates.toJSONString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }

    public boolean calcStatistics(String firstCurrency, String secondCurrency){
        ArrayList<Double> exchangeRates1 = new ArrayList<>();
        for (HashMap<String,HashMap<String,String>> maps : listOfMaps) {
            if (maps.containsKey(firstCurrency)) {
                if (maps.get(firstCurrency).containsKey(secondCurrency)) {
                    Double rate = Double.parseDouble(maps.get(firstCurrency).get(secondCurrency));
                    exchangeRates1.add(rate);
                }
            }

        }
        double sum1 = 0.0000000;
        double sum2 = 0.0000;
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        double median = 0.00000;
        for (Double r : exchangeRates1){
            if (r > max) {
                max = r;
            }
            if(r < min) {
                min = r;
            }
            sum1 += r;
            System.out.println(r);
        }
        if(exchangeRates1.size()%2==1)
        {
            median =exchangeRates1.get((exchangeRates1.size()+1)/2-1);
        }
        else
        {
            median =(exchangeRates1.get(exchangeRates1.size()/2-1)+exchangeRates1.get(exchangeRates1.size()/2)/2);
        }

        double average = sum1/exchangeRates1.size();
        double standardDeviation = 0.0000;
        for(double d: exchangeRates1) {
            standardDeviation += Math.pow(d - average, 2);
        }
        standardDeviation =  Math.sqrt(standardDeviation/exchangeRates1.size());
        System.out.println("The average is "+average+ "");
        System.out.println("The median is" + median + "");
        System.out.println("The minimum is" +min+ "");
        System.out.println("The maximum is " +max+ "");
        System.out.println("The standard deviation is " +standardDeviation+ "");
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
    public String getDate(){ return this.date;}

    public Map<String, HashMap<String, String>> getPopularCurrencies(){
                return this.popCurrencies;
    }


}