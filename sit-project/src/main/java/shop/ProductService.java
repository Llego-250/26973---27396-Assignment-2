package shop;

public class ProductService {
    public double getPrice(String productId) {
        if ("P100".equals(productId)) {
            return 800.00;
        }
        return 0.0;
    }
}
