package shop;

public class NotificationService {
    public String sendConfirmation(String customer) {
        return "Order confirmation sent to " + customer;
    }
}
