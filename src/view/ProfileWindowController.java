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
import java.util.ResourceBundle;
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
import model.Profile;
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
    
    private ContextMenu contextMenu;
    private MenuItem reportItem;

    private Profile profile;
    private Controller cont;
    
    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;

        System.out.println("Perfil: " + profile);
        System.out.println("Controller: " + cont);
        
        label_Username.setText(profile.getUsername());
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
    
    /**
     * Opens the Modify window.
     */
    @FXML
    private void openModifyUser(ActionEvent event) {
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
