/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package view;


import controller.Controller;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import javafx.collections.ObservableList;
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
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.CartItem;
import model.Company;
import model.Product;
import model.Profile;
import model.Size;
import report.ReportService;
import utilidades.MyObjectOutputStream;

/**
 * Controller for the main shop window (ShopWindow).
 * 
 * This class manages the user interface of the shop, including product display,
 * shopping cart management, and interactions with user profile and companies.
 * Implements the JavaFX Initializable interface for component initialization.
 * 
 * @author 2dami
 * @version 1.0
 * @see Initializable
 * @see Controller
 * @see Profile
 * @see Product
 * @see CartItem
 * @see Company
 */




public class ShopWindowController implements Initializable {

    
    
    /**
     * List of items in the shopping cart.
     */
    private ArrayList<CartItem> talis;
    
    /**
     * Table to display cart items.
     */
    @FXML
    private TableView<CartItem> CartTable;
    
    /**
     * Button to empty the cart.
     */
    @FXML
    private Button btnEmptyCart;
    
    /**
     * Button to purchase cart items.
     */
    @FXML
    private Button btnBuy;

    /**
     * File storing the user's cart.
     */
    private File fichC;
    
    /**
     * List of available products.
     */
    private ArrayList<Product> Items;

    /**
     * Shopping cart.
     */
    public ArrayList Cart;
    
    /**
     * Context menu for the search field.
     */
    private ContextMenu contextMenu;
    
    /**
     * Button to access the companies window.
     */
    @FXML
    private Button btnCompanies;
    
    /**
     * Observable list for the cart.
     */
    private ObservableList<CartItem> carrito;
    
    /**
     * Button to access user profile.
     */
    @FXML
    private Button btnUser;
    
    /**
     * Button to access the store.
     */
    @FXML
    private Button btnStore;
    
    /**
     * Vertical container for product cards.
     */
    @FXML
    private VBox productcardList;

    /**
     * Table column for item quantity.
     */
    @FXML
    private TableColumn<CartItem, Integer> tcAmout;
    
    /**
     * Table column for product name.
     */
    @FXML
    private TableColumn<CartItem, String> tcItem;
    
    /**
     * Table column for price.
     */
    @FXML
    private TableColumn<CartItem, String> tcPrice;

    /**
     * Username.
     */
    private String uname;
    
    /**
     * Current user's profile.
     */
    private Profile profile;
    
    /**
     * Logger for recording events and errors.
     */
    private static final Logger LOGGER = Logger.getLogger(ShopWindowController.class.getName());
    
    /**
     * Main application controller.
     */
    private Controller cont;
    
    /**
     * Text field for search.
     */
    @FXML
    private TextField sear;
    
    /**
     * Menu item for generating reports.
     */
    private MenuItem reportItem;

