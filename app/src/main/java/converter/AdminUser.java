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
import java.util.Map;



public class AdminUser {

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

    public Map<String, HashMap<String, String>> getCurrencyMap(){ return this.currency;}

    public void setCurrencyMap( Map<String, HashMap<String, String>> currency){ this.currency = currency;}

    public Map<String, HashMap<String, String>> getPopCurrencies(){ return this.popCurrencies;}

    public void setPopCurrencies(Map<String, HashMap<String, String>> popCurrencies){ this.popCurrencies = popCurrencies;}





    public  List<HashMap<String,HashMap<String,String>>> getListOfMaps(){ return this.listOfMaps;}

    public void setListOfMaps(List<HashMap<String,HashMap<String,String>>> listOfMaps){  this.listOfMaps = listOfMaps;}

    public HashMap<String, HashMap<String, String>> getChosenExchange(){ return this.chosenExchange;}

    public void setChosenExchange(HashMap<String, HashMap<String, String>> chosenExchange){ this.chosenExchange = chosenExchange;}


    public String getDate(){ return this.date;}

    public boolean getDatePassed(){ return this.datePassed;}

    public void setDatePassed(boolean b){ this.datePassed = b;}

    public void setDate(String date){  this.date = date;}


    public Map<String, HashMap<String, String>> getPopularCurrencies(){
                return this.popCurrencies;
    }

    public boolean calcStatistics(String firstCurrency, String secondCurrency, Exchange exchange) {
        ArrayList<Double> exchangeRates1 = new ArrayList<>();
        for (HashMap<String, HashMap<String, String>> maps : this.getListOfMaps()) {
            if (maps.containsKey(firstCurrency)) {
                if (maps.get(firstCurrency).containsKey(secondCurrency)) {
                    Double rate = Double.parseDouble(maps.get(firstCurrency).get(secondCurrency));
                    exchangeRates1.add(rate);
                    return true;
                }
            }
            return false;

        }
        return false;
    }

