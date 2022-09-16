package converter;

import java.util.Currency;

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

        double rate = this.market.getRates().get(to).get(from);

        return amount * rate;
    }
}
