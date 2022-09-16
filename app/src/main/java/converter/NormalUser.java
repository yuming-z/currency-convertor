package converter;

import java.util.*;
import java.time.*;
public class NormalUser implements User {

    private Exchange exchange;
    private Map<String, HashMap<String, String>> currency = new HashMap<String, HashMap<String, String>>();

    public NormalUser(Exchange exchange) {

        this.exchange = exchange;
        this.setup();

    }

    public boolean setup() {
        this.currency = exchange.getHashMap();
        return true;
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


//







}