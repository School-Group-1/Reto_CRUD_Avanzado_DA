/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import static java.lang.String.valueOf;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Company;
import model.DBImplementation;
import model.Product;
import model.Profile;

/**
 * FXML Controller class
 *
 * @author 2dami
 */
public class ShopWindowController implements Initializable {

    @FXML
    private TableView<Product> CartTable;
    @FXML
    private Button btnEmptyCart;
    @FXML
    private Button btnBuy;
    @FXML
    private Button btnaddToCart;
    
    private File fichE;
    private ArrayList<Product> Items;
    
    public ArrayList Cart;
    
    @FXML
    private Button btnCompanies;
    public ObservableList<Product> carrito;
    @FXML
    private Button btnUser;
    @FXML
    private Button btnStore;
    @FXML
    private VBox productcardList;
  
    @FXML
    private TableColumn<?, ?> tcAmout;
    @FXML
    private TableColumn<?, ?> tcItem;
    @FXML
    private TableColumn<?, ?> tcPrice;

    private String uname;
    /**
     * Initializes the controller class.
     */
    private Profile profile;
    
    private Controller cont;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         
    }
    
    public void initData(Profile profile, Controller cont) {
        //Hacer que Items muestre Productos de la base de datos en la vista
        this.profile = profile;
        this.cont = cont;

        System.out.println("Perfil: " + profile);
        System.out.println("Controller: " + cont);

        //Hacer que Items muestre Productos de la base de datos en la vista 
        // System.out.println(cont);
        uname = "Example User Name";
        List<Product> products = cont.findAllProducts();
        /*if(fichE.exists()){
            if (fichE.getName().contains(uname)){
            
            carrito=FXCollections.observableList(products);
            tcAmout.setCellValueFactory(new PropertyValueFactory<>("atributoClase"));
            tcItem.setCellValueFactory(new PropertyValueFactory<>("atributoClase"));
            tcPrice.setCellValueFactory(new PropertyValueFactory<>("atributoClase"));
            
            CartTable.setItems(carrito);
            }
        }else{
            fichE=new File("Carrito"+uname+".dat");
        }*/
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
    private void goToCompanies(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CompanyWindow.fxml"));
            Parent root = loader.load();
            
            view.CompanyWindowController viewController = loader.getController();
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

    private Node createProductCard(Product product) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
                "-fx-background-color: white;"
                + "-fx-border-radius: 12;"
                + "-fx-background-radius: 12;"
        );

        // Image
        ImageView imageView = new ImageView(
                loadImage(product.getImage())
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

        Label priceLabel = new Label(product.getPrice() + "€");
        priceLabel.setStyle(
                "-fx-background-color: #e5e5e5;"
                + "-fx-padding: 4 8 4 8;"
                + "-fx-font-weight: bold;"
        );

        Button addCButton = new Button("Add to Cart");
        addCButton.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-border-color: green;"
                + "-fx-text-fill: green;"
                + "-fx-border-radius: 6;"
        );
        addCButton.setOnMouseClicked(e->addToCart(product));
        rightBox.getChildren().addAll(priceLabel, addCButton);
        
        HBox.setHgrow(textBox, Priority.ALWAYS);

        card.getChildren().addAll(imageView, textBox, rightBox);

        return card;
    }

    private void addToCart( Product product) {
        try {
            ObjectInputStream ois=new ObjectInputStream(new FileInputStream(fichE));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Image loadImage(String path) {

        // 1) Classpath resource (starts with /)
        if (path.startsWith("/")) {
            InputStream is = Product.class.getResourceAsStream(path);
            if (is == null) {
                throw new IllegalArgumentException("Classpath image not found: " + path);
            }
            return new Image(is);
        }

        // 2) File system path (relative or absolute)
        Path filePath = Paths.get(path);

        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("File image not found: " + path);
        }

        return new Image(filePath.toUri().toString());
    }
}
