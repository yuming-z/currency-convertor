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
    public boolean setPopularCurrencies(Currency[] currencies)
            throws InvalidClassException {

        if (!this.market.refreshDatabase()) {
            return false;
        }
        
        for (int i = 0; i < currencies.length; i++) {
            if (!this.market.validateCurrency(currencies[i])) {

                return false;
            }
        }

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
   
    @SuppressWarnings("unchecked")
    private JSONObject copyRates(Currency to, JSONObject rates) {

        // Create a new JSONObject for latest rates
        JSONObject newRates = new JSONObject();

        // add the date
        newRates.put("date", LocalDateTime.now().toString());

        // copy the rates
        for (Currency from: this.market.getCurrencies()) {

            if (to.equals(from)) {
                continue;
            }

            newRates.put(
                from.toString(),
                rates.get(from.toString())
            );
        }

        return newRates;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean addCurrency(Currency newCurrency) throws InvalidClassException {

        // Get the JSON database
        JSONArray database = this.market.getDatabase();

        // update the rates for the existing currencies
        for (int i = 0; i < database.size(); i++) {

            // Get the JSONObject for the currency
            JSONObject currencyObj = (JSONObject)database.get(i);

            // Get the target currency
            Currency to = this.market.loadCurrency(currencyObj);

            // Get the JSONArray rates
            JSONArray rates = (JSONArray)currencyObj.get("rates");

            // Create a new JSONObject for latest rates
            // Copy the existing newest rate
            JSONObject newRates = this.copyRates(
                to,
                (JSONObject)rates.get(rates.size() - 1)
            );

            // add the exchage rate for the new currency
            double rate = UserInterface.getNewRate(newCurrency, to);
            newRates.put(newCurrency.toString(), rate);

            // append the new rates as a new element
            rates.add(newRates);

            // update the rates in CurrencyObj
            currencyObj.put("currency", to.toString());
            currencyObj.put("rates", rates);
        }

        // Create a new JSONObject for new currency
        JSONObject newCurrencyObj = new JSONObject();

        // add the currency name to JSONObject
        newCurrencyObj.put("currency", newCurrency.toString());

        // Create a JSONArray to store rates
        JSONArray rates = new JSONArray();

        // add new entry of exchange rates
        rates.add(this.addRates(newCurrency));

        // Add the list of rates to JSONObject
        newCurrencyObj.put("rates", rates);

        // Append the new CurrencyObj to the database list
        database.add(newCurrencyObj);

        return this.writeFile(database);
    }
}
