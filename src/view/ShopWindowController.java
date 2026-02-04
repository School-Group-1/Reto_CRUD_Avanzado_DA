/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import controller.Controller;
import java.awt.Desktop;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.CartItem;
import model.Company;
import model.DBImplementation;
import model.Product;
import model.Profile;
import utilidades.MyObjectOutputStream;
import utilidades.Utilidades;

/**
 * FXML Controller class
 *
 * @author 2dami
 */
public class ShopWindowController implements Initializable {

    private ArrayList<CartItem> talis;
    @FXML
    private TableView<CartItem> CartTable;
    @FXML
    private Button btnEmptyCart;
    @FXML
    private Button btnBuy;

    private File fichC;
    private ArrayList<Product> Items;

    public ArrayList Cart;

    @FXML
    private Button btnCompanies;
    public ObservableList<CartItem> carrito;
    @FXML
    private Button btnUser;
    @FXML
    private Button btnStore;
    @FXML
    private VBox productcardList;

    @FXML
    private TableColumn<CartItem, Integer> tcAmout;
    @FXML
    private TableColumn<CartItem, String> tcItem;
    @FXML
    private TableColumn<CartItem, String> tcPrice;

    private String uname;
    /**
     * Initializes the controller class.
     */
    private Profile profile;

    private Controller cont;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void initData(Profile profile, Controller cont) throws IOException {
        //Hacer que Items muestre Productos de la base de datos en la vista
        this.profile = profile;
        this.cont = cont;
        ObjectInputStream ois =null;
        boolean filend = false;
        System.out.println("Perfil: " + profile);
        System.out.println("Controller: " + cont);
        talis = new ArrayList<CartItem>();
        //Hacer que Items muestre Productos de la base de datos en la vista 
        // System.out.println(cont);
        uname = profile.getName();
        List<Product> products = cont.findAllProducts();
        if (fichC.exists()) {
            if (fichC.getName().contains(uname)) {
                try {
                    ois = new ObjectInputStream(new FileInputStream(fichC));
                } catch (FileNotFoundException e) { //Excepcion no se ha encontrado el Fichero
                    e.printStackTrace();
                } catch (IOException e) { // Excepcion error al acceder al fichero
                    e.printStackTrace();
                }
                try {
                    while (!filend) {
                        CartItem ci= (CartItem) ois.readObject();
                        talis.add(ci);
                        
                    }
                } catch (EOFException e) {
                    filend = true;
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }

                carrito = FXCollections.observableList(talis);
                tcAmout.setCellValueFactory(new PropertyValueFactory<>("atributoClase"));
                tcItem.setCellValueFactory(new PropertyValueFactory<>("atributoClase"));
                tcPrice.setCellValueFactory(new PropertyValueFactory<>("atributoClase"));

                CartTable.setItems(carrito);
            }
        } else {
            fichC = new File("Carrito" + uname + ".dat");
        }
        for (Product prod : products) {
            Node card = createProductCard(prod);
            productcardList.getChildren().add(card);
        }
    }

    @FXML
    private void emptyList(ActionEvent event) {
    clearC();
    }
    
    private void clearC(){
         File fichAux = new File("mediaAux.dat");
        talis.clear();
        carrito = FXCollections.observableArrayList(talis);
        if (fichC.delete()) {
            fichAux.renameTo(fichC);
        }
        CartTable.setItems(carrito);
    
    }
    
    @FXML
    private void buyCart(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("¿Do you really want to buy this?");
        
        // Opcional: Personalizar botones
        // alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("Usuario confirmó");
                // Tu lógica aquí
                for (CartItem ci: talis){
                 //TODO borrar stock correspondiente
                 cont.lowerStock(ci);
                }
                clearC();
                alert.close();
            }
        });
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.CANCEL) {
                System.out.println("Usuario confirmó");
                alert.close();
            }
        });
    }

    @FXML
    private void goToCompanies(ActionEvent event
    ) {
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
        addCButton.setOnMouseClicked(e -> addToCart(product));
        rightBox.getChildren().addAll(priceLabel, addCButton);

        HBox.setHgrow(textBox, Priority.ALWAYS);

        card.getChildren().addAll(imageView, textBox, rightBox);

        return card;
    }

    private void addToCart(Product product) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        System.out.println("añadiendo a carrito");
        CartItem nuevoItem = new CartItem(product, "xl");
        talis.add(nuevoItem);
        try {
            MyObjectOutputStream moos = new MyObjectOutputStream(new FileOutputStream(fichC, true));
            moos.writeObject(nuevoItem);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
        carrito = FXCollections.observableArrayList(talis);
        CartTable.setItems(carrito);
        System.out.println("añadido a carrito\n " + carrito);
        System.out.println("añadido a carrito\n ");
        

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

    @FXML
    private void searcher(KeyEvent event) {
    }

    @FXML
    private void manual_open(ActionEvent event) {
        LOGGER.info("**ShopWindow** Opening user manual");

        try {
            File pdf = new File("pdfs/User_Manual.pdf");
            if (!pdf.exists()) {
                LOGGER.info("**ShopWindow** User manual file not found: pdfs/User_Manual.pdf");
                return;
            }

            Desktop.getDesktop().open(pdf);
            LOGGER.info("**ShopWindow** User manual opened successfully");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "**ShopWindow** error opening user manual", ex);
        }
    }

}
