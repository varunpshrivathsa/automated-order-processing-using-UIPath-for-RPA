package kinman;

import java.math.BigDecimal;

public class Inventory {

    private String sku;

    private BigDecimal price;

    private int qty;

    public Inventory(String sku, BigDecimal price, int qty) {
        this.sku = sku;
        this.price = price;
        this.qty = qty;
    }

    public String getSku() {
        return sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQty() {
        return qty;
    }
}
