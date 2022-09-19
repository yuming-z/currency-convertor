package converter;

import java.io.InvalidClassException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    public abstract boolean setPopularCurrencies(Currency[] currencies) throws InvalidClassException;

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

    public abstract boolean updateRates() throws InvalidClassException;

    private int getRateIndex(LocalDate date, JSONArray history) {

        for (int i = 0; i < history.size(); i++) {

            JSONObject rates = (JSONObject)history.get(i);

            LocalDateTime storageDate = LocalDateTime.parse(
                rates.get("date").toString()
            );

            if (date.isEqual(storageDate.toLocalDate())) {
                return i;
            }
        }

        System.err.println("There is no record matching this date in the system.");
        return -1;
    }

    private List<Double> getRates(LocalDate start, LocalDate end, Currency from, JSONArray history) {

        List<Double> rates = new ArrayList<>();

        int startIndex = this.getRateIndex(start, history);
        int endIndex = this.getRateIndex(end, history);

        if (startIndex == -1 && endIndex == -1) {
            return null;
        }

        for (int i = startIndex; i <= endIndex; i++) {

            JSONObject rate = (JSONObject)history.get(i);

            rates.add(
                Double.parseDouble(
                    rate.get(from.toString()).toString()
                )
            );
        }

        return rates;
    }

    private double getMedian(List<Double> rates) {

        int middleIndex = rates.size() / 2;

        if (rates.size() % 2 == 0) {
            double left = rates.get(middleIndex);
            double right = rates.get(middleIndex + 1);
            return (left + right) / 2;
        }
        else {
            return rates.get(middleIndex);
        }
    }

    private double getAvg(List<Double> rates) {

        double sum = 0.0;

        for (double rate: rates) {
            sum += rate;
        }

        return sum / rates.size();
    }

    private double getMax(List<Double> rates) {

        double max = -1.00;

        for (int i = 0; i < rates.size(); i++) {
            if (rates.get(i) > max) {
                max = rates.get(i);
            }
        }

        return max;
    }

    private double getMin(List<Double> rates) {

        double min = rates.get(0);

        if (rates.size() == 1) {
            return min;
        }

        for (int i = 1; i < rates.size(); i++) {
            if (rates.get(i) < min) {
                min = rates.get(i);
            }
        }

        return min;
    }

    private double getSD(List<Double> rates) {

        double mean = this.getAvg(rates);
        double standardDeviation = 0.00;

        for (double rate: rates) {
            standardDeviation += Math.pow(rate - mean, 2);
        }

        double result = Math.sqrt(standardDeviation / rates.size());

        return result;
    }

    // private void printRates(List<Double> rates) {

    //     for (double rate: rates) {
    //         System.out.println(rate);
    //     }
    // }

    public boolean getSummary(LocalDate start, LocalDate end, Currency from, Currency to) {

        // Get the history of the target history
        // The process will automatically check
        // the validity of the target currency
        JSONArray history = this.market.getHistory(to);
        if (history == null) {
            return false;
        }

        // check the base currency
        if (!this.market.validateCurrency(from)) {
            return false;
        }

        List<Double> rates = this.getRates(start, end, from, history);
        if (rates == null) {
            return false;
        }

        // System.out.println(
        //     String.format(
        //         "The exchange rates between %s and %s are the following:",
        //         start.toString(), end.toString()
        //     )
        // );

        System.out.println(String.format(
                "Average rate: %.2f",
                this.getAvg(rates)
            )
        );
        System.out.println(String.format(
                "Median: %.2f",
                this.getMedian(rates)
            )
        );
        System.out.println(String.format(
                "Maximum rate: %.2f",
                this.getMax(rates)
            )
        );
        System.out.println(String.format(
                "Minimum rate: %.2f",
                this.getMin(rates)
            )
        );
        System.out.println(String.format(
                "Standard deviation: %.2f",
                this.getSD(rates)
            )
        );
        return true;
    }
    public abstract boolean addCurrency(Currency newCurrency) throws InvalidClassException;
}