    /**
     * Initializes the controller class.
     * 
     * @param url Relative location of the FXML file
     * @param rb Resources to locate objects
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    /**
     * Initializes controller data.
     * 
     * This method configures user data, loads products from the database,
     * retrieves the user's saved cart, and sets up the graphical interface.
     * 
     * @param profile Current user's profile
     * @param cont Main application controller
     * @throws IOException If an error occurs reading the cart file
     */
    public void initData(Profile profile, Controller cont) throws IOException {
        //Hacer que Items muestre Productos de la base de datos en la vista
        
        LOGGER.info("**ShopWindow** Initializing controller");
        
        this.profile = profile;
        this.cont = cont;
        ObjectInputStream ois = null;
        boolean filend = false;
        System.out.println("Perfil: " + profile);
        System.out.println("Controller: " + cont);
        talis = new ArrayList<CartItem>();
        //Hacer que Items muestre Productos de la base de datos en la vista 
        // System.out.println(cont);
        uname = profile.getName();
        fichC = new File("Carrito" + uname + ".dat");

        System.out.println(uname);
        List<Product> products = cont.findAllProducts();
        if (fichC.exists()) {
            if (fichC.getName().contains(uname)) {
                try {

                    ois = new ObjectInputStream(new FileInputStream(fichC));
                    while (!filend) {
                        CartItem ci = (CartItem) ois.readObject();
                        talis.add(ci);
                    }

                    ois.close();
                } catch (FileNotFoundException e) { //Excepcion no se ha encontrado el Fichero
                    e.printStackTrace();
                } catch (IOException e) { // Excepcion error al acceder al fichero
                    e.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        tcAmout.setCellValueFactory(new PropertyValueFactory<>("amount"));
        tcItem.setCellValueFactory(new PropertyValueFactory<>("productName"));
        tcPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        carrito = FXCollections.observableList(talis);
        CartTable.setItems(carrito);

        for (Product prod : products) {
            Node card = createProductCard(prod);
            productcardList.getChildren().add(card);
        }
        contextMenu = new ContextMenu();
        reportItem = new MenuItem("Generar Reporte Carrito");
        reportItem.setOnAction(e -> handleGenerarReporteAction());
        contextMenu.getItems().add(reportItem);
        sear.setOnContextMenuRequested(this::showContextMenu);
        LOGGER.info("**ShopWindow** Data initialized successfully");
    }

    /**
     * Shows the context menu on the search field.
     * 
     * @param event Context menu event
     */
    private void showContextMenu(ContextMenuEvent event) {
        LOGGER.info("**ShopWindow** Context menu opened");
        contextMenu.show(sear, event.getScreenX(), event.getScreenY());
        event.consume();
    }

    /**
     * Handles the event to empty the cart list.
     * 
     * @param event Button action event
     */
    @FXML
    private void emptyList(ActionEvent event) {
        clearC();
    }

    /**
     * Handles the action to generate a cart report.
     * 
     * This method uses ReportService to generate a report of the current cart.
     * If the cart is empty, shows a warning message.
     */
    private void handleGenerarReporteAction() {
        LOGGER.info("**ShopWindow** Generating cart report");

        if (talis == null || talis.isEmpty()) {
            LOGGER.warning("**ShopWindow** Cart is empty, cannot generate report");
            return;
        }

        try {
           ReportService reportService = new ReportService();
            reportService.generateCartReport(talis);
            LOGGER.info("**ShopWindow** Cart report generated successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "**ShopWindow** Error generating cart report", e);
        }
    }

    /**
     * Clears the shopping cart.
     * 
     * This method deletes the cart file and clears local lists.
     */
    private void clearC() {
        //File fichAux = new File("Carrito"+uname+"aux.dat");
        // fichAux.renameTo(fichC);
        fichC.delete();
        talis.clear();
        carrito = FXCollections.observableArrayList(talis);
        CartTable.setItems(carrito);

    }

    /**
     * Handles purchasing the current cart.
     * 
     * Shows a confirmation dialog and, if the user confirms, reduces the stock
     * of purchased products and clears the cart.
     * 
     * @param event Button action event
     */
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

                for (CartItem ci : talis) {
                
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

    /**
     * Navigates to the companies window.
     * 
     * @param event Button action event
     */
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

    /**
     * Navigates to the user profile window.
     * 
     * @param event Button action event
     */
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

    /**
     * Creates a visual card for a product.
     * 
     * Generates a graphical component displaying the image, name, description
     * and price of the product, along with a button to add it to the cart.
     * 
     * @param product Product to display
     * @return JavaFX Node representing the product card
     */
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

    /**
     * Adds a product to the shopping cart.
     * 
     * Shows a dialog to select the product size and then adds
     * the item to the cart, updating both the in-memory list and the file.
     * 
     * @param product Product to add to the cart
     */
    private void addToCart(Product product) {
        Size sz = null;


        List<String> sizeLabels = new ArrayList<>();
        for (Size s : product.getSizes()) {
            sizeLabels.add(s.getLabel());
        }


        ChoiceDialog<String> dialog = new ChoiceDialog<>(sizeLabels.get(0), sizeLabels);
        dialog.setTitle("Choose Size");
        dialog.setHeaderText("What size do you want?");
        dialog.setContentText("Select a size:");


        String selectedLabel = dialog.showAndWait().orElse(null);

        if (selectedLabel != null) {
            for (Size s : product.getSizes()) {
                if (s.getLabel().equals(selectedLabel)) {
                    sz = s;
                    System.out.println(s.getLabel());
                    break;
                }
            }
        }

        System.out.println("añadiendo a carrito");
        CartItem nuevoItem = new CartItem(product, sz);
        
        talis.add(nuevoItem);
        if (fichC.exists()) {
            try {
                MyObjectOutputStream moos = new MyObjectOutputStream(new FileOutputStream(fichC, true));
                moos.writeObject(nuevoItem);
                moos.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichC));
                oos.writeObject(nuevoItem);
                oos.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ShopWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        carrito = FXCollections.observableArrayList(talis);

        CartTable.setItems(carrito);

        System.out.println(
                "añadido a carrito\n " + carrito);

    }

    /**
     * Loads an image from different sources.
     * 
     * Can load images from classpath (if path starts with "/")
     * or from the file system.
     * 
     * @param path Path of the image to load
     * @return Loaded Image object
     * @throws IllegalArgumentException If the image is not found at the specified path
     */
    public static Image
            loadImage(String path) {

        
        if (path.startsWith("/")) {
            InputStream is = Product.class
                    .getResourceAsStream(path);
            if (is == null) {
                throw new IllegalArgumentException("Classpath image not found: " + path);
            }
            return new Image(is);
        }

  
        Path filePath = Paths.get(path);

        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("File image not found: " + path);
        }

        return new Image(filePath.toUri().toString());
    }

    /**
     * Handles the search event in the text field.
     * 
     * @param event Keyboard event
     */
    @FXML
    private void searcher(KeyEvent event) {
    }

    /**
     * Opens the user manual in PDF format.
     * 
     * @param event Button action event
     */
    @FXML
     private void manual_open(ActionEvent event) {
        LOGGER.info("**ShopWindow** Opening user manual");
        
        try {
            File pdf = new File("pdfs/User_Manual.pdf");
            if (!pdf.exists()) {
                LOGGER.warning("**ShopWindow** User manual not found: pdfs/User_Manual.pdf");
                return;
            }
            Desktop.getDesktop().open(pdf);
            LOGGER.info("**ShopWindow** User manual opened successfully");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "**ShopWindow** Error opening user manual", ex);
        }
    }
}