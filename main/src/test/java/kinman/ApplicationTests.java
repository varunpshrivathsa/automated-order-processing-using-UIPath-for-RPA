package kinman;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ApplicationTests {
    @Autowired
    OrderService orderService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    ApiKeyHandlerInterceptor handlerInterceptor;

	@Test
	public void contextLoads() {
	}

	@Test
    public void canCreateOrder() {
        Order order = new Order();

        List<OrderItem> orderItems = new ArrayList<>();

        OrderItem oranges = new OrderItem();
        oranges.setSku("Orange");
        oranges.setQty(3);

        orderItems.add(oranges);

        order.setOrderItems(orderItems);

        Order newOrder = orderService.create(order);

        assertNotNull((newOrder.getId()));
        assertEquals("open", newOrder.getStatus());

        OrderItem orderItem = newOrder.getOrderItems().get(0);
        assertEquals("Orange", orderItem.getSku());
        assertEquals("10.00", orderItem.getPrice().toString());
        assertEquals(3, orderItem.getQty());
        assertEquals("30.00", orderItem.getExtPrice().toString());

        // confirm that inventory was adjusted
        Map<String, Inventory> inventory = inventoryService.inventoryForSkus(order.getSkus());
        assertEquals(97, inventory.get("Orange").getQty());
    }

    @Test
    public void orderInFailedStatusWhenInsufficientInventory() {
        Order order = new Order();

        List<OrderItem> orderItems = new ArrayList<>();

        OrderItem oranges = new OrderItem();
        oranges.setSku("Orange");
        oranges.setQty(101);

        orderItems.add(oranges);

        order.setOrderItems(orderItems);

        Order newOrder = orderService.create(order);

        assertEquals("failed", newOrder.getStatus());
    }

    @Test
    public void orderInFailedStatusWhenUnknownSku() {
        Order order = new Order();

        List<OrderItem> orderItems = new ArrayList<>();

        OrderItem item = new OrderItem();
        item.setSku("Grapefruit");
        item.setQty(1);

        orderItems.add(item);

        order.setOrderItems(orderItems);

        Order newOrder = orderService.create(order);

        assertEquals("failed", newOrder.getStatus());
    }

    @Test
    public void canCheckInventory() {
        List<String> skus = new ArrayList<>();
        skus.add("Orange");

        Map<String, Inventory> result = inventoryService.inventoryForSkus(skus);

        Inventory oranges = result.get("Orange");
        assertEquals(100, oranges.getQty());
        assertEquals("10.00", oranges.getPrice().toString());
    }

    @Test
    public void canCheckInventoryMultiple() {
        List<String> skus = new ArrayList<>();
        skus.add("Orange");
        skus.add("Bananas");

        Map<String, Inventory> result = inventoryService.inventoryForSkus(skus);

        Inventory oranges = result.get("Orange");
        assertEquals(100, oranges.getQty());
        assertEquals("10.00", oranges.getPrice().toString());

        Inventory bananas = result.get("Bananas");
        assertEquals(0, bananas.getQty());
        assertEquals("10.00", bananas.getPrice().toString());
    }

    @Test
    public void preHandleWorksWhenNoApiKey() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // throws 401 when no API key
        handlerInterceptor.preHandle(request, response, "Unused Handler");
        assertEquals(401, response.getStatus());
    }

    @Test
    public void preHandleWorksWhenBadApiKey() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // success when valid API key
        request.addHeader("kinman-api-key", "notagoodkey");
        handlerInterceptor.preHandle(request, response, "Unused Handler");
        assertEquals(403, response.getStatus());
    }

    @Test
    public void preHandleWorksWhenGoodApiKey() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // success when valid API key
        request.addHeader("kinman-api-key", "greengrocer123");
        handlerInterceptor.preHandle(request, response, "Unused Handler");
        assertEquals(200, response.getStatus());
    }

}
