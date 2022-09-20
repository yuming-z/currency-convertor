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
    private HashMap<Currency, HashMap<Currency, Double>> latestRates; // always store the latest rate
    private HashMap<Currency, HashMap<Currency, Double>> previousRates; // the rate of the previous record
    private Currency[] popularCurrencies;

    private final int ATTEMPTS; // number of attempts that the user is allowed to enter username
    private final String DATABASE_PATH;
    private JSONArray database;

    public Exchange(String path, int allowableAttempts) {
        this.users = new ArrayList<User>();
        this.currencies = new ArrayList<Currency>();
        this.latestRates = new HashMap<Currency, HashMap<Currency, Double>>();
        this.previousRates = new HashMap<Currency, HashMap<Currency, Double>>();
        this.popularCurrencies = new Currency[4];
        this.ATTEMPTS = allowableAttempts;
        this.DATABASE_PATH = path;
    }

    public int getATTEMPTS() {
        return ATTEMPTS;
    }

    public void createUser(int accountType, String username) {

        User user;
        
        // Create relevant user
        switch (accountType) {
            case 1:
                user = new Admin(this, username);
                this.users.add(user);
                break;

            case 2:
                user = new NormalUser(this, username);
                this.users.add(user);
                break;
        }    
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

    public HashMap<Currency, HashMap<Currency, Double>> getLatestRates() {
        return latestRates;
    }

    public HashMap<Currency, HashMap<Currency, Double>> getPreviousRates() {
        return previousRates;
    }

    public Currency[] getPopularCurrencies() {
        return this.popularCurrencies;
    }
    
    /**
     * Load the exchange rates
     * @param jsonObject the exchange rates
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

            // for previous loading only
            // if null -> ignore
            if (jsonObject.get(key) == null) {
                continue;
            }

            double value = Double.parseDouble(jsonObject.get(key).toString());

            rate.put(target, value);
        }

        return rate;
    }
    
    public Currency loadCurrency(JSONObject currency) {

        String currencyCode = (String)currency.get("currency");
        return Currency.getInstance(currencyCode);
    }

    private JSONArray loadDatabase() {

        JSONParser parser = new JSONParser();
        JSONArray database;

        try {
            // Read the file
            FileReader reader = new FileReader(this.DATABASE_PATH);

            // Convert the JSON file to JSONArray
            database = (JSONArray)parser.parse(reader);

            reader.close();

        } catch (Exception e) {
            System.err.println("The exchange database cannot be loaded.");
            return null;
        }

        return database;
    }

    private JSONObject getCurrencyObject(Currency target) {

        for (int i = 0; i < this.database.size(); i++) {
            JSONObject currencyObject = (JSONObject)database.get(i);

            Currency currency = this.loadCurrency(currencyObject);

            if (currency.equals(target)) {
                return currencyObject;
            }
        }

        System.err.println(String.format("%s is not supported.", target.toString()));
        return null;
    }

    private JSONObject getCurrencyObject(int index) {

        return (JSONObject)this.database.get(index);
    }

    public JSONArray getHistory(Currency target) {

        JSONObject currencyObj = this.getCurrencyObject(target);
        if (currencyObj == null) {
            return null;
        }

        return (JSONArray)currencyObj.get("rates");
    }

    public JSONArray getHistory(JSONObject currencyObj) {
        return (JSONArray)currencyObj.get("rates");
    }

    public boolean refreshDatabase() {

        this.database = loadDatabase();
        if (this.database == null) {
            return false;
        }

        // load currencies
        for (int i = 0; i < database.size(); i++) {
            this.currencies.add(loadCurrency((JSONObject)database.get(i)));
        }

        // load rates
        for (int i = 0; i < database.size(); i++) {
            JSONObject currency = this.getCurrencyObject(i);
            
            Currency base = loadCurrency(currency);
            JSONArray rates = this.getHistory(currency);

            // load latest rates
            JSONObject latestRate = (JSONObject)rates.get(rates.size() - 1);

            this.latestRates.put(
                base,
                this.loadRate(latestRate, base));

            // load previous rates
            if (rates.size() > 1) {
                JSONObject previousRate = (JSONObject)rates.get(rates.size() - 2);

                this.previousRates.put(
                    base,
                    this.loadRate(previousRate, base));
            }
        }
        return true;
    }

    public boolean validateCurrency(Currency currency) {
        
        for (int i = 0; i < this.currencies.size(); i++) {
            if (this.currencies.get(i).equals(currency)) {
                return true;
            }
        }

        System.err.println(
            String.format("%s is not supported.",
            currency.toString()));
        return false;
    }

    public String getDATABASE_PATH() {
        return DATABASE_PATH;
    }

    public JSONArray getDatabase() {
        return database;
    }

    public boolean validateAdmin(User user) {
        return user.getClass().equals(Admin.class);
    }

    public static void main(String[] args) {
     
        int option;
        final int ATTEMPTS_ALLOWED = 3;

        // Welcome message
        Exchange market = new Exchange("src/main/resources/config.json", ATTEMPTS_ALLOWED);

        // load database
        if (!market.refreshDatabase()) {
            return;
        }

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
            UserInterface.terminate();
            return;
        }

        // main menu
        while (true) {
            option = 0;
            option = UserInterface.mainMenu();
            switch (option) {
                case 1:
                    UserInterface.convert(user);
                    break;

                case 2:
                    user.display();
                    break;
                
                case 3:
                    if (!UserInterface.setPopularCurrencies(user)) {
                        System.out.println("Back to main menu...");
                    }
                    break;

                case 4:
                    if (market.validateAdmin(user)) {

                        if (!UserInterface.updateRates(user)) {
                            System.err.println("The process of updating new exchange rates is unsuccessful.");
                            System.out.println("Your entry during the process will be discarded.");
                        }
                        else {
                            break;
                        }
                    }
                    else {
                        System.err.println("Unauthorised");
                    }
                    System.out.println("Back to main menu...");
                    break;

                case 5:
                    if (!UserInterface.getSummary(user)) {
                        System.out.println("Back to main menu...");
                    }
                    break;
                
                case 6:
                    if (market.validateAdmin(user)) {

                        if (!UserInterface.addCurrency(user)) {
                            System.err.println("The process of adding new currency is unsuccessful.");
                            System.out.println("Your entry during the process will be discarded.");
                        }
                        else {
                            break;
                        }
                    }
                    else {
                        System.err.println("Unauthorised");
                    }
                    System.out.println("Back to main menu...");
                    break;
            
                default:
                    UserInterface.terminate();
                    return;
            }
        }
    }
}
