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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
    @FXML
    private VBox productcardList;

    /**
     * Initializes the controller class.
     */
    
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //Hacer que Items muestre Productos de la base de datos en la vista 
       
        Product BaldkShirt1 = new Product("Bald Shirt", 15.40, "Blue T-shirt", "A Great shirt for the summer, this is just not to write lorem ipsum", "../images/baldinkent.png", new Company("BalKe", "123456789abc", "somewhere", "http://guthib.com/"));

        Product BaldkShirt2 = new Product("Bald Shirt", 15.40, "Blue T-shirt", "A Great shirt for the summer, this is just not to write lorem ipsum", "../images/baldinkent.png", new Company("BalKe", "123456789abc", "somewhere", "http://guthib.com/"));
       
        Items.add(BaldkShirt2)
        Items.add(BaldkShirt2);
        List<Product> products = Items;
        for (Product prod : products) {
            Node card = createProductCard(prod);
            productcardList.getChildren().add(card);
        }
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
            loader = new FXMLLoader(getClass().getResource("CompanyWindow.fxml"));
            root = loader.load();

            CompanyWindowController companyWController = loader.getController();

               
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
            loader = new FXMLLoader(getClass().getResource("ProfileWindow.fxml"));
            root = loader.load();

            ProfileWindowController ProfileWController = loader.getController();

               

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
    private Node createProductCard(Product product) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
                "-fx-background-color: white;"
                + "-fx-border-color: green;"
                + "-fx-border-radius: 12;"
                + "-fx-background-radius: 12;"
        );

        // Image
        ImageView imageView = new ImageView(
                new Image(getClass().getResourceAsStream(product.getImage()))
        );
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);

        // Text container
        VBox textBox = new VBox(5);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Label descLabel = new Label(product.getDescription());
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(280);

        textBox.getChildren().addAll(nameLabel, descLabel);

        // Price + button
        VBox rightBox = new VBox(8);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        Label priceLabel = new Label(product.getPrice()+"€");
        priceLabel.setStyle(
                "-fx-background-color: #e5e5e5;"
                + "-fx-padding: 4 8 4 8;"
                + "-fx-font-weight: bold;"
        );

        Button editButton = new Button("Edit Price");
        editButton.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-border-color: green;"
                + "-fx-text-fill: green;"
                + "-fx-border-radius: 6;"
        );

        rightBox.getChildren().addAll(priceLabel, editButton);

        HBox.setHgrow(textBox, Priority.ALWAYS);

        card.getChildren().addAll(imageView, textBox, rightBox);
      
        return card;
    }
}
