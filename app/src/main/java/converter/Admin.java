package converter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidClassException;
import java.time.LocalDateTime;
import java.util.Currency;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Admin extends User {
    
    public Admin(Exchange market, String username) {
        super(market, username);
    }

    @Override
    public boolean setPopularCurrencies(Currency currency1, Currency currency2, Currency currency3, Currency currency4)
            throws InvalidClassException {

        if (!this.market.refreshDatabase()) {
            return false;
        }
        
        if (!this.market.validateCurrency(currency1)) {
            System.err.println("Currency not supported");
            return false;
        }

        if (!this.market.validateCurrency(currency2)) {
            System.err.println("Currency not supported");
            return false;
        }

        if (!this.market.validateCurrency(currency3)) {
            System.err.println("Currency not supported");
            return false;
        }

        if (!this.market.validateCurrency(currency4)) {
            System.err.println("Currency not suppored");
            return false;
        }

        Currency[] currencies = new Currency[]{
            currency1,
            currency2,
            currency3,
            currency4
        };

        for (int i = 0; i < this.market.getPopularCurrencies().length; i++) {
            this.market.getPopularCurrencies()[i] = currencies[i];
        }
        return true;
    }

    private boolean writeFile(JSONArray database) {
        
        try {
            FileWriter writer = new FileWriter(this.market.getDATABASE_PATH());

            writer.write(database.toJSONString());
            
            // flush the writer ptr
            writer.flush();

            writer.close();

            return true;
            
        } catch (IOException e) {
            System.err.println("The database file cannot be loaded.");
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private JSONObject addRates(Currency to) {

        // Create new JSONObject for new rates
        JSONObject newRates = new JSONObject();

        // Add the date
        newRates.put("date", LocalDateTime.now().toString());

        for (Currency from: this.market.getCurrencies()) {

            if (to.equals(from)) {
                continue;
            }

            double rate = UserInterface.getNewRate(from, to);
            newRates.put(from.toString(), rate);
        }

        return newRates;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean updateRates() throws InvalidClassException {
        
        System.out.println("Updating all exchange rates in the system...");
        
        JSONArray database = this.market.getDatabase();
        
        for (int i = 0; i < database.size(); i++) {

            // Get the JSONObject for the currency
            JSONObject currencyObj = (JSONObject)database.get(i);

            // Get the target currency
            Currency to = this.market.loadCurrency(currencyObj);

            // Get the JSONArray rates
            JSONArray rates = (JSONArray)currencyObj.get("rates");

            // append a new element of JSONObject
            // which stores the new rate
            rates.add(this.addRates(to));

            // Update the rates in currencyObj
            currencyObj.put("currency", to.toString());
            currencyObj.put("rates", rates);
        }

        return this.writeFile(database);
    }
}
