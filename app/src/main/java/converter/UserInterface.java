package converter;

import java.util.*;

public class UserInterface {

    private static String getString(String instruction) {

        String response;
        Scanner scan = new Scanner(System.in);

        do {
            System.out.println(instruction);

            response = scan.nextLine();
            
            // if no response is entered
            if ("".equals(response)) {
                System.err.println("Blank entry is not allowed.");
                response = null;
            }

        } while (response == null);

        scan.close();

        return response;
    }

    private static int getInt(String instruction) {
        int response = 0;

        while (true) {
            
            try {
                response = Integer.parseInt(getString(instruction));
                return response;

            } catch (NumberFormatException e) {
                System.err.println("Invalid entry - Number required");
            }
        }
    }

    public static double getDouble(String instruction) {

        double response = 0.00;

        while (true) {
            
            try {
                response = Double.parseDouble(getString(instruction));
                return response;

            } catch (NumberFormatException e) {
                System.err.println("Invalid input - decimal number required");
            }
        }
    }

    private static boolean getBoolean(String instruction) {

        instruction = instruction + " (y/n)";

        String responseString;

        while (true) {
            
            responseString = getString(instruction);

            if (responseString.toLowerCase().equals("y")) {
                return true;
            }
            else if (responseString.toLowerCase().equals("n")) {
                return false;
            }
            else {
                System.err.println("Invalid input - must enter y or n");
            }
        }
    }
    
    private static int displayMenu(String description, String[] options, String instruction) {
        int selection = 0;

        // Display the menu
        System.out.println(description);
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ": " + options[i]);
        }

        // Get the response of the selection
        do {
            selection = getInt(instruction);

            if (selection < 1 || selection > options.length) {
                System.err.println("Invalid selection.");
                selection = 0;

                System.out.println(String.format("Please enter a number between %d and %d", 1, options.length));
            }
        } while (selection == 0);

        return selection;
    }

    public static void welcome() {
        System.out.println("Welcome to use the currency converter.");
    }

    public static void terminate() {
        System.out.println("System terminating...");
    }

    public static int loginMenu() {
        return displayMenu(
            "You need to have a user account to use the converter.",
            new String[]{"Register", "Log in"},
            "Please enter the number of your option:");
    }

    public static int userDo(String[] actions) {
        return displayMenu(
            "Welcome!",
            actions,
            "Please enter the number of your option:");
    }

    private static int userTypeMenu() {
        return displayMenu(
            "There are two user types available:",
            new String[]{"Admin", "Normal User"},
            "Please enter the user type most appropriate to you:");
    }

    private static String setUsername() {

        String username;

        do {

            username = getString("Please enter your username:");
            System.out.println("Your username is: " + username);
            
        } while (!getBoolean("Are you satisfied with your username?"));
        
        return username;
    }

    public static void createUser(Exchange market) {
        
        int accountType = userTypeMenu();
        String username = setUsername();
        market.createUser(accountType, username);

        System.out.println("Back to main menu...");
    }

    private static String getUsername() {
        return getString("Enter your username:");
    }

    public static User getUser(Exchange market) {

        String username;
        User user;
        
        for (int i = market.getATTEMPTS(); i > 0; i--) {

            username = getUsername();
            user = market.getUser(username);

            if (user != null) {
                System.out.println("You are successfully logged in as: " + username);
                return user;
            }

            System.err.println("Wrong username! Please try again.");

            if (i - 1 != 1) {
                System.out.println(String.format("You have %d attempts left.", i - 1));
            }
            else {
                System.out.println(String.format("You have %d attempt left.", i - 1));
            }
        }

        System.err.println("You failed too many times!");
        return null;
    }

    public static boolean convert(User user) {

        // Retrieve information from stdin
        String toCurrencyCode;
        Currency toCurrency;
        double amount = 0.00;
        String fromCurrencyCode;
        Currency fromCurrency;

        try {
            // get the currency to be converted to
            toCurrencyCode = getString(
                "Enter the currency code of the desired currency you want to convert to:");
            toCurrency = Currency.getInstance(toCurrencyCode.toUpperCase());

            // get the amount
            amount = getDouble("Enter the amount:");

            // Get current currency
            fromCurrencyCode = getString("Enter the currency code of your current currency:");
            fromCurrency = Currency.getInstance(fromCurrencyCode.toUpperCase());
            
        } catch (IllegalArgumentException e) {
            System.err.println("The currency code you entered is not a valid ISO 4217 currency code.");
            return false;
        }

        double result = user.convert(amount, toCurrency, fromCurrency);

        if (result == -1) {
            return false;
        }
        else {
            System.out.println(
                String.format("To get %s%.2f you need %s%.2f",
                toCurrency.getSymbol(),
                amount,
                fromCurrency.getSymbol(),
                result));
            return true;
        }
    }
}
