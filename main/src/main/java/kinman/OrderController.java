package kinman;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepo;

    @RequestMapping(value = "/api/v1/orders", method = RequestMethod.GET)
    public Map<String, Object> index(HttpServletRequest request) {
        Map<String, Object> results = new HashMap<String, Object>();

        Account account = (Account) request.getAttribute("account");

        List<Order> orders;

        String status = request.getParameter("status");
        if (status == null) {
            orders = orderRepo.findByAccount(account);
        } else {
            orders = orderRepo.findByAccountAndStatus(account, status);
        }

        results.put("orders", orders);

        return results;
    }

    @RequestMapping(value = "/api/v1/orders/{id}", method = RequestMethod.GET)
    public ResponseEntity<Order> show(HttpServletRequest request, @PathVariable Long id) {
        Account account = (Account) request.getAttribute("account");

        // TODO: Adjust mapping to allow fetch of order record only (no orderItems)
        Order order = orderRepo.findByAccountAndId(account, id);

        if (order == null) {
            return new ResponseEntity<Order>(order, HttpStatus.NOT_FOUND);
        }

        String expand = request.getParameter("expand");
        if (expand == null || !expand.equalsIgnoreCase("orderitems")) {
            // ideally, we just wouldn't fetch them in the first place
            order.setOrderItems(null);
        }

        return new ResponseEntity(order, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/v1/orders", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody Order input, HttpServletRequest request) {

        Account account = (Account) request.getAttribute("account");
        input.setAccount(account);

        orderService.create(input);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(input.getId()).toUri());

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

}
