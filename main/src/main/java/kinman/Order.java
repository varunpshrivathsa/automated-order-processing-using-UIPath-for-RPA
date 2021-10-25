package kinman;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT)
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    @Id
    @GeneratedValue
    private long id;

    private String status;

    @JsonIgnore
    @ManyToOne
    private Account account;

    private Date createdAt = new Date();

    @OneToMany(mappedBy = "order", cascade = {CascadeType.ALL})
    private List<OrderItem> orderItems;

    public long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Account getAccount() { return account; }

    public void setAccount(Account account) { this.account = account; }

    public Date getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    @JsonIgnore
    public List<String> getSkus() {
        List<String> skus = new ArrayList<>();

        for (OrderItem item : orderItems) {
            skus.add(item.getSku());
        }

        return skus;
    }
}
