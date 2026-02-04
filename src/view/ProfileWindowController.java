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
 * Controller for the main Menu window. Handles navigation to modify, delete,
 * and logout actions.
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

    @FXML
    private void goToStore(ActionEvent event) {
        LOGGER.info("**ProfileWindow** Navigating to Store");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ShopWindow.fxml"));
            Parent root = loader.load();

            view.ShopWindowController viewController = loader.getController();
            viewController.initData(profile, cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProfileWindow** Error opening Store window", e);
        }
    }

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
            stage.setTitle("Companies");
            stage.show();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProfileWindow** Error opening Companies window", e);
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        LOGGER.info("**ProfileWindow** User logging out: " + profile.getUsername());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("LogIn");
            stage.show();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProfileWindow** Error during logout", e);
        }
    }

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
            stage.show();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProfileWindow** Error opening Modify User window", e);
        }
    }

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
            modal.showAndWait();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProfileWindow** Error opening delete confirmation popup", e);
        }
    }

    @FXML
    private void showContextMenu(ContextMenuEvent event) {
        LOGGER.info("**ProfileWindow** Context menu opened");
        contextMenu.show(gridPane, event.getScreenX(), event.getScreenY());
        event.consume();
    }

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("**ProfileWindow** Initializing controller");

        contextMenu = new ContextMenu();
        reportItem = new MenuItem("Report");
        reportItem.setOnAction(e -> handleImprimirAction());
        contextMenu.getItems().add(reportItem);

        gridPane.setOnContextMenuRequested(this::showContextMenu);
    }

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

    //Level.SEVERE, "**ProfileWindow** Error opening user manual", ex ESTO ESTA BIEN?
}
