/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import javafx.collections.ObservableList;
import model.CartItem;
import model.ClassDAO;
import model.Company;
import model.Product;
import model.Profile;
import model.Purchase;
import model.Size;
import model.User;

/**
 * Main controller class that handles the interaction between the GUI and the database.
 * Provides methods for login, signup, deletion, modification, and data retrieval.
 * Acts as a bridge between the view and the DAO.
 */
public class Controller {

    private ClassDAO dao;

    /**
     * Constructor for Controller.
     *
     * @param dao the DAO implementation used for database operations
     */
    public Controller(ClassDAO dao) {
        this.dao = dao;
    }

    /**
     * Attempts to log in a user or admin.
     *
     * @param username the username
     * @param password the password
     * @return the Profile if login succeeds, null otherwise
     */
    public Profile logIn(String username, String password) {
        return dao.logIn(username, password);
    }

    /**
     * Signs up a new user.
     *
     * @param gender the user's gender
     * @param cardNumber the card number
     * @param username the desired username
     * @param password the desired password
     * @param email the email address
     * @param name the user's first name
     * @param telephone the telephone number
     * @param surname the user's surname
     * @return true if signup succeeds, false otherwise
     */
    public Boolean signUp(String gender, String cardNumber, String username, String password, String email,
                          String name, String telephone, String surname) {
        return dao.signUp(gender, cardNumber, username, password, email, name, telephone, surname);
    }

    /**
     * Deletes a user account.
     *
     * @param username the username to delete
     * @param password the user's password
     * @return true if deletion succeeds, false otherwise
     */
    public Boolean dropOutUser(String username, String password) {
        return dao.dropOutUser(username, password);
    }

    /**
     * Deletes an admin account.
     *
     * @param usernameToDelete the admin username to delete
     * @param adminUsername the performing admin's username
     * @param adminPassword the performing admin's password
     * @return true if deletion succeeds, false otherwise
     */
    public Boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword) {
        return dao.dropOutAdmin(usernameToDelete, adminUsername, adminPassword);
    }

    /**
     * Modifies user information.
     *
     * @param password new password
     * @param email new email
     * @param name new first name
     * @param telephone new telephone
     * @param surname new surname
     * @param username username of the user to modify
     * @param gender new gender
     * @return true if modification succeeds, false otherwise
     */
    public Boolean modificarUser(String password, String email, String name, String telephone, String surname, String username, String gender) {
        return dao.modificarUser(password, email, name, telephone, surname, username, gender);
    }

    /**
     * Retrieves a list of usernames for combo boxes in the GUI.
     *
     * @return a list of usernames
     */
    public List comboBoxInsert() {
        return dao.comboBoxInsert();
    }

    /**
     * Retrieves all products in the database.
     *
     * @return list of products
     */
    public List<Product> findAllProducts() {
        return dao.findAllProducts();
    }

    /**
     * Saves a new product.
     *
     * @param product the product to save
     */
    public void saveProduct(Product product) {
        dao.saveProduct(product);
    }

    /**
     * Saves product sizes with initial stock.
     *
     * @param product the product
     * @param sizeLabels list of size labels
     * @param initialStock initial stock for each size
     */
    public void saveProductSizes(Product product, List<String> sizeLabels, int initialStock) {
        dao.saveProductSizes(product, sizeLabels, initialStock);
    }

    /**
     * Deletes a product.
     *
     * @param product the product to delete
     */
    public void deleteProduct(Product product) {
        dao.deleteProduct(product);
    }

    /**
     * Updates a product's information.
     *
     * @param product the product to update
     */
    public void updateProduct(Product product) {
        dao.updateProduct(product);
    }

    /**
     * Retrieves products for a specific company.
     *
     * @param company the company
     * @return list of products
     */
    public List<Product> findProductsByCompany(Company company) {
        return dao.findProductsByCompany(company);
    }

    /**
     * Retrieves sizes for a product.
     *
     * @param product the product
     * @return list of sizes
     */
    public List<Size> findProductSizes(Product product) {
        return dao.findProductSizes(product);
    }

    /**
     * Retrieves all companies.
     *
     * @return list of companies
     */
    public List<Company> findAllCompanies() {
        return dao.findAllCompanies();
    }

    /**
     * Retrieves all users as ObservableList.
     *
     * @return list of users
     */
    public ObservableList<User> findAll() {
        return dao.findAll();
    }

    /**
     * Saves a new user.
     *
     * @param user the user to save
     */
    public void saveUser(User user) {
        dao.saveUser(user);
    }

    /**
     * Updates a user.
     *
     * @param user the user to update
     */
    public void updateUser(User user) {
        dao.updateUser(user);
    }

    /**
     * Updates a company.
     *
     * @param company the company to update
     */
    public void updateCompany(Company company) {
        dao.updateCompany(company);
    }

    /**
     * Saves a new company.
     *
     * @param company the company to save
     */
    public void saveCompany(Company company) {
        dao.saveCompany(company);
    }

    /**
     * Deletes a company.
     *
     * @param company the company to delete
     */
    public void deleteCompany(Company company) {
        dao.deleteCompany(company);
    }

    /**
     * Retrieves all users.
     *
     * @return list of users
     */
    public List<User> findAllUsers() {
        return dao.findAllUsers();
    }

    /**
     * Finds a company by name.
     *
     * @param name the company name
     * @return the company object
     */
    public Company findCompanyByName(String name) {
        return dao.findCompanyByName(name);
    }

    /**
     * Retrieves purchases for a given size.
     *
     * @param size the size
     * @return list of purchases
     */
    public List<Purchase> findSizePurchases(Size size) {
        return dao.findSizePurchases(size);
    }

    /**
     * Modifies a size.
     *
     * @param size the size to modify
     * @param newLabel new label
     * @param newStock new stock
     * @return modified size
     */
    public Size modifySize(Size size, String newLabel, int newStock) {
        return dao.modifySize(size, newLabel, newStock);
    }

    /**
     * Creates a new size for a product.
     *
     * @param label size label
     * @param stock initial stock
     * @param product associated product
     * @return created size
     */
    public Size createSize(String label, int stock, Product product) {
        return dao.createSize(label, stock, product);
    }

    /**
     * Deletes a size.
     *
     * @param size the size to delete
     */
    public void deleteSize(Size size) {
        dao.deleteSize(size);
    }

    /**
     * Retrieves purchases for a product.
     *
     * @param product the product
     * @return list of purchases
     */
    public List<Purchase> findProductPurchases(Product product) {
        return dao.findProductPurchases(product);
    }
    
    public User findUserByUsername(String username) {
        return dao.findUserByUsername(username);
    }

    public void lowerStock(CartItem ci) {
        dao.lowerStock(ci);
    }

}
