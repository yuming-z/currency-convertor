package converter;

import java.io.InvalidClassException;
import java.util.Currency;

public class NormalUser extends User {

    public NormalUser(Exchange market, String username) {
        super(market, username);
    }

    @Override
    public boolean setPopularCurrencies(Currency[] currencies)
            throws InvalidClassException {
        throw new InvalidClassException("Unauthorised");
    }

    @Override
    public boolean updateRates() throws InvalidClassException {
        throw new InvalidClassException("Unauthorised");
    }
}