package converter;
//import sun.nio.cs.US_ASCII;
import java.io.*;
import java.util.*;


public class Exchange {

    private Map<String, HashMap<String, String>> currency = new HashMap<String, HashMap<String, String>>();
    private List<String> currencyCountries = new ArrayList<String>();

    private Map<String, String> login = new HashMap<String, String>();
    private Map<String, HashMap<String, String>> popCurrencies = new HashMap<String, HashMap<String, String>>();


    public Exchange() {
    }


    public  Map<String, HashMap<String, String>> getHashMap() {
        return this.currency;
    }

    public void setPopCurrencies(Map<String, HashMap<String, String>> popCurrencies) {
        this.popCurrencies = popCurrencies;
    }




    public  Map<String, String>  getLogin(){ return this.login;}





    public static void main(String[] args){
        Exchange exchange = new Exchange();
        NormalUser normalUser1 = new NormalUser(exchange);
        AdminUser adminUser1 = new AdminUser(exchange);
        UserInterface userinterface = new UserInterface();
        userinterface.whichUser(exchange, adminUser1, normalUser1);


    }

}
