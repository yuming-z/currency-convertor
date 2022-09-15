package converter;

import java.util.Scanner;

public class UserInterface {

    public static String getString(String instruction) {

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

    public static int getInt(String instruction) {
        int response = 0;

        while (true) {
            
            try {
                response = Integer.parseInt(getString(instruction));
                return response;

            } catch (NumberFormatException e) {
                System.err.println("Invalid entry - Number required");
            }

            System.out.println(instruction);
        }
    }

    public static boolean getBoolean(String instruction) {

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

    public static int loginMenu() {
        return displayMenu(
            "You need to have a user account to use the converter.",
            new String[]{"Register", "Log in"},
            "Please enter the number of your option:");
    }

    public static int userTypeMenu() {

        return displayMenu(
            "There are two user types available:",
            new String[]{"Admin", "Normal User"},
            "Please enter the user type most appropriate to you:");
    }

    public static String requestUsername() {

        String username;

        do {

            username = getString("Please enter your username:");
            System.out.println("Your username is: " + username);
            
        } while (!getBoolean("Are you satisfied with your username?"));
        
        return username;
    }
}
