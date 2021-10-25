package kinman;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class OrderService {
    private OrderRepository repo;

    private InventoryService inventoryService;

    public OrderService(OrderRepository orderRepository, InventoryService inventoryService) {
        this.repo = orderRepository;
        this.inventoryService = inventoryService;
    }

    public Order create(Order order) {
        List<String> skus = order.getSkus();

        order.setStatus("open"); // happy path

        if (skus.isEmpty()) {
            // Order must have at least one SKU on it.
            order.setStatus("failed");
        }

        // prices
        Map<String, Inventory> inventoryItems = inventoryService.inventoryForSkus(skus);

        for (OrderItem item : order.getOrderItems()) {
            Inventory inventoryForSku = inventoryItems.get(item.getSku());

            item.setOrder(order);

            // if item isn't in our catalog, fail the order and keep processing
            if (inventoryForSku == null) {
                order.setStatus("failed");
                continue;
            }

            // if we don't have enough, fail the order but continue processing
            if (item.getQty() > inventoryForSku.getQty()) {
                order.setStatus("failed");
            } else {
                item.setPrice(inventoryForSku.getPrice());
                item.setExtPrice(item.getPrice().multiply(new BigDecimal(item.getQty())));

                // order will be placed, so adjust inventory.
                // TODO: None of this is particularly thread-safe
                inventoryService.decrementInventory(item.getSku(), item.getQty());
            }
        }

        repo.save(order);

        return order;
    }
}
