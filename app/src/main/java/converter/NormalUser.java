package converter;

public class NormalUser extends User {

    public NormalUser(Exchange market, String username) {
        super(market, username);
        this.actions = new String[]{"Display most popular currencies",
                                    "Get currency statistics", 
                                    "Convert from one currency to another"};
    }
}