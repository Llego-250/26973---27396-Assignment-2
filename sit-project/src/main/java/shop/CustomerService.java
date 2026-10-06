package shop;

public class CustomerService {
    public boolean authenticate(String username, String password) {
        return "john".equals(username) && "1234".equals(password);
    }
}
