/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import model.Company;
import model.User;
import model.Admin;
import model.Profile;
import controller.Controller;

import java.net.URL;
import java.util.ResourceBundle;

public class DeleteConfirmationViewController implements Initializable {
    
    @FXML
    private Label lblMessage;
    @FXML
    private PasswordField passwordField;

    private Stage stage;
    private User userToDelete;
    private Company companyToDelete;
    private Admin admin;
    private boolean confirmed = false;
    private String adminUsername;  
    private String enteredPassword; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización básica
    }
    
    public void initData(Profile profile, Controller cont) {
        if (profile instanceof Admin) {
            this.admin = (Admin) profile;
            this.adminUsername = admin.getUsername();
        }
    }
    
    public void setUserToDelete(User user) {
        this.userToDelete = user;
        
    }
    
    public void setCompanyToDelete(Company company) {
        this.companyToDelete = company;
        
    }
    
    @FXML
    private void confirm() {
        enteredPassword = passwordField.getText();
        
        if (admin != null && admin.getPassword().equals(passwordField.getText())) {
            confirmed = true;
        }
        closeWindow();
    }
    
    @FXML
    private void cancel() {
        closeWindow();
    }
    
    private void closeWindow() {
        if (stage != null) {
            stage.close();
        } else {
            ((Stage) passwordField.getScene().getWindow()).close();
        }
    }
    
    public String getAdminUsername() {
        return adminUsername;
    }
    
    public String getEnteredPassword() {
        return enteredPassword;
    }
    
    public User getUserToDelete() {
        return userToDelete;
    }
    
    public Company getCompanyToDelete() {
        return companyToDelete;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
