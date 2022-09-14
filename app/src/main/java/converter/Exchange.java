package converter;
//import sun.nio.cs.US_ASCII;
import java.io.*;
import java.util.*;
import org.json.simple.*;
import java.util.Hashtable;
import java.nio.file.Path;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.Currency;

public class Exchange {

    private Map<String, HashMap<String, String>> currency = new HashMap<String, HashMap<String, String>>();
    private String history = "history.json";
    private List<String> currencyCountries = new ArrayList<String>();
    private NormalUser normalUser;
    private AdminUser adminUser;
    private Map<String, String> login = new HashMap<String, String>();
    private Map<String, HashMap<String, String>> popCurrencies = new HashMap<String, HashMap<String, String>>();


    public Exchange() {
    }


    public  Map<String, HashMap<String, String>> getHashMap() {
        return this.currency;
    }


    public boolean parseString(String path) {

        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(new FileReader(path));

            JSONObject jsonObject = (JSONObject) object;

            JSONObject rates = (JSONObject) jsonObject.get("rates");

            String date = (String) rates.get("date");

            JSONArray rate = (JSONArray) rates.get("rate");

            for (Object r : rate) {
                JSONObject thisRate = (JSONObject) r;

            }
            return true;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public  Map<String, String>  getLogin(){ return this.login;}

    public  NormalUser getNormalUser(){ return this.normalUser;}

    public  AdminUser getAdminUser(){ return this.adminUser;}


    public static void main(String[] args){
        Exchange exchange = new Exchange();
        NormalUser normalUser1 = new NormalUser(exchange);
        AdminUser adminUser1 = new AdminUser(exchange);

        try {
            Scanner scan = new Scanner(System.in);
            while (scan.hasNext()) {
                System.out.println("Are you an admin user or normal user? 1 for admin 2 for normal(1/2)");
                String typeOfuser = scan.next();
                if (typeOfuser == "1") {
                    System.out.println("what is your username");
                    String username = scan.next();
                    if (exchange.getLogin().containsKey(username)) {
                        System.out.println("what is your password");
                        String password = scan.next();
                        if (exchange.login.get(username) == password) {
                            System.out.println("which step do you want to take (choose the number) :");
                            System.out.println("1:display table of exchange rates");
                            System.out.println("2:convert currency of amount");
                            System.out.println("3:add currency");
                            System.out.println("4:change rate to latest daily");
                            System.out.println("5:see statistics");
                            String choice = scan.next();
                            if (choice == "4"){
                                exchange.getAdminUser().addCurrencyDaily();
                                exchange.popCurrencies = exchange.getAdminUser().getPopularCurrencies();
                            }
                            if (choice == "5"){
                                JSONParser jsonRead = new JSONParser();

                                FileReader reader = new FileReader("config.json");

                                    //Read JSON file
                                    Object obj = jsonRead.parse(reader);

                                    JSONObject rates = (JSONObject) obj;
                                    JSONArray rate = (JSONArray) obj;
                                    System.out.println("From what date?");
                                    String dateFrom = scan.next() ;
                                    System.out.println("To what date?");
                                    String dateTo = scan.next();
                                    System.out.println("what is the first currency?");
                                    String firstCurrency = scan.next() ;
                                    System.out.println("what is the second currency?");
                                    String secondCurrency = scan.next() ;
                                    boolean state = false;
                                    int num = 0;
                                    for (Object o : rate){
                                        state = exchange.getAdminUser().parseRates(o, dateFrom, dateTo);
                                        if (state == true){
                                            num = 1;
                                        }
                                        else if (state == false && num == 1){
                                            break;
                                        }
                                    }
                                System.out.println("Here are the statistics");
                                getAdminUser().calcStatistics(firstCurrency, secondCurrency)
                                //all conversion rates, average, median, maximum, minimum and standard deviation


                            }
                        }

                    }

                }
                if (typeOfuser == "2") {
                    System.out.println("which step do you want to take (choose the number) :");
                    System.out.println("1:display table of exchange rates");
                    System.out.println("2:convert currency of amount");
                    String choice = scan.next();
                    if (choice == "1") {
                        exchange.getNormalUser().getDisplayTable();
                    }
                    if (choice == "2") {
                        System.out.println("what amount");
                        String amount = scan.next();
                        System.out.println("from what currency");
                        String oldSymbol = scan.next();
                        System.out.println("to what currency");
                        String newSymbol = scan.next();
                        double amountTochange = Double.parseDouble(amount);
                        exchange.getNormalUser().convertCurrency(amountTochange, oldSymbol, newSymbol);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
