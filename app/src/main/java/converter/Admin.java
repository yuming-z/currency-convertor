package converter;

import java.io.InvalidClassException;
import java.util.Currency;

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
}
