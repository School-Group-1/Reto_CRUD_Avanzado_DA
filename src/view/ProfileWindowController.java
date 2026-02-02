/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.List;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Modality;
import model.Admin;
import model.Company;
import model.DBImplementation;
import model.Product;
import model.Profile;
import model.User;
import report.ReportService;

/**
 * Controller for the main Menu window.
 * Handles navigation to modify, delete, and logout actions.
 */
public class ProfileWindowController implements Initializable {

    @FXML 
    private Label label_Username;
    @FXML 
    private GridPane gridPane;

    @FXML 
    private Button btnStore;
    @FXML 
    private Button btnCompanies;

    @FXML 
    private Button Button_Modify;
    @FXML 
    private Button Button_Delete;
    
    private ContextMenu contextMenu;
    private MenuItem reportItem;

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
    
    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;

        System.out.println("Perfil: " + profile);
        System.out.println("Controller: " + cont);
    }
    
    @FXML
    private void goToStore(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ShopWindow.fxml"));
            Parent root = loader.load();
            
            view.ShopWindowController viewController = loader.getController();
            viewController.initData(profile, cont);

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
    private void goToCompanies(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CompanyWindow.fxml"));
            Parent root = loader.load();
            
            CompanyWindowController viewController = loader.getController();

            viewController.initData(profile, cont);

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
    
    /**
     * Opens the Modify window.
     */
    
    /**
     * NO ES UN POP UP
     */
    @FXML
    private void openModifyPopup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ModifyUserAdmin.fxml"));
            Parent root = loader.load();
            
            ModifyUserAdminController viewController = loader.getController();

            viewController.initData(profile, cont);

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DeleteConfirmationView.fxml"));
            Parent root = loader.load();
            
            DeleteConfirmationViewController viewController = loader.getController();

            viewController.initData(profile, cont);

            Stage modal = new Stage();
            modal.initOwner(((Node) event.getSource()).getScene().getWindow());
            modal.initModality(Modality.WINDOW_MODAL);
            modal.setScene(new Scene(root));
            modal.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
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

    @FXML
    private void showContextMenu(ContextMenuEvent event) {
        contextMenu.show(gridPane, event.getScreenX(), event.getScreenY());
        event.consume();
    }

    private void handleImprimirAction() {
        ReportService reportService = new ReportService();
        reportService.generateUserReport(profile);

        System.out.println("Reporte generado correctamente");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        contextMenu = new ContextMenu();

        reportItem = new MenuItem("Report");
        reportItem.setOnAction(e -> handleImprimirAction());

        contextMenu.getItems().add(reportItem);

        gridPane.setOnContextMenuRequested(this::showContextMenu);
    }
}
