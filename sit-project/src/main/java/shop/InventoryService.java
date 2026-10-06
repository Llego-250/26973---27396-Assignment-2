package shop;

public class InventoryService {
    private int stock = 10;

    public boolean checkStock(int quantity) {
        return stock >= quantity;
    }

    public void reduceStock(int quantity) {
        stock -= quantity;
    }

    public int getStock() {
        return stock;
    }
}
