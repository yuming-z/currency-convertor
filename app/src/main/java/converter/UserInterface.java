package converter;

import java.io.InvalidClassException;
import java.util.Currency;
import java.util.Scanner;

public class UserInterface {

    @SuppressWarnings("all")
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

    private static double getDouble(String instruction) {

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

    private static Currency getCurrency(String instruction) {

        String currencyCode;
        Currency currency;

        while (true) {

            try {
                
                // get the currency code
                currencyCode = getString(instruction);
                currency = Currency.getInstance(currencyCode.toUpperCase());

                return currency;

            } catch (IllegalArgumentException e) {
                System.err.println("The currency code you entered is not a valid ISO 4217 currency code.");
                System.out.println("Please try again.");
            }
        }
    }

    public static boolean convert(User user) {

        Currency toCurrency = getCurrency(
            "Enter the currency code of the desired currency you want to convert to:"
        );
        double amount = getDouble("Enter the amount:");
        Currency fromCurrency = getCurrency("Enter the currency code of your current currency:");

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
    
    public static double getNewRate(Currency from, Currency to) {

        double rate = getDouble(String.format(
            "Please enter the new rate from %s to %s:",
            from.toString(),
            to.toString()));

        while (rate < 0) {
            System.err.println("The new rate cannot be negative.");
            System.out.println("Please try again.");
            
            rate = getDouble(String.format(
                "Please enter the new rate from %s to %s:",
                from.toString(),
                to.toString()));
        }

        return rate;
    }

    public static boolean updateRates(User user) {
        
        try {
            if (!user.updateRates()) {
                System.err.println(
                    "The update on exchange rates is unfinished.");
                System.out.println(
                    "Your entries during the process will be discarded.");
                return false;
            }
        } catch (InvalidClassException e) {
            System.err.println(e.getMessage());
            return false;
        }

        return true;
    }

    private static Currency[] setPopularCurrencies() {

        Currency[] currencies = new Currency[4];

        for (int i = 0; i < currencies.length; i++) {
            currencies[i] = getCurrency(
                String.format(
                    "Please enter the most popular currency %d:",
                    i + 1)
            );
        }

        return currencies;
    }

    public static boolean setPopularCurrencies(User user) {

        try {
            Currency[] currencies = setPopularCurrencies();

            if (!user.setPopularCurrencies(currencies)) {
                System.err.println("Setting popular currencies is unsuccessful.");
                return false;
            };
        } catch (InvalidClassException e) {
            System.err.println(e.getMessage());
            return false;
        }

        return true;
    }

    public static boolean addCurrency(User user) {

        Currency newCurrency = getCurrency(
            "Please enter the currency code of the new currency type you want to add:"
        );

        try {

            if (!user.addCurrency(newCurrency)) {
                System.err.println(
                    "The process of adding a new currency type is unsuccessful."
                );
                return false;
            }
            
        } catch (InvalidClassException e) {
            System.err.println(e.getMessage());
            return false;
        }

        return true;
    }

    public static int mainMenu() {
        return displayMenu(
            "Main menu",
            new String[]{
                "Convert money",
                "Display excchange rates of most popular currencies",
                "Maintain popular currencies",
                "Maintain exchange rates",
                "Add new currency type",
                "Log Out and Exit"
            },
            "Please enter the index of your option:");
    }
}
