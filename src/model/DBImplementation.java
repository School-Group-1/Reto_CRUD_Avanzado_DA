package model;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 * Implementation of ClassDAO using database operations. Handles all database
 * interactions for users and admins. Provides login, signup, deletion,
 * modification, and retrieval of usernames.
 *
 * Author: acer
 */
public class DBImplementation implements ClassDAO {

    private PreparedStatement stmt;

    // Configuration for database connection
    private ResourceBundle configFile;
    private Session session;

    // HQL statements
    final String SQLMODIFYPROFILE = "UPDATE PROFILE_ P SET P.PASSWORD_ = ?, P.EMAIL = ?, P.NAME_ = ?, P.TELEPHONE = ?, P.SURNAME = ? WHERE USERNAME = ?;";
    final String SQLMODIFYUSER = "UPDATE USER_ U SET U.GENDER = ? WHERE USERNAME = ?";

    private final String HQL_GET_PROFILE_USERNAME = "select p from Profile p where p.username = :username";

    private final String HQL_GET_USERS_USERNAME = "select u.username from User u";
    private final String HQL_GET_USER_USERNAME = "select u from User u where u.username = :username";
    private final String HQL_GET_USER_USERNAME_PASSWORD = "select u from User u where u.username = :username and u.password = :password";
    private final String HQL_GET_ADMIN_USERNAME_PASSWORD = "select a from Admin a where a.username = :username and a.password = :password";

    /**
     * Default constructor that loads DB configuration.
     */
    public DBImplementation() {
        this.session = HibernateUtil.getSessionFactory().openSession();
    }

    /**
     * Logs in a user or admin from the database.
     *
     * @param username The username to log in
     * @param password The password to validate
     * @return Profile object (User or Admin) if found, null otherwise
     */
    @Override
    public Profile logIn(String username, String password) {
        Query query = session.createQuery(HQL_GET_USER_USERNAME_PASSWORD);
        query.setParameter("username", username);
        query.setParameter("password", password);
        List result = query.list();

        if (result.isEmpty()) {
            query = session.createQuery(HQL_GET_ADMIN_USERNAME_PASSWORD);
            query.setParameter("username", username);
            query.setParameter("password", password);
            result = query.list();

            if (!result.isEmpty()) {
                System.out.println("Admin: " + result.get(0));
                return (Admin) result.get(0);
            }
        } else {
            System.out.println("User: " + result.get(0));
            return (User) result.get(0);
        }

        return null;
    }

    /**
     * Signs up a new user in the database.
     *
     * @param gender
     * @param cardNumber
     * @param username
     * @param password
     * @param surname
     * @param name
     * @param email
     * @param telephone
     * @return true if signup was successful, false otherwise
     */
    @Override
    public Boolean signUp(String gender, String cardNumber, String username, String password, String email, String name, String telephone, String surname) {
        try {
            User user = new User(gender, cardNumber, username, password, email, name, telephone, surname);
            Transaction tx = session.beginTransaction();
            session.save(user);
            tx.commit();
            return true;
        } catch (PersistenceException e) {
            System.out.println("Verify data.");
        }

        return false;
    }

    /**
     * Deletes a standard user from the database.
     *
     * @param username
     * @param password
     * @return true if deleted, false otherwise
     */
    @Override
    public Boolean dropOutUser(String username, String password) {
        Query query = session.createQuery(HQL_GET_USER_USERNAME_PASSWORD);
        query.setParameter("username", username);
        query.setParameter("password", password);
        List result = query.list();

        if (!result.isEmpty()) {
            User user = (User) result.get(0);

            Transaction tx = session.beginTransaction();
            session.delete(user);
            tx.commit();

            return true;
        } else {
            return false;
        }
    }

