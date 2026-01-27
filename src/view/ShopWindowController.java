/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import static java.lang.String.valueOf;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Company;
import model.Product;

/**
 * FXML Controller class
 *
 * @author 2dami
 */
public class ShopWindowController implements Initializable {

    @FXML
    private TableView<String> CartTable;
    @FXML
    private Button btnEmptyCart;
    @FXML
    private Button btnBuy;
    @FXML
    private Button btnaddToCart;

    
    private ArrayList<Product> Items;
    public ArrayList Cart;
    @FXML
    private Label name;
    @FXML
    private Label desc;
    @FXML
    private Label price;
    @FXML
    private GridPane Item1;
    @FXML
    private Button btnCompanies;
    @FXML
    private Button btnUser;
    @FXML
    private Button btnStore;
    /**
     * Initializes the controller class.
     */
    
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //Hacer que Items muestre Productos de la base de datos en la vista 
        Product BaldkShirt= new Product("Bald Shirt", 15.40 , "Blue T-shirt", "A Great shirt for the summer, this is just not to write lorem ipsum", "i dont have an image yet", new Company("BalKe", "123456789abc", "somewhere","http://guthib.com/"));
        price.setText(valueOf(BaldkShirt.getPrice()));
        name.setText(BaldkShirt.getProductType());
        desc.setText(BaldkShirt.getDescription());
        
    }    

    @FXML
    private void emptyList(ActionEvent event) {
    }

    @FXML
    private void buyCart(ActionEvent event) {
    }

    @FXML
    private void addItem(ActionEvent event) {
        
        
    }

    @FXML
    private void GoToComp(ActionEvent event) {
        try {
            FXMLLoader loader;
            Parent root;
            Stage stage = new Stage();

            
                // 🔹 Si es usuario normal, cargar ModifyWindow.fxml
                loader = new FXMLLoader(getClass().getResource("CompanyProducts.fxml"));
                root = loader.load();

                CompanyProductsController companyController = loader.getController();
             
               

                stage.setTitle("Modificar perfil");
            

            stage.setScene(new Scene(root));

            stage.show();

            // Cerrar la ventana de login
            Stage currentStage = (Stage) btnStore.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            System.err.println(e);
        }
    }

    @FXML
    private void GoToProf(ActionEvent event) {
        
        try {
            FXMLLoader loader;
            Parent root;
            Stage stage = new Stage();

            
                // 🔹 Si es usuario normal, cargar ModifyWindow.fxml
                loader = new FXMLLoader(getClass().getResource("CompanyProducts.fxml"));
                root = loader.load();

                CompanyProductsController companyController = loader.getController();
             
               

                stage.setTitle("Modificar perfil");
            

            stage.setScene(new Scene(root));

            stage.show();

            // Cerrar la ventana de login
            Stage currentStage = (Stage) btnStore.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            System.err.println(e);
        }
        
    }
    
}
