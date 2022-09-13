package converter;

public abstract class User {
    
    protected Exchange market;
    protected String username;

    public User(Exchange market) {
        this.market = market;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