    /**
     * Deletes a user selected by admin from the database.
     *
     * @param usernameToDelete
     * @param adminUsername
     * @param adminPassword
     * @return true if user is deleted, false otherwise
     */
    @Override
    public Boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword) {
        Query query = session.createQuery(HQL_GET_ADMIN_USERNAME_PASSWORD);
        query.setParameter("username", adminUsername);
        query.setParameter("password", adminPassword);
        List result = query.list();

        if (!result.isEmpty()) {
            query = session.createQuery(HQL_GET_USER_USERNAME);
            query.setParameter("username", usernameToDelete);
            result = query.list();

            if (!result.isEmpty()) {
                User user = (User) result.get(0);
                Transaction tx = session.beginTransaction();
                session.delete(user);
                tx.commit();
                return true;
            }
        }

        return false;
    }

    /**
     * Modifies the information of a user in the database.
     *
     * @param password
     * @param email
     * @param username
     * @param telephone
     * @param surname
     * @param name
     * @param gender
     * @return true if user is modified, false otherwise
     */
    @Override
    public Boolean modificarUser(String password, String email, String name, String telephone, String surname, String username, String gender) {
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_PROFILE_USERNAME);
        query.setParameter("username", username);
        List result = query.list();

        if (!result.isEmpty()) {
            Profile profile = (Profile) result.get(0);
            profile.setPassword(password);
            profile.setName(name);
            profile.setTelephone(telephone);
            profile.setSurname(surname);

            session.update(profile);

            query = session.createQuery(HQL_GET_USER_USERNAME);
            query.setParameter("username", username);
            result = query.list();

            if (!result.isEmpty()) {
                User user = (User) result.get(0);
                user.setGender(gender);

                session.update(user);
            }

            tx.commit();

            return true;
        }

        return false;
    }

    public void updateUser(User user) {
        Transaction tx = session.beginTransaction();
        session.update(user);
        tx.commit();
    }

    public void deleteUser(User user) {
        Transaction tx = session.beginTransaction();
        session.delete(user);
        tx.commit();
    }

    public void saveUser(User user) {
        Transaction tx = session.beginTransaction();
        session.save(user);
        tx.commit();
    }

    /**
     * Retrieves a list of usernames from the database.
     *
     * @return List of usernames
     */
    @Override
    public List comboBoxInsert() {
        Query query = session.createQuery(HQL_GET_USERS_USERNAME);
        List<String> listaUsuarios = query.list();
        System.out.println(listaUsuarios);

        return listaUsuarios;
    }

    public ObservableList<User> findAll() {
        ObservableList<User> users = FXCollections.observableArrayList();

        try {
            Query query = session.createQuery("from User");
            List<User> result = query.list();
            users.addAll(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public List<Product> findAllProducts() {
        Query query = session.createQuery("FROM Product");
        return query.list();
    }

    public void saveProduct(Product product) {
        Transaction tx = session.beginTransaction();
        session.save(product);
        tx.commit();
    }

    public void updateProduct(Product product) {
        Transaction tx = session.beginTransaction();
        session.update(product);
        tx.commit();
    }
    
    public void deleteProduct(Product product) {
        Transaction tx = session.beginTransaction();
        session.delete(product);
        tx.commit();
    }
    
    
    public void saveCompany(Company company) {
        Transaction tx = session.beginTransaction();
        session.save(company);
        tx.commit();
    }
    
     public void updateCompany(Company company) {
        Transaction tx = session.beginTransaction();
        session.update(company);
        tx.commit();
    }
     
    public void deleteCompany(Company company) {
        Transaction tx = session.beginTransaction();
        session.delete(company);
        tx.commit();
    }

    
    public List<Product> findProductsByCompany(Company company) {
        Query query = session.createQuery(
            "from Product p where p.company = :company"
        );
        query.setParameter("company", company);
        return query.list();
    }
    
    public List<Size> findProductSizes(Product product) {
        Query query = session.createQuery("from Size s WHERE s.product = :product");
        query.setParameter("product", product);
        return query.list();
    }
    
    public Size modifySize(Size size, String newLabel, int newStock) {
        Transaction tx = session.beginTransaction();
        size.setLabel(newLabel);
        size.setStock(newStock);
        session.update(size);
        tx.commit();
        return size;
    }
    
    public Size createSize(String label, int stock, Product product) {
        Transaction tx = session.beginTransaction();
        Size size = new Size(label, stock, product);
        session.save(size);
        tx.commit();
        return size;
    }
    
    public void deleteSize(Size size) {
        Transaction tx = session.beginTransaction();
        session.delete(size);
        tx.commit();
    }

    public List<Company> findAllCompanies() {
        Query query = session.createQuery("FROM Company");
        return query.list();
    }
    
    public List<Purchase> findSizePurchases(Size size) {
        Query query = session.createQuery("FROM Purchase p WHERE p.size = :size");
        query.setParameter("size", size);
        return query.list();
    }
    
    public List<Purchase> findProductPurchases(Product product) {
        List<Purchase> size_purchases = new ArrayList<>();
        
        Query query = session.createQuery("FROM Size s WHERE s.product = :product");
        query.setParameter("product", product);
        List<Size> sizes = query.list();
        
        for(Size s:sizes) {
            query = session.createQuery("FROM Purchase p WHERE p.size = :size");
            query.setParameter("size", s);
            List<Purchase> purchases = query.list();
            
            size_purchases.addAll(purchases);
        }
        
        return size_purchases;
    }
}
