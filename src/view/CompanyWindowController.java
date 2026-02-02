/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import model.Admin;
import model.Company;
import model.Profile;
import model.User;
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
        loadCompanies();
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
    private void goToProfile(ActionEvent event) {
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
            e.printStackTrace();
        }
    }
    
    private void selectCompany(Company company) {
        System.out.println("Empresa seleccionada: " + company.getName());
        openCompanyProductsWindow(company);
    }
    
    @FXML
    private void openCompanyProductsWindow(Company company) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CompanyProducts.fxml"));
            Parent root = loader.load();
            
            CompanyProductsController controller = loader.getController();
            
            List<Product> companyProducts = cont.findProductsByCompany(company);
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
            e.printStackTrace();
        }
    }
    
    private void loadCompanies() {

        PaneButtons.getChildren().clear();

        List<Company> companies = cont.findAllCompanies();

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

        /*ImageView imageView = new ImageView(
            new Image(getClass().getResourceAsStream(
                "/images/Captura de pantalla 2026-01-26 085933.png"
            ))
        );
        imageView.setFitWidth(140);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);*/

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
        List<Company> companies = cont.findAllCompanies();

        ReportService reportService = new ReportService();
        reportService.generateCompaniesReport(companies);

        System.out.println("Reporte generado correctamente");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        contextMenu = new ContextMenu();

        reportItem = new MenuItem("Report");
        reportItem.setOnAction(e -> handleImprimirAction());

        contextMenu.getItems().add(reportItem);

        ScrollPaneCompanies.setOnContextMenuRequested(this::showContextMenu);
    }
}
