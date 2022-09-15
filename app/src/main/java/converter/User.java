package converter;

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
}
