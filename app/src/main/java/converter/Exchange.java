package converter;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Exchange {
    
    private List<User> users;
    private List<Currency> currencies;
    private HashMap<Currency, HashMap<Currency, Double>> rates; // always store the latest rate
    private Currency[] popularCurrencies;

    private final int ATTEMPTS; // number of attempts that the user is allowed to enter username
    private final String DATABASE_PATH;

    public Exchange(String path, int allowableAttempts) {
        this.users = new ArrayList<User>();
        this.currencies = new ArrayList<Currency>();
        this.rates = new HashMap<Currency, HashMap<Currency, Double>>();
        this.popularCurrencies = new Currency[4];
        this.ATTEMPTS = allowableAttempts;
        this.DATABASE_PATH = path;
    }

    public int getATTEMPTS() {
        return ATTEMPTS;
    }

    public void terminate() {
        System.out.println("System terminating...");
    }

    public void createUser(int accountType, String username) {

        User user;
        
        // Create relevant user
        switch (accountType) {
            case 1:
                user = new Admin(this, username);
                break;

            case 2:
                user = new NormalUser(this, username);
                break;
        
            default:
                user = new NormalUser(this, username);
                break;
        }

        this.users.add(user);
    }

    public List<User> getUsers() {
        return users;
    }

    public User getUser(String username) {

        for (int i = 0; i < users.size(); i++) {

            if (users.get(i).getUsername().equals(username)) {
                return users.get(i);
            }
        }

        return null;
    }

    public List<Currency> getCurrencies() {
        return this.currencies;
    }

    public HashMap<Currency, HashMap<Currency, Double>> getRates() {
        return this.rates;
    }

    public Currency[] getPopularCurrencies() {
        return this.popularCurrencies;
    }
    
    /**
     * Load the latest exchange rates
     * @param jsonObject the latest exchange rates
     * @param base the currency to be converted from
     * @return the collection of exchange rates
     */
    private HashMap<Currency, Double> loadRate(JSONObject jsonObject, Currency base) {

        HashMap<Currency, Double> rate = new HashMap<Currency, Double>();

        for (Currency target: this.currencies) {
            if (base.equals(target)) {
                continue;
            }

            String key = target.toString();
            double value = Double.parseDouble(jsonObject.get(key).toString());

            rate.put(target, value);
        }

        return rate;
    }
    
    private Currency loadCurrency(JSONObject currency) {

        String currencyCode = (String)currency.get("currency");
        return Currency.getInstance(currencyCode);
    }

    public boolean refreshDatabase() {
        JSONParser parser = new JSONParser();

        try {
            // Read the file
            FileReader reader = new FileReader(this.DATABASE_PATH);

            // convert the JSON file to JSONArray
            JSONArray database = (JSONArray)parser.parse(reader);

            // load currencies
            for (int i = 0; i < database.size(); i++) {
                this.currencies.add(loadCurrency((JSONObject)database.get(i)));
            }

            // load latest rate
            for (int i = 0; i < database.size(); i++) {
                JSONObject currency = (JSONObject)database.get(i);
                
                Currency base = loadCurrency(currency);
                JSONArray rates = (JSONArray)currency.get("rates");
                JSONObject latestRate = (JSONObject)rates.get(rates.size() - 1);

                this.rates.put(
                    base,
                    this.loadRate(latestRate, base));
            }

            reader.close();
            return true;
        } catch (Exception e) {
            System.err.println("The exchange database cannot be loaded.");
            return false;
        }
    }

    public boolean validateCurrency(Currency currency) {
        
        for (int i = 0; i < this.currencies.size(); i++) {
            if (this.currencies.get(i).equals(currency)) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
     
        int option;
        final int ATTEMPTS_ALLOWED = 3;

        // Welcome message
        Exchange market = new Exchange("src/main/resources/config.json", ATTEMPTS_ALLOWED);
        UserInterface.welcome();

        // User login
        option = 0;
        option = UserInterface.loginMenu();
        
        while (option == 1) {

            // User creation
            UserInterface.createUser(market);

            option = 0;
            option = UserInterface.loginMenu();
        }

        // Get username from stdin
        User user = UserInterface.getUser(market);

        // No user found in the system
        // terminate the system
        if (user == null) {
            market.terminate();
            return;
        }
    }
}
