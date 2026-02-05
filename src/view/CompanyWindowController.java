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
import javafx.scene.control.Button;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Company;
import model.Profile;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ContextMenuEvent;
import model.Product;
import report.ReportService;


/**
 * Controller class for the Company Window.
 * This controller manages the visualization of companies and allows navigation to other application windows such as Store, Profile and Company Products. It also provides access to reports and the user manual.
 *
 * The controller communicates with the main application controller to retrieve companies and products from the database.
 */
public class CompanyWindowController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(CompanyWindowController.class.getName());

    @FXML
    private TilePane PaneButtons;

    @FXML
    private ScrollPane ScrollPaneCompanies;

    private ContextMenu contextMenu;
    private MenuItem reportItem;

    private Profile profile;
    private Controller cont;

    /**
     * Initializes the window with the current user profile and controller.
     * Loads all companies available in the system.
     *
     * @param profile the profile of the logged-in user
     * @param cont the main application controller
     */
    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;
        LOGGER.info("**CompanyWindow** Initialized for user: " + profile.getUsername());
        loadCompanies();
    }

    /**
     * Opens the Store window and closes the current Company window.
     *
     * @param event the action event triggered by the Store button
     */
    @FXML
    private void goToStore(ActionEvent event) {
        LOGGER.info("**CompanyWindow** Navigating to Store");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ShopWindow.fxml"));
            Parent root = loader.load();

            view.ShopWindowController viewController = loader.getController();
            viewController.initData(profile, cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("ShopWindow");
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**CompanyWindow** Error opening Store window", e);
        }
    }

    /**
     * Opens the Profile window and closes the current Company window.
     *
     * @param event the action event triggered by the Profile button
     */
    @FXML
    private void goToProfile(ActionEvent event) {
        LOGGER.info("**CompanyWindow** Navigating to Profile");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileWindow.fxml"));
            Parent root = loader.load();

            view.ProfileWindowController viewController = loader.getController();
            viewController.initData(profile, cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("ProfileWindow");
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**CompanyWindow** Error opening Profile window", e);
        }
    }

    /**
     * Opens the products window for the selected company.
     *
     * @param company the company whose products will be displayed
     */
    @FXML
    private void openCompanyProductsWindow(Company company) {
        LOGGER.info("**CompanyWindow** Opening products for company: " + company.getName());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CompanyProducts.fxml"));
            Parent root = loader.load();

            CompanyProductsController controller = loader.getController();
            List<Product> companyProducts = cont.findProductsByCompany(company);

            controller.initData(company, companyProducts, profile, cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(company.getName() + " - Products");
            stage.show();

            Stage currentStage = (Stage) PaneButtons.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**CompanyWindow** Error opening CompanyProducts window", e);
        }
    }

    /**
     * Loads all companies from the database and displays them as buttons.
     */
    private void loadCompanies() {
        PaneButtons.getChildren().clear();
        List<Company> companies = cont.findAllCompanies();
        LOGGER.info("**CompanyWindow** Loading companies: " + companies.size());

        for (Company c : companies) {
            PaneButtons.getChildren().add(createCompanyButton(c));
        }
    }

    /**
     * Creates a button associated with a company.
     *
     * @param company the company represented by the button
     * @return a configured company button
     */
    private Button createCompanyButton(Company company) {
        Button button = new Button();
        button.setId("companyBtn_" + company.getNie());
        button.setPrefSize(200, 200);
        button.setStyle(
            "-fx-background-color: white;"
          + "-fx-background-radius: 20;"
          + "-fx-border-radius: 20;"
          + "-fx-border-color: #0f954a;"
          + "-fx-cursor: hand;"
        );

        VBox content = new VBox(10);
        content.setAlignment(Pos.TOP_CENTER);

        Label nameLabel = new Label(company.getName());
        nameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        content.getChildren().add(nameLabel);
        button.setGraphic(content);

        button.setOnAction(e -> openCompanyProductsWindow(company));

        return button;
    }

    /**
     * Shows the context menu on right click.
     *
     * @param event the context menu event
     */
    @FXML
    private void showContextMenu(ContextMenuEvent event) {
        contextMenu.show(ScrollPaneCompanies, event.getScreenX(), event.getScreenY());
        event.consume();
    }

    /**
     * Generates a report with all companies in the system.
     */
    private void handleImprimirAction() {
        LOGGER.info("**CompanyWindow** Generating companies report");
        try {
            List<Company> companies = cont.findAllCompanies();
            new ReportService().generateCompaniesReport(companies);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "**CompanyWindow** Error generating companies report", e);
        }
    }

    /**
     * Initializes UI components after the FXML file is loaded.
     *
     * @param url the location used to resolve relative paths
     * @param rb the resource bundle used for localization
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        contextMenu = new ContextMenu();

        reportItem = new MenuItem("Report");
        reportItem.setOnAction(e -> handleImprimirAction());

        contextMenu.getItems().add(reportItem);
        ScrollPaneCompanies.setOnContextMenuRequested(this::showContextMenu);
    }

    /**
     * Opens the user manual PDF file.
     *
     * @param event the action event triggered from the Help menu
     */
    @FXML
    private void openUserManual(ActionEvent event) {
        LOGGER.info("**CompanyWindow** Opening user manual");
        try {
            File pdf = new File("pdfs/User_Manual.pdf");
            if (!pdf.exists()) {
                LOGGER.warning("**CompanyWindow** User manual file not found");
                return;
            }
            Desktop.getDesktop().open(pdf);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "**CompanyWindow** Error opening user manual", ex);
        }
    }
}
