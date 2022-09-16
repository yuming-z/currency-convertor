package converter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import java.util.*;
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
    
    public static int displayMenu(String description, String[] options, String instruction) {
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


    public static boolean nameAndPass(String username, String password, Exchange exchange) {

        Map<String, String> login = exchange.getLogin();
        if(login.containsKey(username)) {
            if (login.get(username) == password) {
                return true;
            }
        }
        return false;
    }
    public static boolean parseString(String path) {

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


    public static boolean whichUser(Exchange exchange, AdminUser adminUser, NormalUser normalUser) {

        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()) {
            System.out.println("Hi How Are You, Welcome to the Exchange :)");
            System.out.println("Are you an admin user or normal user? 1 for admin 2 for normal(1/2)");
            String typeOfuser = scan.next();
            if (typeOfuser == "1") {
                System.out.println("what is your username");
                String username = scan.next();
                if (exchange.getLogin().containsKey(username)) {
                    System.out.println("what is your password");
                    String password = scan.next();
                    if (nameAndPass(username, password, exchange)) {
//                        boolean worked = false;
//                        while (worked == false) {
                            String choice = chooseForAdmin(exchange);
                            while (choice == null){
                                 choice = chooseForAdmin(exchange);
                            }
                            boolean worked = selectChoicesAdmin(choice, exchange, adminUser);
                        //}
                    }

                }
                return true;
            } else if (typeOfuser == "2") {
                boolean worked = false;
                while (!worked ) {
                    String choice = chooseForUser(exchange);
                    while(choice == null){
                        choice = chooseForUser(exchange);
                    }
                    worked = selectChoicesNormal(choice, exchange,normalUser);

            }
        }

        return false;
    }

    public static  boolean selectChoicesNormal(String choice, Exchange exchange, NormalUser normalUser){
            if (choice == "1"){
                normalUser.displayTable(exchange);
                return true;
            }
            else if(choice == "2"){
                choiceIsTwo(exchange);
                return true;
            }
        }



    public static boolean  selectChoicesAdmin(String choice, Exchange exchange, AdminUser adminUser){
        if (choice == "1"){
            adminUser.displayTable(exchange);
            return true;
        }
        else if(choice == "2"){
            choiceIsTwo(exchange);
            return true;
        }
        else if(choice == "4"){
            adminUser.choiceIsFour(exchange);
            return true;
        }
        else if(choice =="5"){
            adminUser.choiceIsFive(exchange);
            return true;
        }
        System.out.println("something went wrong try again. (choose from the selected numbers");
        return false;


    }

    public static String chooseForAdmin(Exchange exchange) {

        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()) {
            System.out.println("which step do you want to take (choose the number) :");
            System.out.println("1:display table of exchange rates");
            System.out.println("2:convert currency of amount");
            System.out.println("3:add currency");
            System.out.println("4:change rate to latest daily");
            System.out.println("5:see statistics");
            String choice = scan.nextLine();
            return choice;
        }

        return null;
    }

    public static String chooseForUser(Exchange exchange){

        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()) {
            System.out.println("which step do you want to take (choose the number) :");
            System.out.println("1:display table of exchange rates");
            System.out.println("2:convert currency of amount");
            String choice = scan.nextLine();
            return choice;
        }


        return null;

    }







    public static boolean choiceIsOne(Exchange exchange) {
            displayTable(exchange);
            return true;
    }

    public static boolean choiceIsTwo(Exchange exchange) {
        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()) {
            System.out.println("what amount");
            String amount = scan.next();
            System.out.println("from what currency");
            String oldSymbol = scan.next();
            System.out.println("to what currency");
            String newSymbol = scan.next();
            double amountTochange = Double.parseDouble(amount);
            convertCurrency(amountTochange, oldSymbol, newSymbol, exchange);
            return true;
        }

        return false;
    }











}

