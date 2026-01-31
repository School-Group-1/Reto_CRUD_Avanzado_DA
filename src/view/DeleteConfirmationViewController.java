/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import model.Company;
import model.User;
import model.Profile;
import model.Admin;
import model.DBImplementation;
import model.Product;

public class DeleteConfirmationViewController implements Initializable {

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblError;

    @FXML
    private PasswordField passwordField;

    private Stage stage;
    private User userToDelete;
    private Company companyToDelete;
    private Profile currentUser;
    private boolean confirmed = false;
    private String adminPassword;
    private String operationType;
    private DBImplementation dao = new DBImplementation(); // INICIALIZADO AQUÍ

    private Profile profile;
    private Controller cont;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización
    }

    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;
        System.out.println("DeleteConfirmation - Perfil: " + profile);
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

        if (enteredPassword == null || enteredPassword.trim().isEmpty()) {
            showError("Por favor, ingrese su contraseña");
            return;
        }

        if (verifyPassword(enteredPassword)) {
            confirmed = true;
            if (stage != null) {
                stage.close();
            } else {
                // Si stage es null, cerrar desde el scene
                ((Stage) passwordField.getScene().getWindow()).close();
            }
        } else {
            showError("Contraseña incorrecta");
            passwordField.clear();
        }
    }

    private boolean verifyPassword(String enteredPassword) {
        // Si currentUser no es null, verificamos con DAO
        if (currentUser != null) {
            // Verificar que dao no sea null
            if (dao == null) {
                System.err.println("ERROR: dao es null en verifyPassword");
                return false;
            }

            Profile verifiedProfile = dao.logIn(currentUser.getUsername(), enteredPassword);

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
        if (stage != null) {
            stage.close();
        } else {
            ((Stage) passwordField.getScene().getWindow()).close();
        }
    }

    private void showError(String message) {
        if (lblError != null) {
            lblError.setText(message);
            lblError.setVisible(true);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public User getUserToDelete() {
        return userToDelete;
    }

    public Company getCompanyToDelete() {
        return companyToDelete;
    }

}
