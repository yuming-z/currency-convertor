package converter;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Exchange {
    
    private List<User> users;
    private List<Currency> currencies;
    private HashMap<Currency, HashMap<Currency, Double>> rates;
    private Currency[] popularCurrencies;

    private final int ATTEMPTS; // number of attempts that the user is allowed to enter username

    public Exchange() {
        this.users = new ArrayList<User>();
        this.currencies = new ArrayList<Currency>();
        this.rates = new HashMap<Currency, HashMap<Currency, Double>>();
        this.popularCurrencies = new Currency[4];
        this.ATTEMPTS = 3;
    }

    public void terminate() {
        System.out.println("System terminating...");
    }

    public User createUser() {         
        User user;
        String username;
        int accountType = 0;

        // Get the user type
        accountType = UserInterface.displayMenu("There are two user type available.", 
            new String[]{"Admin", "Normal User"}, 
            "Please enter the user type most appropriate to you.");
        
        // Create relevant user
        switch (accountType) {
            case 1:
                user = new Admin(this);
                break;

            case 2:
                user = new User(this);
                break;
        
            default:
                user = new User(this);
                break;
        }
        
        // Set username
        username = UserInterface.getString("Please enter your username.");
        user.setUsername(username);

        return user;
    }

    private User validateUser(String username) {

        for (int i = 0; i < users.size(); i++) {

            if (users.get(i).getUsername().equals(username)) {
                return users.get(i);
            }
        }

        return null;
    }

    public User getUser(Scanner input) {
        String username;
        User user;
        int attemptLeft = this.ATTEMPTS;

        System.out.println("Please enter your username:");

        for (int i = 0; i < this.ATTEMPTS; i++) {
            attemptLeft--;

            username = input.nextLine();
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

    public static void main(String[] args) {
        
        // Welcome message
        Exchange market = new Exchange();
        System.out.println("Welcome to use the currency converter.");

        // Create scanner from stdin
        Scanner scan = new Scanner(System.in);

        // Get username from stdin
        User user = market.getUser(scan);

        // No user found in the system
        // terminate the system
        if (user == null) {
            market.terminate();
            return;
        }
    }
}
