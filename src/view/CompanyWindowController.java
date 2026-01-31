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
import model.Product;
import model.Profile;
import model.User;

/**
 * Controller for the main Menu window.
 * Handles navigation to modify, delete, and logout actions.
 */
public class CompanyWindowController implements Initializable {
    
    @FXML
    private TilePane PaneButtons;

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
    private Company selectedCompany;

    public void setUsuario(Profile profile) {
        this.profile = profile;
        //label_Username.setText(profile.getUsername());
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
        loadCompanies();
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
    
    @FXML
    private void openModifyPopup(ActionEvent event) {
        changeWindow("/view/ModifyUserAdmin.fxml", event);
    }
    
    @FXML
    private void openCompanyProducts(ActionEvent event) {
        changeWindow("/view/CompanyProducts.fxml", event);
    }

    private void openModal(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void selectCompany(Company company) {
        System.out.println("Empresa seleccionada: " + company.getName());
        this.selectedCompany = company;
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
                this.profile, 
                this.cont
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

    @FXML
    private void openDeletePopup(ActionEvent event) {
        openModal("/view/DeleteCOnfirmationView.fxml", event);
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
    
    /**
     * Opens the Modify window.
     */
    @FXML
    private void modifyVentana(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ModifyWindow.fxml"));
            javafx.scene.Parent root = fxmlLoader.load();

            view.ModifyWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setProfile(profile);
            controllerWindow.setCont(this.cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) Button_Modify.getScene().getWindow();
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization logic if needed
        System.out.println("Controller REAL creado: " + this);
        System.out.println("PaneButtons = " + PaneButtons);
    }
}
