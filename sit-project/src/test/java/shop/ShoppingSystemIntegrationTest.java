package shop;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * System Integration Tests SIT-01 .. SIT-10.
 * Real module objects are wired together (no mocks), so each test checks the
 * interaction between modules, not a single class in isolation.
 */
public class ShoppingSystemIntegrationTest {

    private CustomerService customerService;
    private InventoryService inventoryService;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService();
        inventoryService = new InventoryService();
        orderService = new OrderService(new ProductService(), inventoryService,
                new PaymentService(), new NotificationService());
    }

    /** Mirrors the main application: login first, order only if login works. */
    private String loginThenOrder(String user, String pass, String productId, int qty) {
        if (!customerService.authenticate(user, pass)) {
            return "Login failed";
        }
        return orderService.placeOrder("John", productId, qty);
    }

    @Test
    @DisplayName("SIT-01 Customer -> Order: valid login and order creates the order")
    void sit01_validLoginAndOrder() {
        assertEquals("Order confirmation sent to John", loginThenOrder("john", "1234", "P100", 2));
    }

    @Test
    @DisplayName("SIT-02 Order -> Product: P100 price is 800.00")
    void sit02_productPriceRetrieved() {
        assertEquals(800.00, new ProductService().getPrice("P100"), 0.001);
        assertEquals("Order confirmation sent to John", orderService.placeOrder("John", "P100", 1));
    }

    @Test
    @DisplayName("SIT-03 Order -> Inventory: quantity 2 reduces stock from 10 to 8")
    void sit03_stockReduced() {
        assertEquals(10, inventoryService.getStock());
        orderService.placeOrder("John", "P100", 2);
        assertEquals(8, inventoryService.getStock());
    }

    @Test
    @DisplayName("SIT-04 Order -> Payment: amount 1600 is accepted")
    void sit04_paymentSuccessful() {
        double total = new ProductService().getPrice("P100") * 2;
        assertEquals(1600.00, total, 0.001);
        assertTrue(new PaymentService().processPayment(total));
        assertNotEquals("Payment failed", orderService.placeOrder("John", "P100", 2),
                "OrderService must pass the 1600 total to Payment and get it accepted");
    }

    @Test
    @DisplayName("SIT-05 Payment -> Order: successful payment lets order proceed to confirmation")
    void sit05_paymentThenConfirmation() {
        String result = orderService.placeOrder("John", "P100", 2);
        assertNotEquals("Payment failed", result);
        assertEquals("Order confirmation sent to John", result);
    }

    @Test
    @DisplayName("SIT-06 Order -> Notification: confirmation is sent to the customer")
    void sit06_notificationSent() {
        assertEquals("Order confirmation sent to John", orderService.placeOrder("John", "P100", 2));
    }

    @Test
    @DisplayName("SIT-07 Inventory: quantity 15 is rejected, stock unchanged")
    void sit07_insufficientStock() {
        assertEquals("Insufficient stock", orderService.placeOrder("John", "P100", 15));
        assertEquals(10, inventoryService.getStock());
    }

    @Test
    @DisplayName("SIT-08 Product: invalid product ID is rejected, stock unchanged")
    void sit08_invalidProduct() {
        assertEquals("Product not found", orderService.placeOrder("John", "P999", 2));
        assertEquals(10, inventoryService.getStock());
    }

    @Test
    @DisplayName("SIT-09 Customer: invalid login means no order is created")
    void sit09_invalidLogin() {
        assertEquals("Login failed", loginThenOrder("john", "wrong", "P100", 2));
        assertEquals(10, inventoryService.getStock());
    }

    @Test
    @DisplayName("SIT-10 Payment: invalid amount fails the order, stock unchanged")
    void sit10_invalidPaymentAmount() {
        // Quantity 0 gives a total of 0.00, which the Payment module rejects.
        assertEquals("Payment failed", orderService.placeOrder("John", "P100", 0));
        assertEquals(10, inventoryService.getStock());
    }

    @Test
    @DisplayName("End-to-end: whole scenario with test data C001 / P100 / qty 2")
    void endToEndScenario() {
        assertTrue(customerService.authenticate("john", "1234"));
        String result = orderService.placeOrder("John", "P100", 2);
        assertEquals("Order confirmation sent to John", result);
        assertEquals(8, inventoryService.getStock());
    }
}
