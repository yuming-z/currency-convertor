package converter;

import java.io.InvalidClassException;
import java.util.Currency;
import java.util.HashMap;

public abstract class User {
    
    protected Exchange market;
    protected String username;

    public User(Exchange market, String username) {
        this.market = market;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public double convert(double amount, Currency to, Currency from) {

        if (!this.market.refreshDatabase()) {
            return -1;
        }

        if (amount < 0) {
            System.err.println("Amount cannot be negative");
            return -1;
        }

        if (!(this.market.validateCurrency(from) && this.market.validateCurrency(to))) {
            System.err.println("Currency is not supported");
            return -1;
        }

        double rate = this.market.getLatestRates().get(to).get(from);

        return amount * rate;
    }

    public abstract boolean setPopularCurrencies(Currency currency1, Currency currency2, Currency currency3, Currency currency4) throws InvalidClassException;

    private double[][] getPopularRates(HashMap<Currency, HashMap<Currency, Double>> rates) {

        Currency[] currencies = this.market.getPopularCurrencies();

        double[][] popularRates = new double[currencies.length][currencies.length];

        for (int i = 0; i < popularRates.length; i++) {
            
            Currency from = currencies[i];

            for (int j = 0; j < popularRates[i].length; j++) {

                Currency to = currencies[j];

                if (from.equals(to)) {
                    popularRates[i][j] = 1;
                }
                else {
                    popularRates[i][j] = rates.get(to).get(from);
                }
            }
        }

        return popularRates;
    }

    private void displayPopularRates(double[][] latestRates, double[][] previousRates) {

        Currency[] currencies = this.market.getPopularCurrencies();

        // print column name
        System.out.print("From/To\t");
        for (int i = 0; i < currencies.length; i++) {
            
            System.out.print(currencies[i].getCurrencyCode());

            if (i == currencies.length - 1) {
                System.out.print("\n");
            }
            else {
                System.out.print("\t");
            }
        }

        // print conversion rates
        for (int i = 0; i < currencies.length; i++) {
            
            System.out.print(currencies[i].getCurrencyCode() + "\t");

            for (int j = 0; j < currencies.length; j++) {

                double latest = latestRates[i][j];
                double previous = previousRates[i][j];

                System.out.print(String.format("%.2f", latest));
                if (latest < previous) {
                    System.out.print(" (D)");
                }
                else if (latest > previous) {
                    System.out.print(" (I)");
                }

                // suffix
                if (j == currencies.length - 1) {
                    System.out.print("\n");
                }
                else {
                    System.out.print("\t");
                }
            }
        }
    }

    public boolean display() {

        if (!market.refreshDatabase()) {
            return false;
        }
        
        // Get latest rates
        double[][] latestRates = this.getPopularRates(this.market.getLatestRates());

        // Get previous rates
        // The same as latest rates by default
        double[][] previousRates = latestRates;
        if (this.market.getPreviousRates().size() != 0) {
            previousRates = this.getPopularRates(this.market.getPreviousRates());
        }

        // print conversion rates
        this.displayPopularRates(latestRates, previousRates);
        return true;
    }
}
