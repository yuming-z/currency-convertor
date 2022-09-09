package converter;

public interface User{
    public double convertCurrency(double amount, String currency1, String currency2);
    public boolean getDisplayTable();

    public boolean setup();
}
