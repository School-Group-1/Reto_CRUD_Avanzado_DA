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
 * Controller for the main Menu window.
 * Handles navigation to modify, delete, and logout actions.
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
    
    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;
        System.out.println("Perfil: " + profile);
        System.out.println("Controller: " + cont);
        LOGGER.info("**CompanyWindow** Initialized for user: " + profile.getUsername());
        loadCompanies();
    }
    
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
            stage.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**CompanyWindow** Error opening Store window", e);
        }
    }

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
            stage.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**CompanyWindow** Error opening Profile window", e);
        }
    }
    
    private void selectCompany(Company company) {
        System.out.println("Empresa seleccionada: " + company.getName());
        LOGGER.info("**CompanyWindow** Company selected: " + company.getName());
        openCompanyProductsWindow(company);
    }
    
    @FXML
    private void openCompanyProductsWindow(Company company) {
        LOGGER.info("**CompanyWindow** Opening products for company: " + company.getName());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CompanyProducts.fxml"));
            Parent root = loader.load();
            
            CompanyProductsController controller = loader.getController();
            
            List<Product> companyProducts = cont.findProductsByCompany(company);
            
            LOGGER.info("**CompanyWindow** Products loaded: " + companyProducts.size());
            
            controller.initData(
                company, 
                companyProducts, 
                profile, 
                cont
            );
            
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
    
    private void loadCompanies() {

        PaneButtons.getChildren().clear();

        List<Company> companies = cont.findAllCompanies();
        
        LOGGER.info("**CompanyWindow** Loading companies: " + companies.size());

        for (Company c : companies) {
            Button companyButton = createCompanyButton(c);
            PaneButtons.getChildren().add(companyButton);
            System.out.println(c.getName());
        }
    }

    
    private Button createCompanyButton(Company company) {

        Button button = new Button();
        button.setId("companyBtn_" + company.getNie());
        button.setPrefSize(200, 200);
        button.setStyle(
            "-fx-background-color: white;"
          + "-fx-background-radius: 20;"
          + "-fx-border-radius: 20;"
          + "-fx-border-color: #0f954a;"
        );

        VBox content = new VBox(10);
        content.setAlignment(Pos.TOP_CENTER);

        Label nameLabel = new Label(company.getName());
        nameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        content.getChildren().addAll(nameLabel );
        button.setGraphic(content);

        button.setOnAction(e -> selectCompany(company));

        return button;
    }
    
    @FXML
    private void showContextMenu(ContextMenuEvent event) {
        contextMenu.show(ScrollPaneCompanies, event.getScreenX(), event.getScreenY());
        event.consume();
    }

    private void handleImprimirAction() {
        LOGGER.info("**CompanyWindow** Generating companies report");
        try {
            List<Company> companies = cont.findAllCompanies();
            new ReportService().generateCompaniesReport(companies);
            LOGGER.info("**CompanyWindow** Companies report generated successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "**CompanyWindow** Error generating companies report", e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        contextMenu = new ContextMenu();

        reportItem = new MenuItem("Report");
        reportItem.setOnAction(e -> handleImprimirAction());

        contextMenu.getItems().add(reportItem);

        ScrollPaneCompanies.setOnContextMenuRequested(this::showContextMenu);
    }
    
    @FXML
    private void openUserManual(ActionEvent event) {
        LOGGER.info("**CompanyWindow** Opening user manual");
        
        try {
            File pdf = new File("pdfs/User_Manual.pdf");
            if (!pdf.exists()) {
                LOGGER.warning("**CompanyWindow** User manual file not found: pdfs/User_Manual.pdf");
                return;
            }

            Desktop.getDesktop().open(pdf);
            LOGGER.info("**CompanyWindow** User manual opened successfully");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "**CompanyWindow** error opening user manual", ex);
        }
    }
}
