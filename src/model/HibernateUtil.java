package model;

import model.*;
import model.Size;
import org.hibernate.SessionFactory;
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
}
