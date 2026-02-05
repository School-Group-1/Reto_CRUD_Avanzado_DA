/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import java.awt.Desktop;
import java.io.File;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Profile;
import model.User;
import report.ReportService;

/**
 * Controller for modifying the logged-in user's profile information.
 * Allows users to update their name, surname, telephone, and password.
 * 
 * @author acer
 */
public class ModifyUserAdminController implements Initializable {

    @FXML
    private GridPane gridpane;
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
    @FXML
    private Label errorLabel;
    @FXML
    private Menu helpMenu;
    @FXML
    private MenuItem viewManualItem;

    private Profile profile;
    private Controller cont;
    private String originalName;
    private String originalSurname;
    private String originalTelephone;
    private ContextMenu contextMenu;
    private MenuItem reportItem;
    private static final Logger LOGGER = Logger.getLogger(ModifyUserAdminController.class.getName());

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("**ModifyUser** Initializing Modify User Window Controller");
        
        this.originalName = "";
        this.originalSurname = "";
        this.originalTelephone = "";
        
        contextMenu = new ContextMenu();
        reportItem = new MenuItem("Report");
        reportItem.setOnAction(e -> handleImprimirAction());
        contextMenu.getItems().add(reportItem);
        gridpane.setOnContextMenuRequested(this::showContextMenu);
    }

    /**
     * Initializes the controller with user data.
     */
    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;

        LOGGER.log(Level.INFO, "**ModifyUser** Profile received: {0}", profile);
        LOGGER.log(Level.INFO, "**ModifyUser** Controller received: {0}", cont);

        loadUserData();
    }

    @FXML
    private void loadUserData() {
        LOGGER.fine("**ModifyUser** Loading user data into form");
        
        if (profile != null) {
            nameText.setText(profile.getName());
            surnameText.setText(profile.getSurname());
            telephoneText.setText(profile.getTelephone());

            this.originalName = profile.getName();
            this.originalSurname = profile.getSurname();
            this.originalTelephone = profile.getTelephone();

            passwordText.clear();
            confirmText.clear();
        }
    }

    /**
     * Navigates to the shop window.
     */
    @FXML
    private void goToShopWindow(ActionEvent event) {
        LOGGER.info("**ModifyUser** Switching to Shop window");
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ShopWindow.fxml"));
            Parent root = loader.load();
            
            Object controller = loader.getController();
            try {
                java.lang.reflect.Method initMethod = controller.getClass().getMethod("initData", Profile.class, Controller.class);
                initMethod.invoke(controller, profile, cont);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "**ModifyUser** ShopWindowController doesn't have initData method", e);
            }
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ModifyUser** Error switching to Shop window: ", e);
        }
    }

    /**
     * Navigates to the companies window.
     */
    @FXML
    private void goToCompanyWindow(ActionEvent event) {
        LOGGER.info("**ModifyUser** Switching to Company window");
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CompanyWindow.fxml"));
            Parent root = loader.load();
            
            Object controller = loader.getController();
            try {
                java.lang.reflect.Method initMethod = controller.getClass().getMethod("initData", Profile.class, Controller.class);
                initMethod.invoke(controller, profile, cont);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "**ModifyUser** CompanyWindowController doesn't have initData method", e);
            }
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ModifyUser** Error switching to Company window: ", e);
        }
    }

    /**
     * Navigates to the profile window.
     */
    @FXML
    private void goToProfileWindow(ActionEvent event) {
        LOGGER.info("**ModifyUser** Switching to Profile window");
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileWindow.fxml"));
            Parent root = loader.load();
            
            view.ProfileWindowController controller = loader.getController();
            if (controller != null) {
                controller.initData(profile, cont);
            }
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ModifyUser** Error switching to Profile window: ", e);
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        LOGGER.info("**ModifyUser** Cancel button clicked, opening Profile window");
        openProfileWindow();
    }

    /**
     * Saves the user profile changes.
     */
    @FXML
    private void handleSaveChanges() {
        LOGGER.info("**ModifyUser** Save changes button clicked");
        clearErrors();

        boolean canProceed = true;

        boolean inputsValid = validateInputs();
        if (!inputsValid) {
            canProceed = false;
            LOGGER.warning("**ModifyUser** Input validation failed");
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
                LOGGER.info("**ModifyUser** User updated successfully");

            } catch (Exception e) {
                updateSuccessful = false;
                showError("Error. Failed to update user.");
                LOGGER.log(Level.SEVERE, "**ModifyUser** Error updating user: ", e);
            }
        }

        if (updateSuccessful) {
            showError("Success. User modified successfully!");
            openProfileWindow();
        }
    }

    @FXML
    private boolean validateInputs() {
        LOGGER.fine("**ModifyUser** Validating inputs");
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

                if (newPassword.equals(profile.getPassword())) {
                    showError("New password cannot be the same as current password");
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
        
        boolean hasRealChanges = nameChanged || surnameChanged || telephoneChanged || passwordChanged;
        
        LOGGER.log(Level.FINE, "**ModifyUser** Has real changes: {0}", hasRealChanges);
        
        return hasRealChanges;
    }

    @FXML
    private void updateUser() {
        LOGGER.info("**ModifyUser** Updating user in database");
        
        profile.setName(nameText.getText());
        profile.setSurname(surnameText.getText());
        profile.setTelephone(telephoneText.getText());

        if (!passwordText.getText().isEmpty()) {
            profile.setPassword(passwordText.getText().trim());
        }

        cont.updateUser((User) profile);

        originalName = profile.getName();
        originalSurname = profile.getSurname();
        originalTelephone = profile.getTelephone();
    }

    @FXML
    private void showError(String message) {
        LOGGER.log(Level.INFO, "**ModifyUser** Showing error message: {0}", message);
        
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            showAlert("Validation Error", message, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clearErrors() {
        LOGGER.fine("**ModifyUser** Clearing error messages");
        if (errorLabel != null) {
            errorLabel.setText("");
        }
    }

    @FXML
    private void showAlert(String title, String message, Alert.AlertType type) {
        LOGGER.log(Level.INFO, "**ModifyUser** Showing alert: {0} - {1}", new Object[]{title, message});
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void openProfileWindow() {
        LOGGER.info("**ModifyUser** Opening Profile window");
        
        try {
            Stage currentStage = (Stage) cancelButton.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileWindow.fxml"));
            Parent root = loader.load();
            
            ProfileWindowController controller = loader.getController();
            if (controller != null) {
                controller.initData(profile, cont);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Profile");
            stage.show();
            
            LOGGER.info("**ModifyUser** Successfully opened Profile window");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ModifyUser** Error opening Profile window: ", e);
            showAlert("Error", "Could not open Profile window", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void showContextMenu(ContextMenuEvent event) {
        LOGGER.fine("**ModifyUser** Showing context menu");
        contextMenu.show(
                gridpane.getScene().getWindow(),
                event.getScreenX(),
                event.getScreenY()
        );
        event.consume();
    }

    private void handleImprimirAction() {
        LOGGER.info("**ModifyUser** Generating user report");
        ReportService reportService = new ReportService();
        reportService.generateUserReport(this.profile);
        LOGGER.info("**ModifyUser** Report generated successfully");
    }
    
    /**
     * Opens the user manual PDF file.
     */
    @FXML
    private void openUserManual(ActionEvent event) {
        LOGGER.info("**ModifyUser** Opening user manual");

        try {
            File pdf = new File("pdfs/User_Manual.pdf");
            if (!pdf.exists()) {
                LOGGER.warning("**ModifyUser** User manual not found at: pdfs/User_Manual.pdf");
                return;
            }

            Desktop.getDesktop().open(pdf);
            LOGGER.info("**ModifyUser** User manual opened successfully");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "**ModifyUser** Error opening user manual: ", ex);
        }
    }
}