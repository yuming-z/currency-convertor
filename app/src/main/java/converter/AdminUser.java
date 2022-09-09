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
    private Map<String, HashMap<String, String>> currency = new HashMap<String, HashMap<String, String>>();
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
        System.out.println("Whats the date today? in form 00/00/00");
        String date = scan.nextLine();
        if (date == this.date){
            System.out.println("Currencies have already been entered for today");
        }
        JSONObject rates = new JSONObject();
        rates.put("date", date);

        for(int i = 1;  i <= 6;  i++) {
            System.out.println("" + i + " currency to alter rates of is?(use symbol)");
            String currency = scan.nextLine();
            rates.put("currency", currency);
            System.out.println("First currency and rate exchange (e.g AUS 0.000):");
            String placeAndrate1 = scan.nextLine();
            String[] symbolAndrate1 = placeAndrate1.split(" ");
            rates.put(symbolAndrate1[1], symbolAndrate1[2]);
        }
        System.out.println("Limit of currencies have been reached.");
        try (FileWriter file = new FileWriter("config.json")) {
            file.write(rates.toJSONString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;

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