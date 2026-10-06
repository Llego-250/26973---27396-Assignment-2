package shop;

public class ShoppingSystem {
    public static void main(String[] args) {
        CustomerService customerService = new CustomerService();
        ProductService productService = new ProductService();
        InventoryService inventoryService = new InventoryService();
        PaymentService paymentService = new PaymentService();
        NotificationService notificationService = new NotificationService();

        OrderService orderService = new OrderService(
                productService, inventoryService, paymentService, notificationService);

        boolean loggedIn = customerService.authenticate("john", "1234");
        if (!loggedIn) {
            System.out.println("Login failed");
            return;
        }
        System.out.println("Customer logged in successfully.");

        String result = orderService.placeOrder("John", "P100", 2);
        System.out.println(result);

        System.out.println("Remaining stock: " + inventoryService.getStock());
    }
}
