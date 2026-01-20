package model;

import java.sql.PreparedStatement;
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
    private String driverDB;
    private String urlDB;
    private String userDB;
    private String passwordDB;
    private final Session session = HibernateUtil.getSessionFactory().openSession();

    // HQL statements
    final String SQLMODIFYPROFILE = "UPDATE PROFILE_ P SET P.PASSWORD_ = ?, P.EMAIL = ?, P.NAME_ = ?, P.TELEPHONE = ?, P.SURNAME = ? WHERE USERNAME = ?;";
    final String SQLMODIFYUSER = "UPDATE USER_ U SET U.GENDER = ? WHERE USERNAME = ?";

    private final String SLQSELECTNUSER = "SELECT u.USERNAME FROM USER_ u;";

    private final String HQL_GET_PROFILE_USERNAME = "select p from Profile p where p.username = :username";

    private final String HQL_GET_USERS_USERNAME = "select u.username from User u";
    private final String HQL_GET_USER_USERNAME = "select u from User u where u.username = :username";
    private final String HQL_GET_USER_USERNAME_PASSWORD = "select u from User u where u.username = :username and u.password = :password";
    private final String HQL_GET_ADMIN_USERNAME_PASSWORD = "select a from Admin a where a.username = :username and a.password = :password";

    /**
     * Default constructor that loads DB configuration.
     */
    public DBImplementation() {
        this.configFile = ResourceBundle.getBundle("model.configClass");
        this.driverDB = this.configFile.getString("Driver");
        this.urlDB = this.configFile.getString("Conn");
        this.userDB = this.configFile.getString("DBUser");
        this.passwordDB = this.configFile.getString("DBPass");
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
    
    public List<Product> findProductsByCompany(Company company) {
        Query query = session.createQuery(
            "from Product p where p.company = :company"
        );
        query.setParameter("company", company);
        return query.list();
    }

}
