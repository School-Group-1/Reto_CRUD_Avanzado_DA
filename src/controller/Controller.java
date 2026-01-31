/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import model.ClassDAO;
import model.Company;
import model.Product;
import model.Profile;
import model.Purchase;
import model.Size;

/**
 * Controller class that handles interaction between the GUI and the database.
 * Provides login, signup, deletion, modification, and data retrieval methods.
 *
 * Author: acer
 */
public class Controller {
    
    private ClassDAO dao;

    /**
     * Constructor for Controller.
     *
     * @param dao The DAO implementation to handle database operations
     */
    public Controller(ClassDAO dao) {
        this.dao = dao;
    }

    /**
     * Attempts to log in a user or admin.
     *
     * @param username The username
     * @param password The password
     * @return Profile object if login succeeds, null otherwise
     */
    public Profile logIn(String username, String password) {
        return dao.logIn(username, password);
    }

    /**
     * Signs up a new user.
     *
     * @return true if signup succeeds, false otherwise
     */
    public Boolean signUp(String gender, String cardNumber, String username, String password, String email,
            String name, String telephone, String surname) {
        return dao.signUp(gender, cardNumber, username, password, email, name, telephone, surname);
    }

    /**
     * Deletes a user account.
     */
    public Boolean dropOutUser(String username, String password) {
        return dao.dropOutUser(username, password);
    }
    
    public Boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword) {
        return dao.dropOutAdmin(usernameToDelete, adminUsername, adminPassword);
    }

    /**
     * Modifies user information.
     */
    public Boolean modificarUser(String password, String email, String name, String telephone, String surname, String username, String gender) {
        return dao.modificarUser(password, email, name, telephone, surname, username, gender);
    }

    /**
     * Retrieves a list of usernames for GUI combo boxes.
     */
    public List comboBoxInsert() {
        return dao.comboBoxInsert();
    }
    
    public List<Product> findAllProducts() {
        return dao.findAllProducts();
    }
    
    public void saveProduct(Product product) {
        dao.saveProduct(product);
    }
    
    public void deleteProduct(Product product) {
        dao.deleteProduct(product);
    }
    
    public void updateProduct(Product product) {
        dao.updateProduct(product);
    }
    
    public List<Product> findProductsByCompany(Company company) {
        return dao.findProductsByCompany(company);
    }
    
    public List<Size> findProductSizes(Product product) {
        return dao.findProductSizes(product);
    }
    
    public List<Company> findAllCompanies() {
        return dao.findAllCompanies();
    }
    
    public List<Purchase> findSizePurchases(Size size) {
        return dao.findSizePurchases(size);
    }
    
    public Size modifySize(Size size, String newLabel, int newStock) {
        return dao.modifySize(size, newLabel, newStock);
    }
    
    public Size createSize(String label, int stock, Product product) {
        return dao.createSize(label, stock, product);
    }
    
    public void deleteSize(Size size) {
        dao.deleteSize(size);
    }
    
    public List<Purchase> findProductPurchases(Product product) {
        return dao.findProductPurchases(product);
    }
}
