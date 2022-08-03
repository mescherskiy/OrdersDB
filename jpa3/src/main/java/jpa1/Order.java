package jpa1;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "orders_products",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products;

    private Integer totalSum;

    public Order() {
    }

    public Order(Client client) {
        this.client = client;
    }

    public Order(Client client, List<Product> products) {
        this.client = client;
        this.products = products;
        calculateTotalSum();
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalSum() {
        return totalSum;
    }

    private void setTotalSum(Integer totalSum) {
        this.totalSum = totalSum;
    }

    public void calculateTotalSum() {
        Integer sum = 0;
        for (Product product : products) {
            sum += product.getPrice() * product.getAmount();
        }
        setTotalSum(sum);
    }
}