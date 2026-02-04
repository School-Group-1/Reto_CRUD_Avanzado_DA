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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Modality;
import model.Profile;
import report.ReportService;

/**
 * Controller class for the Profile window.
 * 
 * Handles navigation to store, companies, profile modification, deletion, logout, and opening of user manual. Also manages context menu actions and user report generation.
 */
public class ProfileWindowController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ProfileWindowController.class.getName());

    @FXML
    private Label label_Username;
    @FXML
    private GridPane gridPane;

    private ContextMenu contextMenu;
    private MenuItem reportItem;

    private Profile profile;
    private Controller cont;

    /**
     * Initializes this controller with the user profile and main controller.
     *
     * @param profile the current user profile
     * @param cont the main application controller
     */
    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;

        if (profile == null || cont == null) {
            LOGGER.warning("**ProfileWindow** initData called with null values");
            return;
        }

        label_Username.setText(profile.getUsername());
        LOGGER.info("**ProfileWindow** Initialized for user: " + profile.getUsername());
    }

    /**
     * Opens the Store window and closes the current Profile window.
     *
     * @param event the ActionEvent triggered by user
     */
    @FXML
    private void goToStore(ActionEvent event) {
        LOGGER.info("**ProfileWindow** Navigating to Store");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ShopWindow.fxml"));
            Parent root = loader.load();
            ShopWindowController viewController = loader.getController();
            viewController.initData(profile, cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("ShopWindow");
            stage.show();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProfileWindow** Error opening Store window", e);
        }
    }

    /**
     * Opens the Companies window and closes the current Profile window.
     *
     * @param event the ActionEvent triggered by user
     */
    @FXML
    private void goToCompanies(ActionEvent event) {
        LOGGER.info("**ProfileWindow** Navigating to Companies");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CompanyWindow.fxml"));
            Parent root = loader.load();
            CompanyWindowController viewController = loader.getController();
            viewController.initData(profile, cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("CompanyWindow");
            stage.show();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProfileWindow** Error opening Companies window", e);
        }
    }

    /**
     * Logs out the user and opens the login window.
     *
     * @param event the ActionEvent triggered by user
     */
    @FXML
    private void logout(ActionEvent event) {
        LOGGER.info("**ProfileWindow** User logging out: " + profile.getUsername());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("LogInWindow");
            stage.show();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProfileWindow** Error during logout", e);
        }
    }

    /**
     * Opens the Modify User window.
     *
     * @param event the ActionEvent triggered by user
     */
    @FXML
    private void openModifyUser(ActionEvent event) {
        LOGGER.info("**ProfileWindow** Opening Modify User window");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ModifyUserAdmin.fxml"));
            Parent root = loader.load();
            ModifyUserAdminController viewController = loader.getController();
            viewController.initData(profile, cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("ModifyUserAdmin");
            stage.show();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProfileWindow** Error opening Modify User window", e);
        }
    }

    /**
     * Opens a delete confirmation popup for the user account.
     *
     * @param event the ActionEvent triggered by user
     */
    @FXML
    private void openDeletePopup(ActionEvent event) {
        LOGGER.warning("**ProfileWindow** Delete account popup opened for user: " + profile.getUsername());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DeleteConfirmationView.fxml"));
            Parent root = loader.load();
            DeleteConfirmationViewController viewController = loader.getController();
            viewController.initData(profile, cont);

            Stage modal = new Stage();
            modal.initOwner(((Node) event.getSource()).getScene().getWindow());
            modal.initModality(Modality.WINDOW_MODAL);
            modal.setScene(new Scene(root));
            modal.setTitle("DeleteConfirmationView");
            modal.showAndWait();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProfileWindow** Error opening delete confirmation popup", e);
        }
    }

    /**
     * Shows the context menu at the mouse location.
     *
     * @param event the ContextMenuEvent triggered by user
     */
    @FXML
    private void showContextMenu(ContextMenuEvent event) {
        LOGGER.info("**ProfileWindow** Context menu opened");
        contextMenu.show(gridPane, event.getScreenX(), event.getScreenY());
        event.consume();
    }

    /**
     * Generates a user report using ReportService.
     */
    private void handleImprimirAction() {
        LOGGER.info("**ProfileWindow** Generating user report for: " + profile.getUsername());
        try {
            ReportService reportService = new ReportService();
            reportService.generateUserReport(profile);
            LOGGER.info("**ProfileWindow** User report generated successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "**ProfileWindow** Error generating user report", e);
        }
    }

    /**
     * Initializes the controller after the FXML has been loaded.
     *
     * @param url the FXML location or null
     * @param rb the ResourceBundle used for localization or null
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("**ProfileWindow** Initializing controller");

        contextMenu = new ContextMenu();
        reportItem = new MenuItem("Report");
        reportItem.setOnAction(e -> handleImprimirAction());
        contextMenu.getItems().add(reportItem);

        gridPane.setOnContextMenuRequested(this::showContextMenu);
    }

    /**
     * Opens the user manual PDF file.
     *
     * @param event the ActionEvent triggered by user
     */
    @FXML
    private void openUserManual(ActionEvent event) {
        LOGGER.info("**ProfileWindow** Opening user manual");
        try {
            File pdf = new File("pdfs/User_Manual.pdf");
            if (!pdf.exists()) {
                LOGGER.warning("**ProfileWindow** User manual not found: pdfs/User_Manual.pdf");
                return;
            }
            Desktop.getDesktop().open(pdf);
            LOGGER.info("**ProfileWindow** User manual opened successfully");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "**ProfileWindow** Error opening user manual", ex);
        }
    }
}
