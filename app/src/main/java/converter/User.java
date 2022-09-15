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

        double rate = this.market.getRates().get(to).get(from);

        return amount * rate;
    }
}
