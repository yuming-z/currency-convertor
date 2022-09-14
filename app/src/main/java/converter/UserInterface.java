package converter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
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


    public static boolean whichUser(Exchange exchange) {
        try {
            Scanner scan = new Scanner(System.in);
            while (scan.hasNext()) {
                System.out.println("Are you an admin user or normal user? 1 for admin 2 for normal(1/2)");
                String typeOfuser = scan.next();
                if (typeOfuser == "1") {
                    System.out.println("what is your username");
                    String username = scan.next();
                    if (exchange.getLogin().containsKey(username)) {
                        System.out.println("what is your password");
                        String password = scan.next();
                        if (nameAndPass(username, password, exchange)) {
                            chooseForAdmin(exchange);
                            return true;
                        }

                    }
                    return true;
                } else if (typeOfuser == "2") {
                    chooseForUser(exchange);
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String chooseForAdmin(Exchange exchange) {
        try {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String chooseForUser(Exchange exchange){
        try {
            Scanner scan = new Scanner(System.in);
            while (scan.hasNext()) {
                System.out.println("which step do you want to take (choose the number) :");
                System.out.println("1:display table of exchange rates");
                System.out.println("2:convert currency of amount");
                String choice = scan.nextLine();
                return choice;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static boolean choiceIsFour(Exchange exchange){
        try {
            Scanner scan = new Scanner(System.in);
            while (scan.hasNext()) {
                System.out.println("Whats the date today? in form 00/00/00");
                String dateNow = scan.nextLine();
                if (dateNow == exchange.getAdminUser().getDate()) {
                    System.out.println("Currencies have already been entered for today");
                }
                else{
                    exchange.getAdminUser().addCurrencyDaily(dateNow);
                    exchange.setPopCurrencies(exchange.getAdminUser().getPopularCurrencies());
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean choiceIsFive(Exchange exchange) {
        try {
            Scanner scan = new Scanner(System.in);
            while (scan.hasNext()) {
                JSONParser jsonRead = new JSONParser();

                FileReader reader = new FileReader("config.json");

                //Read JSON file
                Object obj = jsonRead.parse(reader);

                JSONObject rates = (JSONObject) obj;
                JSONArray rate = (JSONArray) obj;
                System.out.println("From what date?");
                String dateFrom = scan.next();
                System.out.println("To what date?");
                String dateTo = scan.next();
                System.out.println("what is the first currency?");
                String firstCurrency = scan.next();
                System.out.println("what is the second currency?");
                String secondCurrency = scan.next();
                boolean state = false;
                int num = 0;
                for (Object o : rate) {
                    state = exchange.getAdminUser().parseRates(o, dateFrom, dateTo);
                    if (state == true) {
                        num = 1;
                    } else if (state == false && num == 1) {
                        break;
                    }
                }
                System.out.println("Here are the statistics");
                exchange.getAdminUser().calcStatistics(firstCurrency, secondCurrency);
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
            //all conversion rates, average, median, maximum, minimum and standard deviation
    }

    public static boolean choiceIsOne(Exchange exchange) {
            displayTable(exchange);
            return true;
    }

    public static boolean choiceIsTwo(Exchange exchange) {
        try {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean displayTable(Exchange exchange){
        String horizontal = "";
        String vertical = "";
        for (String row : exchange.getHashMap().keySet()) {
            if (Double.compare(Double.parseDouble(row), Double.valueOf(0.0)) > 0 ){
                horizontal += "\n" + row + "(I)";
            }
            else {
                horizontal += "\n" + row + "(D)";
            }
            for (String column : exchange.getHashMap().get(row).keySet()) {
                vertical += column + "\t";
                horizontal += "\t" + exchange.getHashMap().get(row).get(column);
            }
        }
        System.out.println("\t" + vertical + "\n");
        System.out.println(horizontal);
        return true;

    }

    public static double convertCurrency(double amount, String currency1, String currency2, Exchange exchange) {
        if (exchange.getHashMap().containsKey(currency1)) {
            if (exchange.getHashMap().get(currency1).containsKey(currency2)) {

                String rate = exchange.getHashMap().get(currency1).get(currency2);
                double dRate = Double.parseDouble(rate);
                double newAmount = dRate * amount;
                return newAmount;


            }
            return -0.00;
        }
        return -0.00;
    }


}

