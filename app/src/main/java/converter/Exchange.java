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

    public Exchange() {
        this.users = new ArrayList<User>();
        this.currencies = new ArrayList<Currency>();
        this.rates = new HashMap<Currency, HashMap<Currency, Double>>();
        this.popularCurrencies = new Currency[4];
    }

    public User getUser(String username) {

        for (int i = 0; i < users.size(); i++) {

            if (users.get(i).getUsername().equals(username)) {
                return users.get(i);
            }
        }

        return null;
    }

    public static void main(String[] args) {
        
        // Welcome message
        Exchange market = new Exchange();
        System.out.println("Welcome to use the currency converter.");

        // Get username from stdin
        String username;
        User user;
        final int ATTEMPTS = 3; // number of attempts that the user is allowed to enter username

        System.out.println("Please enter your username:");
        Scanner scan = new Scanner(System.in);

        for (int i = 0; i < ATTEMPTS; i++) {
            username = scan.nextLine();
            user = market.getUser(username);

            if (user != null) {
                break;
            }

            if (i == ATTEMPTS - 1 && user == null) {
                // if it is the last attempt and still no user can be found
                // print error message and terminate the system
                System.err.println("You are failing too many times.");
                System.out.println("System terminating...");
                return;
            }
            else {
                // if the user does not exist
                // print the error message and show the number of attempts left
                System.err.println("Wrong username. Please try again.");
                System.out.println(String.format("You have %d attempts left.", ATTEMPTS - (i + 1)));
            }
        }
    }
}
