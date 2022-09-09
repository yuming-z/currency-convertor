package converter;

import java.util.HashMap;
import java.util.Map;

public class AdminUser implements User{

    private Exchange exchange;
    private Map<String, HashMap<String, String>> currency = new HashMap<String, HashMap<String, String>>();
    //6 currencies
    public AdminUser(Exchange exchange){
        this.exchange = exchange;
        this.setup();
    }

    public boolean setup() {
        this.currency = exchange.getHashMap();
        return true;
    }

    @Override
    public double convertCurrency(double amount, String currency1, String currency2) {
        if (currency.containsKey(currency1)) {
            if (currency.get(currency1).containsKey(currency2)) {

                String rate = currency.get(currency1).get(currency2);
                double dRate = Double.parseDouble(rate);
                double newAmount = dRate * amount;
                return newAmount;


            }
            return -0.00;
        }
        return -0.00;
    }

    @Override
    public boolean getDisplayTable() {
        String Row = "";
        String Column = "";
        for (String row : this.currency.keySet()) {
            if (Double.compare(Double.parseDouble(row), Double.valueOf(0.0)) > 0 ){
                Row += "\n" + row + "(I)";
            }
            else {
                Row += "\n" + row + "(D)";
            }
            for (String column : this.currency.get(row).keySet()) {
                Column += column + "\t";
                Row += "\t" + this.currency.get(row).get(column);
            }
        }
        System.out.println("\t" + Column + "\n");
        System.out.println(Row);
        return true;
    }
}