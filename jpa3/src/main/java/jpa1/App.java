package jpa1;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class App {
    static EntityManagerFactory emf;
    static EntityManager em;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            // create connection
            emf = Persistence.createEntityManagerFactory("JPATest");
            em = emf.createEntityManager();
            try {
                while (true) {
                    System.out.println("1. Create new client");
                    System.out.println("2. Add new product");
                    System.out.println("3. Make an order");
                    System.out.print("-> ");
                    switch (sc.nextLine()) {
                        case "1":
                            createNewClient(sc);
                            break;
                        case "2":
                            addNewProduct(sc);
                            break;
                        case "3":
                            makeOrder(sc);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    protected static <T> T performTransaction(Callable<T> action) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            T result = action.call();
            transaction.commit();

            return result;
        } catch (Exception ex) {
            if (transaction.isActive())
                transaction.rollback();

            throw new RuntimeException(ex);
        }
    }


    public static void createNewClient(Scanner scanner) {
        System.out.println("Enter login: ");
        String login = scanner.nextLine();
        System.out.println("Enter phone number: ");
        String phone = scanner.nextLine();
        System.out.println("Enter address: ");
        String address = scanner.nextLine();
        final Client client =  new Client(login, phone, address);

        performTransaction(() -> {
        em.persist(client);
        System.out.println("Client " + client.getLogin() + " has been successfully added!");
        return client;
        });
    }

    public static void addNewProduct(Scanner sc) {
        System.out.println("Enter product name: ");
        String name = sc.nextLine();
        System.out.println("Enter price: ");
        Integer price = stringToInt(sc);
        final Product product = new Product(name, price);
        performTransaction(() -> {
           em.persist(product);
           System.out.println("New product has been added: " + product.getName() + "(" + product.getPrice() + "₴)");
           return product;
        });
    }


    public static void makeOrder(Scanner sc) {
        System.out.println("Enter client's ID: ");
        Long clientID = stringToLong(sc);
        System.out.println("Enter product's ID: ");
        Long productID = stringToLong(sc);
        System.out.println("Enter amount: ");
        Integer amount = stringToInt(sc);
        Client client = em.getReference(Client.class, clientID);
        List<Product> products = new ArrayList<>();
        Product product = em.getReference(Product.class, productID);
        product.setAmount(amount);
        products.add(product);
        while(true) {
            System.out.println("If you want to add more products - enter it's ID, if not - press 0");
            Long chose = stringToLong(sc);
            if (chose <= 0) {
                break;
            }
            System.out.println("Enter amount: ");
            amount = stringToInt(sc);
            Product anotherProduct = em.getReference(Product.class, chose);
            anotherProduct.setAmount(amount);
            products.add(anotherProduct);
        }
        final Order order = new Order(client, products);
        performTransaction(() -> {
            em.persist(order);
            System.out.println("Your order: ");
            for (Product prod : products) {
                System.out.println(prod);
            }
            System.out.println("Total cost: " + order.getTotalSum() + "₴");
            return order;
        });
    }

    public static Long stringToLong(Scanner sc) {
        Long num;
        while (true) {
            try {
                return (num = Long.parseLong(sc.nextLine()));
            } catch (NumberFormatException e) {
                System.out.println("Wrong format! Type only numbers: ");
            }
        }
    }

    public static Integer stringToInt(Scanner sc) {
        Integer num;
        while (true) {
            try {
                return (num = Integer.parseInt(sc.nextLine()));
            } catch (NumberFormatException e) {
                System.out.println("Wrong format! Type only numbers: ");
            }
        }
    }

}


