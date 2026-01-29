/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import model.Company;
import model.User;
import model.Profile;
import model.Admin;
import model.DBImplementation;

/**
 * FXML Controller class
 *
 * @author acer
 */
public class DeleteConfirmationViewController implements Initializable {
    
    @FXML
    private Label lblMessage;
    
    @FXML
    private Label lblError;

    @FXML
    private PasswordField passwordField;

    private Stage stage;
    private User userToDelete;  // Cambié de 'user' a 'userToDelete'
    private Company companyToDelete;
    private Profile currentUser;
    private boolean confirmed = false;
    private String adminPassword;
    private String operationType;  // Añadí esta variable
    private DBImplementation dao;  // Añadí esta variable
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.dao = new DBImplementation();  // Inicializar DAO
        lblError.setVisible(false);  // Ocultar mensaje de error inicialmente
    }
     
    public void setUser(User user) {
        this.userToDelete = user;
        this.operationType = "user";
    }
    
    public void setCompanyToDelete(Company company) {
        this.companyToDelete = company;
        this.operationType = "company";
    }
    
    public void setCurrentUser(Profile user) {
        this.currentUser = user;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
    
    @FXML
    private void confirm() {
        String enteredPassword = passwordField.getText();
        
        // Validación básica
        if (enteredPassword == null || enteredPassword.trim().isEmpty()) {
            showError("Por favor, ingrese su contraseña");
            return;
        }
        
        // Verificar contraseña
        if (verifyPassword(enteredPassword)) {
            confirmed = true;
            stage.close();
        } else {
            showError("Contraseña incorrecta");
            passwordField.clear();
        }
    }
    
    private boolean verifyPassword(String enteredPassword) {
        // Si tenemos currentUser, usamos DBImplementation
        if (currentUser != null) {
            Profile verifiedProfile = dao.logIn(currentUser.getUsername(), enteredPassword);
            
            // Verificar que sea un Admin
            if (verifiedProfile != null && verifiedProfile instanceof Admin) {
                return true;
            }
            return false;
        }
        
        // Método antiguo para compatibilidad
        if (adminPassword != null) {
            return enteredPassword.equals(adminPassword);
        }
        
        return false;
    }
    
    
    public String getPassword() {
        return passwordField.getText();
    }

    @FXML
    private void cancel() {
        confirmed = false;
        stage.close();
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }
    
    // Getters
    public boolean isConfirmed() {
        return confirmed;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    // Métodos para obtener lo que se va a eliminar
    public User getUserToDelete() {
        return userToDelete;
    }
    
    public Company getCompanyToDelete() {
        return companyToDelete;
    }
}
