package converter;

import java.io.FileReader;
import java.time.LocalDateTime;
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

    public Exchange(String path) {
        this.users = new ArrayList<User>();
        this.currencies = new ArrayList<Currency>();
        this.rates = new HashMap<Currency, HashMap<Currency, Double>>();
        this.popularCurrencies = new Currency[4];
        this.ATTEMPTS = 3;
        this.DATABASE_PATH = path;
    }

    public void terminate() {
        System.out.println("System terminating...");
    }

    public void createUser() {         
        User user;
        String username;
        int accountType = 0;

        // Get the user type
        accountType = UserInterface.displayMenu("There are two user type available.", 
            new String[]{"Admin", "Normal User"}, 
            "Please enter the user type most appropriate to you:");
        
        // Create relevant user
        switch (accountType) {
            case 1:
                user = new Admin(this);
                break;

            case 2:
                user = new NormalUser(this);
                break;
        
            default:
                user = new NormalUser(this);
                break;
        }
        
        // Set username
        username = UserInterface.getString("Please enter your username:");
        System.out.println("Your username is: " + username);
        user.setUsername(username);

        this.users.add(user);
    }

    private User validateUser(String username) {

        for (int i = 0; i < users.size(); i++) {

            if (users.get(i).getUsername().equals(username)) {
                return users.get(i);
            }
        }

        return null;
    }

    public User getUser() {
        String username;
        User user;
        int attemptLeft = this.ATTEMPTS;

        for (int i = 0; i < this.ATTEMPTS; i++) {
            attemptLeft--;

            username = UserInterface.getString("Enter your username:");
            user = this.validateUser(username);

            if (user != null) {
                System.out.println("You are successfully logged in as: " + username);
                return user;
            }

            if (attemptLeft != 0) {
                System.err.println("Wrong username! Please try again.");

                if (attemptLeft > 1) {
                    System.out.println(String.format("You have %d attempts left.", attemptLeft));
                }
                else {
                    System.out.println(String.format("You have %d attempt left.", attemptLeft));
                }
            }
        }

        System.err.println("You failed too many times!");
        return null;
    }

    /**
     * Read exchange rate from JSON file
     * @param jsonObject JSON object storing the rates 
     * @param target the currency to be converted to
     * @param base the currency to be converted from
     */
    private HashMap<Currency, Double> loadRate(JSONObject jsonObject, Currency target) {

        HashMap<Currency, Double> rate = new HashMap<Currency, Double>();

        String key = target.toString();
        double value = Double.parseDouble(jsonObject.get(key).toString());

        rate.put(target, value);
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

                for (Currency target: this.currencies) {
                    if (base.equals(target)) {
                        continue;
                    }

                    this.rates.put(
                        base,
                        loadRate(latestRate, target));
                }
            }

            reader.close();
            return true;
        } catch (Exception e) {
            System.err.println("The exchange database cannot be loaded.");
            return false;
        }
    }

    public static void main(String[] args) {
     
        // Welcome message
        Exchange market = new Exchange("src/main/resources/config.json");
        System.out.println("Welcome to use the currency converter.");

        // User login interface
        int option = UserInterface.displayMenu(
            "You need to have a user account to use the converter.",
            new String[]{"Register", "Log in"},
            "Please enter the index of your option.");
        
        if (option == 1) {
            market.createUser();
            System.out.println("You are now prompted to log into your account.");
        }

        // Get username from stdin
        User user = market.getUser();

        // No user found in the system
        // terminate the system
        if (user == null) {
            market.terminate();
            return;
        }
    }
}
