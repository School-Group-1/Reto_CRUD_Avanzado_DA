package model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import static main.Main.generateDailyPurchases;
import model.*;
import model.Size;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    private static SessionFactory sessionFactory = null;
    private static ServiceRegistry serviceRegistry = null;

    public static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure("/resources/hibernate.cfg.xml");

        configuration.addAnnotatedClass(Profile.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Admin.class);
        configuration.addAnnotatedClass(Company.class);
        configuration.addAnnotatedClass(Purchase.class);
        configuration.addAnnotatedClass(Size.class);
        configuration.addAnnotatedClass(Product.class);

        serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        return sessionFactory;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            buildSessionFactory();
        }
        return sessionFactory;
    }

    public static void closeSessionFactory() {
        getSessionFactory().close();
    }

    public static void initializeData() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        User user1 = new User("Male", "123456789", "username1", "1234", "user1@example.com", "John", "123456789", "Doe");
        User user2 = new User("Female", "123456789", "username2", "1234", "user2@example.com", "Jane", "123456789", "Doe");
        User user3 = new User("Male", "123456789", "username3", "1234", "user3@example.com", "Joe", "123456789", "Doe");

        Admin admin1 = new Admin("12456789", "admin1", "1234", "admin1@example.com", "John", "123456789", "Doe");
        Admin admin2 = new Admin("12456789", "admin2", "1234", "admin1@example.com", "Jane", "123456789", "Doe");
        Admin admin3 = new Admin("12456789", "admin3", "1234", "admin1@example.com", "John", "123456789", "Doe");

        Company company1 = new Company("Company 1", "123", "Bilbao", "https://www.youtube.com/");
        Company company2 = new Company("Company 2", "456", "Bilbao", "https://www.linkedin.com/feed/");
        Company company3 = new Company("Company 3", "789", "Bilbao", "https://github.com/");

        Product product1 = new Product("Bald Shirt", 19.99, "Shirt", "A cool shirt.", "/images/baldinkent.png", company1);
        Product product2 = new Product("Bald Shirt", 39.99, "Shirt", "A cool shirt.", "/images/baldinkent.png", company1);
        Product product3 = new Product("Blue Shirt", 19.99, "Shirt", "A cool shirt.", "/images/exampleShirt1.png", company2);
        Product product4 = new Product("Shoes", 19.99, "Shoes", "A cool pair of shoes.", "/images/exampleShoes.png", company2);
        Product product5 = new Product("Bald Shirt", 19.99, "Shirt", "A cool shirt.", "/images/baldinkent.png", company3);
        Product product6 = new Product("Shoes", 19.99, "Shoes", "A cool pair of shoes.", "/images/exampleShoes.png", company3);

        Size size1_1 = new Size("SM", 3, product1);
        Size size1_2 = new Size("MD", 5, product1);
        Size size1_3 = new Size("LG", 2, product1);
        product1.getSizes().add(size1_1);
        product1.getSizes().add(size1_2);
        product1.getSizes().add(size1_3);
        Size size2_1 = new Size("SM", 4, product2);
        Size size2_2 = new Size("MD", 6, product2);
        Size size2_3 = new Size("LG", 3, product2);
        product2.getSizes().add(size2_1);
        product2.getSizes().add(size2_2);
        product2.getSizes().add(size2_3);
        Size size3_1 = new Size("SM", 2, product3);
        Size size3_2 = new Size("MD", 4, product3);
        Size size3_3 = new Size("LG", 5, product3);
        product3.getSizes().add(size3_1);
        product3.getSizes().add(size3_2);
        product3.getSizes().add(size3_3);
        Size size4_1 = new Size("SM", 3, product4);
        Size size4_2 = new Size("MD", 5, product4);
        Size size4_3 = new Size("LG", 2, product4);
        product4.getSizes().add(size4_1);
        product4.getSizes().add(size4_2);
        product4.getSizes().add(size4_3);
        Size size5_1 = new Size("SM", 3, product5);
        Size size5_2 = new Size("MD", 4, product5);
        Size size5_3 = new Size("LG", 2, product5);
        product5.getSizes().add(size5_1);
        product5.getSizes().add(size5_2);
        product5.getSizes().add(size5_3);
        Size size6_1 = new Size("SM", 2, product6);
        Size size6_2 = new Size("MD", 3, product6);
        Size size6_3 = new Size("LG", 1, product6);
        product6.getSizes().add(size6_1);
        product6.getSizes().add(size6_2);
        product6.getSizes().add(size6_3);

        session.save(user1);
        session.save(user2);
        session.save(user3);

        session.save(admin1);
        session.save(admin2);
        session.save(admin3);

        session.save(company1);
        session.save(company2);
        session.save(company3);

        session.save(product1);
        session.save(product2);
        session.save(product3);
        session.save(product4);
        session.save(product5);
        session.save(product6);

        session.save(size1_1);
        session.save(size1_2);
        session.save(size1_3);
        session.save(size2_1);
        session.save(size2_2);
        session.save(size2_3);
        session.save(size3_1);
        session.save(size3_2);
        session.save(size3_3);
        session.save(size4_1);
        session.save(size4_2);
        session.save(size4_3);
        session.save(size5_1);
        session.save(size5_2);
        session.save(size5_3);
        session.save(size6_1);
        session.save(size6_2);
        session.save(size6_3);

        List<User> users = Arrays.asList(user1, user2, user3);

        List<Size> allSizes = Arrays.asList(
                size1_1, size1_2, size1_3,
                size2_1, size2_2, size2_3,
                size3_1, size3_2, size3_3,
                size4_1, size4_2, size4_3,
                size5_1, size5_2, size5_3,
                size6_1, size6_2, size6_3
        );

        Random random = new Random();

        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        for (Size size : allSizes) {

            int usersForThisSize = random.nextInt(users.size()) + 1;

            Collections.shuffle(users);

            for (int i = 0; i < usersForThisSize; i++) {
                User user = users.get(i);

                generateDailyPurchases(
                        user,
                        size,
                        startDate,
                        endDate,
                        2,
                        session
                );
            }
        }

        tx.commit();
        session.close();
    }
}
