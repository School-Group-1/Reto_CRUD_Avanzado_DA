/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.DBImplementation;
import model.Profile;
import model.User;

public class ModifyUserAdminController implements Initializable {

    // Campos FXML
    @FXML
    private TextField nameText;
    @FXML
    private TextField surnameText;
    @FXML
    private TextField telephoneText;
    @FXML
    private TextField passwordText;
    @FXML
    private TextField confirmText;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button storeButton;
    @FXML
    private Button companiesButton;
    @FXML
    private Button profileButton;
    private DBImplementation dao = new DBImplementation();

    // Usuario actual (deberías obtenerlo del sistema de login)
    private Profile currentUser;
    private Controller cont;

    // Para mostrar mensajes de error
    @FXML
    private Label errorLabel;

    // Datos originales para comparar cambios
    private String originalName;
    private String originalSurname;
    private String originalTelephone;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.originalName = "";
        this.originalSurname = "";
        this.originalTelephone = "";
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            loadUserData();
        }
    }

    @FXML
    private void loadUserData() {
        if (currentUser != null) {
            nameText.setText(currentUser.getName());
            surnameText.setText(currentUser.getSurname());
            telephoneText.setText(currentUser.getTelephone());

            // Limpiar campos de contraseña
            passwordText.clear();
            confirmText.clear();
        }
    }

    @FXML
    private void goToShopWindow(ActionEvent event) {
        changeWindow("/view/ShopWindow.fxml", event);
    }

    @FXML
    private void goToCompanyWindow(ActionEvent event) {
        changeWindow("/view/CompanyWindow.fxml", event);
    }

    @FXML
    private void goToProfileWindow(ActionEvent event) {
        changeWindow("/view/ProfileWindow.fxml", event);
    }
    
    @FXML
    private void cancel(ActionEvent event) {
        changeWindow("/view/ProfileWindow.fxml",event);
    }

    private void changeWindow(String fxml, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private boolean hasUnsavedChanges() {
        return !nameText.getText().equals(originalName)
                || !surnameText.getText().equals(originalSurname)
                || !telephoneText.getText().equals(originalTelephone)
                || !passwordText.getText().isEmpty()
                || !confirmText.getText().isEmpty();
    }

    @FXML
    private void handleSaveChanges() {
        clearErrors();

        boolean canProceed = true;

        boolean inputsValid = validateInputs();
        if (!inputsValid) {
            canProceed = false;
        }

        boolean hasChanges = true;
        if (canProceed) {
            hasChanges = hasRealChanges();
            if (!hasChanges) {
                canProceed = false;
                showError("You haven't made any changes to save.");
            }
        }

        boolean updateSuccessful = false;
        if (canProceed) {
            try {
                updateUser();
                updateSuccessful = true;

            } catch (Exception e) {
                updateSuccessful = false;
                showError("Error. Failed to update user.");
                e.printStackTrace();
            }
        }

        if (updateSuccessful) {
            showError("Sucess. User modified successfully!");
            openProfileWindow();
        }
    }

    @FXML
    private boolean validateInputs() {
        boolean correct = true;
        String newPassword = passwordText.getText();
        String confirmPassword = confirmText.getText();

        if (!newPassword.isEmpty() || !confirmPassword.isEmpty()) {
            if (newPassword.isEmpty() && !confirmPassword.isEmpty()) {
                showError("Please enter a new password");
                return !correct;
            }

            if (!newPassword.isEmpty() && confirmPassword.isEmpty()) {
                showError("Please confirm your new password");
                return !correct;
            }

            if (!newPassword.isEmpty() && !confirmPassword.isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    showError("Passwords do not match");
                    return !correct;
                }

                if (newPassword.equals(currentUser.getPassword())) {
                    showError("New password cannot be the same as current password");
                    return !correct;
                }

                if (newPassword.length() < 6) {
                    showError("Password must be at least 6 characters long");
                    return !correct;
                }
            }
        }

        String telephone = telephoneText.getText();
        if (!telephone.isEmpty() && !telephone.matches("\\d{9,15}")) {
            showError("Telephone must contain only numbers (9-15 digits)");
            return !correct;
        }

        String name = nameText.getText();
        String surname = surnameText.getText();

        if (name.isEmpty()) {
            showError("Name cannot be empty");
            return !correct;
        }

        if (surname.isEmpty()) {
            showError("Surname cannot be empty");
            return !correct;
        }

        return correct;
    }

    @FXML
    private boolean hasRealChanges() {
        boolean nameChanged = !nameText.getText().equals(originalName);
        boolean surnameChanged = !surnameText.getText().equals(originalSurname);
        boolean telephoneChanged = !telephoneText.getText().equals(originalTelephone);
        boolean passwordChanged = !passwordText.getText().isEmpty();

        return nameChanged || surnameChanged || telephoneChanged || passwordChanged;
    }

    @FXML
    private void updateUser() {
        currentUser.setName(nameText.getText());
        currentUser.setSurname(surnameText.getText());
        currentUser.setTelephone(telephoneText.getText());

        if (!passwordText.getText().isEmpty()) {
            currentUser.setPassword(passwordText.getText().trim());
        }

        dao.updateUser((User) currentUser);

        originalName = currentUser.getName();
        originalSurname = currentUser.getSurname();
        originalTelephone = currentUser.getTelephone();
    }

    @FXML
    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            showAlert("Validation Error", message, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clearErrors() {
        if (errorLabel != null) {
            errorLabel.setText("");
        }
    }

    @FXML
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void openProfileWindow() {
        try {
            Stage currentStage = (Stage) cancelButton.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileWindow.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Profile");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open Profile window", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void openWindow(String fxmlFile, String title) {
        try {
            Stage currentStage = (Stage) cancelButton.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open " + title + " window", Alert.AlertType.ERROR);
        }
    }
}
