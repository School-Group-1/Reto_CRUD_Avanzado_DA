/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import static com.sun.org.apache.bcel.internal.Repository.instanceOf;
import controller.Controller;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.stage.Stage;
import javafx.stage.Modality;
import model.Admin;
import model.DBImplementation;
import model.Profile;
import model.User;

/**
 * Controller for the main Menu window.
 * Handles navigation to modify, delete, and logout actions.
 */
public class ProfileWindowController implements Initializable {

    @FXML 
    private Label label_Username;

    @FXML 
    private Button btnStore;
    @FXML 
    private Button btnCompanies;

    @FXML 
    private Button Button_Modify;
    @FXML 
    private Button Button_Delete;

    private Profile profile;
    private Controller cont;

    public void setUsuario(Profile profile) {
        this.profile = profile;
        label_Username.setText(profile.getUsername());
    }

    public void setCont(Controller cont) {
        this.cont = cont;
    }

    public Controller getCont() {
        return cont;
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
    private void goToStore(ActionEvent event) {
        changeWindow("/view/ShopWindow.fxml", event);
    }

    @FXML
    private void goToCompanies(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CompanyWindow.fxml"));
            Parent root = loader.load();
            
            CompanyWindowController viewController = loader.getController();
            viewController.setCont(cont);

            viewController.initData();

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
    private void logout (ActionEvent event){
        changeWindow("/view/LogInWindow.fxml", event);
    }

    private void openModal(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(Button_Modify.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openDeletePopup(ActionEvent event) {
        openModal("/view/DeleteConfirmationView.fxml");
    }

    /**
     * Opens the Modify window.
     */
    @FXML
    private void openModifyPopup(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ModifyUserAdmin.fxml"));
            javafx.scene.Parent root = fxmlLoader.load();

            /*view.ModifyWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setProfile(profile);
            controllerWindow.setCont(this.cont);*/

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();
        } catch (IOException ex) {
            Logger.getLogger(ProfileWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Opens the Delete Account window depending on profile type.
     * Users open DeleteAccount; Admins open DeleteAccountAdmin.
     */
    @FXML
    private void delete() {
        try {
            FXMLLoader fxmlLoader;
            if (profile instanceof User) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/view/DeleteAccount.fxml"));
                javafx.scene.Parent root = fxmlLoader.load();
                view.DeleteAccountControllerEjemplo controllerWindow = fxmlLoader.getController();
                controllerWindow.setProfile(profile);
                controllerWindow.setCont(cont);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                Stage currentStage = (Stage) Button_Delete.getScene().getWindow();
                currentStage.close();

            } else if (profile instanceof Admin) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/view/DeleteAccountAdmin.fxml"));
                javafx.scene.Parent root = fxmlLoader.load();
                view.DeleteAccountAdminControllerEjemplo controllerWindow = fxmlLoader.getController();
                controllerWindow.setProfile(profile);
                controllerWindow.setCont(cont);
                controllerWindow.setComboBoxUser();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                Stage currentStage = (Stage) Button_Delete.getScene().getWindow();
                currentStage.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ProfileWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Closes the current window (used for logout).
     */
    @FXML
    private void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
            Stage stage = (Stage) label_Username.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization logic if needed
        cont=new Controller(DBImplementation dao = new DBImplementation());
    }
}