    public boolean choiceIsFive(Exchange exchange) {
        try {
            Scanner scan = new Scanner(System.in);
            while (scan.hasNext()) {
                JSONParser jsonRead = new JSONParser();

                FileReader reader = new FileReader("config.json");

                //Read JSON file
                Object obj = jsonRead.parse(reader);

                JSONObject rates = (JSONObject) obj;
                JSONArray rate = (JSONArray) obj;
                System.out.println("From what date?");
                String dateFrom = scan.next();
                System.out.println("To what date?");
                String dateTo = scan.next();
                System.out.println("what is the first currency?");
                String firstCurrency = scan.next();
                System.out.println("what is the second currency?");
                String secondCurrency = scan.next();
                boolean state = false;
                int num = 0;
                for (Object o : rate) {
                    state = parseRates(o, dateFrom, dateTo, exchange);
                    if (state == true) {
                        num = 1;
                    } else if (this.getDatePassed() && num == 1) {
                        break;
                    }
                }
                System.out.println("Here are the statistics");
                this.calcStatistics(firstCurrency, secondCurrency, exchange);
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
        //all conversion rates, average, median, maximum, minimum and standard deviation
    }

    public boolean doMaths(ArrayList<Double> exchangeRates){
        double sum1 = 0.0000000;
        double sum2 = 0.0000;
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        double median = 0.00000;
        for (Double r : exchangeRates){
            if (r > max) {
                max = r;
            }
            if(r < min) {
                min = r;
            }
            sum1 += r;
            System.out.println(r);
        }
        if(exchangeRates.size()%2==1)
        {
            median =exchangeRates.get((exchangeRates.size()+1)/2-1);
        }
        else
        {
            median =(exchangeRates.get(exchangeRates.size()/2-1)+exchangeRates.get(exchangeRates.size()/2)/2);
        }

        double average = sum1/exchangeRates.size();
        double standardDeviation = 0.0000;
        for(double d: exchangeRates) {
            standardDeviation += Math.pow(d - average, 2);
        }
        standardDeviation =  Math.sqrt(standardDeviation/exchangeRates.size());
        System.out.println("The average is "+average+ "");
        System.out.println("The median is" + median + "");
        System.out.println("The minimum is" +min+ "");
        System.out.println("The maximum is " +max+ "");
        System.out.println("The standard deviation is " +standardDeviation+ "");
        return true;
    }

    public boolean addCurrencyDaily(String dateNow, Exchange exchange){
        this.setDate(dateNow);
        Scanner scan = new Scanner(System.in);
        JSONArray rates = new JSONArray();
        JSONObject rate = new JSONObject();
        JSONArray currencies = new JSONArray();
        JSONObject currency = new JSONObject();
        rates.add(rate);
        rate.put("date", dateNow);
        JSONArray exchanges = new JSONArray();
        JSONObject exchang = new JSONObject();
        System.out.println("enter 4 mot popular currencies first then another two currencies");
        System.out.println("do you want to add currency? (say yes, if you are done say no):");
        String yesOrno = scan.nextLine();
        int i = 0;
        while(yesOrno == "yes") {

            System.out.println("" + i + " currency to alter rates of is?(use symbol)");
            String currentCurr = scan.nextLine();
            currency.put("curr", currentCurr);
            this.getCurrencyMap().clear();
            for (int j = 0;  j <= 5;  j++){
                System.out.println("exchange rate for currency" + currentCurr + "(e.g 0.000):");
                String exchRate = scan.nextLine();
                exchang.put("exch", exchRate);
                System.out.println("what country? for rate " + exchRate + "(e.g AUS):");
                String country = scan.nextLine();

                exchang.put("country", country);
                this.getCurrencyMap().put(currentCurr,null);
                this.getCurrencyMap().get(currentCurr).put(country,exchRate);
                if (i <= 4){
                    this.getPopCurrencies().put(currentCurr,null);
                    this.getCurrencyMap().get(currentCurr).put(country,exchRate);
                    i++;
                }
            }
            yesOrno = scan.nextLine();

        }
        rate.put(exchanges, exchang);
        currencies.add(currency);
        rate.put(currencies, currency);

        try (FileWriter file = new FileWriter("config.json")) {
            file.write(rates.toJSONString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }

    public boolean choiceIsFour(Exchange exchange){

        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()) {
            System.out.println("Whats the date today? in form 00/00/00");
            String dateNow = scan.nextLine();
            if (dateNow == this.getDate()) {
                System.out.println("Currencies have already been entered for today");
            }
            else{
                this.addCurrencyDaily(dateNow, exchange);
                exchange.setPopCurrencies(this.getPopularCurrencies());
            }

        }

        return true;
    }

    public boolean parseRates(Object rates, String dateFrom, String dateTo, Exchange exchange) {
        this.getListOfMaps().clear();
        this.getChosenExchange().clear();
        JSONObject rate = (JSONObject) rates;
        String date = (String) rate.get("date");

        if (date == dateFrom) {
            this.setDatePassed(true);
            JSONObject currencies = (JSONObject) rate.get("currencies");
            JSONArray currency = (JSONArray) currencies.get("currency");
            for (Object o : currency) {
                JSONObject ob = (JSONObject) o;
                String curr = (String) ob.get("curr");
                JSONObject exchanges = (JSONObject) ob.get("exchanges");
                JSONArray exchang = (JSONArray) exchanges.get("exchange");
                List<String> exchangeRates = new ArrayList<>();
                for (Object obj : exchang) {
                    JSONObject object = (JSONObject) obj;
                    String exch = (String) object.get("exch");
                    String country = (String) object.get("country");
                    this.getChosenExchange().put(curr, null);
                    this.getChosenExchange().get(curr).put(country, exch);

                }

            }
            this.getListOfMaps().add(this.getChosenExchange());
            return true;


        } else if (date == dateTo) {
            this.setDatePassed(false);
            return false;
        } else {
            return false;
        }
    }

    public static boolean displayTable(Exchange exchange){
        String horizontal = "";
        String vertical = "";
        for (String row : exchange.getHashMap().keySet()) {
            if (Double.compare(Double.parseDouble(row), Double.valueOf(0.0)) > 0 ){
                horizontal += "\n" + row + "(I)";
            }
            else {
                horizontal += "\n" + row + "(D)";
            }
            for (String column : exchange.getHashMap().get(row).keySet()) {
                vertical += column + "\t";
                horizontal += "\t" + exchange.getHashMap().get(row).get(column);
            }
        }
        System.out.println("\t" + vertical + "\n");
        System.out.println(horizontal);
        return true;

    }

    public static double convertCurrency(double amount, String currency1, String currency2, Exchange exchange) {
        if (exchange.getHashMap().containsKey(currency1)) {
            if (exchange.getHashMap().get(currency1).containsKey(currency2)) {

                String rate = exchange.getHashMap().get(currency1).get(currency2);
                double dRate = Double.parseDouble(rate);
                double newAmount = dRate * amount;
                return newAmount;


            }
            return -0.00;
        }
        return -0.00;
    }


}