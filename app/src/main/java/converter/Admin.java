package converter;

import java.io.InvalidClassException;
import java.util.Currency;

public class Admin extends User {
    
    public Admin(Exchange market, String username) {
        super(market, username);
    }

    @Override
    public boolean setPopularCurrencies(Currency currency1, Currency currency2, Currency currency3, Currency currency4)
            throws InvalidClassException {

        this.market.refreshDatabase();
        
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

        if (this.market.validateCurrency(currency4)) {
            System.err.println("Currency not suppored");
            return false;
        }

        this.market.setPopularCurrencies(new Currency[]{
            currency1,
            currency2,
            currency3,
            currency4
        });
        return true;
    }
}
