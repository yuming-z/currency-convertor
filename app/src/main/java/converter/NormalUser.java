package converter;

import java.io.InvalidClassException;
import java.util.Currency;

public class NormalUser extends User {

    public NormalUser(Exchange market, String username) {
        super(market, username);
    }

    @Override
    public boolean setPopularCurrencies(Currency currency1, Currency currency2, Currency currency3, Currency currency4)
            throws InvalidClassException {
        throw new InvalidClassException("Unauthorised");
    }
}