package shop;

public class OrderService {
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    public OrderService(ProductService productService,
                        InventoryService inventoryService,
                        PaymentService paymentService,
                        NotificationService notificationService) {
        this.productService = productService;
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    public String placeOrder(String customer, String productId, int quantity) {
        double price = productService.getPrice(productId);
        if (price == 0) {
            return "Product not found";
        }
        if (!inventoryService.checkStock(quantity)) {
            return "Insufficient stock";
        }
        double total = price * quantity;
        if (!paymentService.processPayment(total)) {
            return "Payment failed";
        }
        inventoryService.reduceStock(quantity);
        return notificationService.sendConfirmation(customer);
    }
}
