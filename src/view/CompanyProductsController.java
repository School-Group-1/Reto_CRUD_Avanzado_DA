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
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Product;
import javafx.scene.image.Image;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Stage;
import model.Company;
import model.Profile;
import model.Size;
import report.ReportService;

/**
 * FXML Controller class for displaying products of a specific company.
 * Shows product cards with details, allows size selection and adding to cart.
 * 
 * @author acer
 */
public class CompanyProductsController implements Initializable {

    @FXML
    private VBox productContainer;
    @FXML
    private Label titleLabel;
    @FXML
    private ImageView bigImage;
    @FXML
    private HBox sizesContainer;
    @FXML
    private Button selectedSize;
    @FXML
    private Button profileButton;
    @FXML
    private Button goBackButton;

    private static final Logger LOGGER = Logger.getLogger(CompanyProductsController.class.getName());

    private Company company;
    private Product selectedProduct;
    private List<Product> products;
    private Profile profile;
    private Controller cont;
    private boolean isDataInitialized = false;
    private boolean isSizeSelected = false;
    private String selectedSizeLabel = "";
    private ContextMenu contextMenu;
    private MenuItem reportItem;

    /**
     * Initializes the controller with company and product data.
     */
    public void initData(Company company, List<Product> products, Profile profile, Controller cont) {
        this.company = company;
        this.products = products;
        this.profile = profile;
        this.cont = cont;
        this.isDataInitialized = true;

        LOGGER.log(Level.INFO, "**CompanyProducts** Initializing data. Company: {0}, Products: {1}",
                new Object[]{company != null ? company.getName() : "null", products != null ? products.size() : 0});

        contextMenu = new ContextMenu();
        reportItem = new MenuItem("Report");
        reportItem.setOnAction(e -> handleImprimirAction());
        contextMenu.getItems().add(reportItem);
        productContainer.setOnContextMenuRequested(this::showContextMenu);

        updateUI();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("**CompanyProducts** Initializing Company Products Window Controller");
        if (isDataInitialized && company != null) {
            updateUI();
        }
    }

    private void updateUI() {
        if (!areEssentialComponentsValid()) {
            return;
        }

        titleLabel.setText(company.getName() + " - Products");
        loadProducts();
        LOGGER.log(Level.INFO, "**CompanyProducts** UI updated for company: {0}", company.getName());
    }

    private boolean areEssentialComponentsValid() {
        if (titleLabel == null) {
            LOGGER.warning("**CompanyProducts** ERROR: titleLabel es null");
            return false;
        }
        if (productContainer == null) {
            LOGGER.warning("**CompanyProducts** ERROR: productContainer es null");
            return false;
        }
        if (company == null) {
            LOGGER.warning("**CompanyProducts** ERROR: company es null");
            return false;
        }
        return true;
    }

    private void loadProducts() {
        clearProductContainer();

        if (products == null || products.isEmpty()) {
            displayNoProductsMessage();
            LOGGER.info("**CompanyProducts** No products available");
            return;
        }

        for (Product product : products) {
            HBox productCard = createProductCard(product);
            VBox.setMargin(productCard, new Insets(0, 0, 10, 0));
            productContainer.getChildren().add(productCard);
        }
        
        LOGGER.log(Level.INFO, "**CompanyProducts** Loaded {0} products", products.size());
    }

    private void clearProductContainer() {
        LOGGER.fine("**CompanyProducts** Clearing product container");
        productContainer.getChildren().clear();
    }

    private void displayNoProductsMessage() {
        LOGGER.info("**CompanyProducts** Displaying 'no products' message");
        Label emptyLabel = new Label("No hay productos disponibles");
        emptyLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
        productContainer.getChildren().add(emptyLabel);
    }

    private HBox createProductCard(Product product) {
        LOGGER.log(Level.FINE, "**CompanyProducts** Creating card for product: {0}", product.getName());
        HBox card = new HBox(15);
        
        ImageView productImage = new ImageView(loadProductImage(product.getImage()));
        productImage.setFitWidth(120);
        productImage.setFitHeight(120);
        productImage.setPreserveRatio(true);
        
        VBox infoBox = new VBox(10);
        infoBox.setPrefWidth(200);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(180);
        
        String description = product.getDescription();
        if (description == null) description = "Sin descripción";
        if (description.length() > 80) description = description.substring(0, 77) + "...";
        
        Label descriptionLabel = new Label(description);
        descriptionLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(180);
        
        HBox bottomBox = new HBox();
        bottomBox.setSpacing(20);
        bottomBox.setAlignment(Pos.CENTER_LEFT);

        Label priceLabel = new Label(String.format("€%.2f", product.getPrice()));
        priceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f954a;");
        
        Button viewButton = new Button("View");
        viewButton.setStyle("-fx-background-color: #0f954a; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 15 8 15;");
        viewButton.setOnAction(e -> showProductDetail(product));

        bottomBox.getChildren().addAll(priceLabel, viewButton);
        infoBox.getChildren().addAll(nameLabel, descriptionLabel, bottomBox);
        card.getChildren().addAll(productImage, infoBox);
        
        return card;
    }

    private void showProductDetail(Product product) {
        LOGGER.log(Level.INFO, "**CompanyProducts** Showing details for product: {0}", product.getName());
        this.selectedProduct = product;
        resetSizeSelection();
        displayProductImage(product);
        loadSizes(product);
    }

    private void resetSizeSelection() {
        LOGGER.fine("**CompanyProducts** Resetting size selection");
        isSizeSelected = false;
        selectedSizeLabel = "";
        disableAddToCartButton();
    }

    private void displayProductImage(Product product) {
        if (bigImage == null) {
            LOGGER.warning("**CompanyProducts** bigImage is null!");
            return;
        }

        try {
            Image image = loadProductImage(product.getImage());
            bigImage.setImage(image);
            bigImage.setFitWidth(300);
            bigImage.setFitHeight(300);
            bigImage.setPreserveRatio(true);
            LOGGER.fine("**CompanyProducts** Product image displayed");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "**CompanyProducts** Error loading product image, using default", e);
            bigImage.setImage(loadDefaultImage());
        }
    }

    private Image loadDefaultImage() {
        try {
            return new Image(getClass().getResourceAsStream("/images/default-product.png"));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "**CompanyProducts** Could not load default image", e);
            return null;
        }
    }

    private void loadSizes(Product product) {
        if (sizesContainer == null) {
            LOGGER.warning("**CompanyProducts** sizesContainer is null!");
            return;
        }

        sizesContainer.getChildren().clear();
        List<Size> sizes = product.getSizes();

        if (sizes == null || sizes.isEmpty()) {
            LOGGER.info("**CompanyProducts** No sizes available for this product");
            return;
        }

        for (Size size : sizes) {
            Button sizeButton = new Button(size.getLabel());
            applySizeButtonStyle(sizeButton, false);
            sizeButton.setTooltip(new Tooltip("Stock disponible: " + size.getStock()));
            sizeButton.setOnAction(e -> handleSizeSelection(sizeButton, size));
            sizesContainer.getChildren().add(sizeButton);
        }
        
        LOGGER.log(Level.INFO, "**CompanyProducts** Loaded {0} sizes for product: {1}",
                new Object[]{sizes.size(), product.getName()});
    }

    private void handleSizeSelection(Button selectedButton, Size size) {
        LOGGER.log(Level.INFO, "**CompanyProducts** Size selected: {0}", size.getLabel());
        resetAllSizeButtons();
        applySizeButtonStyle(selectedButton, true);
        isSizeSelected = true;
        selectedSizeLabel = size.getLabel();
        enableAddToCartButton();
        LOGGER.log(Level.INFO, "**CompanyProducts** Size selected: {0} - Stock: {1}",
                new Object[]{size.getLabel(), size.getStock()});
    }

    private void resetAllSizeButtons() {
        LOGGER.fine("**CompanyProducts** Resetting all size buttons");
        for (Node node : sizesContainer.getChildren()) {
            if (node instanceof Button) {
                applySizeButtonStyle((Button) node, false);
            }
        }
    }

    private void applySizeButtonStyle(Button button, boolean isSelected) {
        if (isSelected) {
            button.setStyle("-fx-background-color: #0f954a; -fx-border-color: #0f954a; -fx-border-radius: 5; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15 8 15;");
        } else {
            button.setStyle("-fx-background-color: white; -fx-border-color: #0f954a; -fx-border-radius: 5; -fx-text-fill: #0f954a; -fx-font-weight: bold; -fx-padding: 8 15 8 15;");
        }
    }

    private void disableAddToCartButton() {
        if (selectedSize != null) {
            selectedSize.setDisable(true);
            selectedSize.setStyle("-fx-border-color: #0f954a; -fx-background-color: #FFFFFF; -fx-text-fill: #0f954a; -fx-font-weight: bold;");
            LOGGER.fine("**CompanyProducts** Add to cart button disabled");
        }
    }

    private void enableAddToCartButton() {
        if (selectedSize != null) {
            selectedSize.setDisable(false);
            selectedSize.setStyle("-fx-background-color: #0f954a; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
            LOGGER.fine("**CompanyProducts** Add to cart button enabled");
        }
    }

    /**
     * Adds the selected product to the shopping cart.
     */
    @FXML
    private void addToCart() {
        LOGGER.info("**CompanyProducts** Adding product to cart");
        LOGGER.log(Level.INFO, "**CompanyProducts** === ADDING TO CART === Product: {0}, Size: {1}, Price: €{2}",
                new Object[]{selectedProduct.getName(), selectedSizeLabel, selectedProduct.getPrice()});
        showConfirmationMessage();
    }

    private void showConfirmationMessage() {
        LOGGER.info("**CompanyProducts** Showing confirmation message");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Producto añadido");
        alert.setHeaderText(null);
        alert.setContentText(selectedProduct.getName() + " (Talla: " + selectedSizeLabel + ") añadido al carrito");
        alert.showAndWait();
    }

    /**
     * Navigates back to the companies window.
     */
    @FXML
    private void goBackToCompanies(ActionEvent event) {
        LOGGER.info("**CompanyProducts** Switching back to companies window");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CompanyWindow.fxml"));
            Parent root = loader.load();

            CompanyWindowController controller = loader.getController();
            if (controller != null) {
                controller.initData(profile, cont);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            LOGGER.info("**CompanyProducts** Successfully switched to companies window");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**CompanyProducts** Error switching to companies window: ", e);
        }
    }

    /**
     * Navigates to the store window.
     */
    @FXML
    private void goToStore(ActionEvent event) {
        LOGGER.info("**CompanyProducts** Switching to store window");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ShopWindow.fxml"));
            Parent root = loader.load();

            view.ShopWindowController controller = loader.getController();
            if (controller != null) {
                controller.initData(profile, cont);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            LOGGER.info("**CompanyProducts** Successfully switched to store window");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**CompanyProducts** Error switching to store window: ", e);
        }
    }

    /**
     * Navigates to the user profile window.
     */
    @FXML
    private void goToProfile(ActionEvent event) {
        LOGGER.info("**CompanyProducts** Switching to profile window");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileWindow.fxml"));
            Parent root = loader.load();

            view.ProfileWindowController controller = loader.getController();
            if (controller != null) {
                controller.initData(profile, cont);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            LOGGER.info("**CompanyProducts** Successfully switched to profile window");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**CompanyProducts** Error switching to profile window: ", e);
        }
    }

    /**
     * Loads a product image from the specified path.
     * 
     * @param path The path to the image file
     * @return Image object loaded from the path
     * @throws IllegalArgumentException if the image cannot be found
     */
    public static Image loadProductImage(String path) {
        LOGGER.log(Level.INFO, "**CompanyProducts** Loading product image from: {0}", path);

        if (path.startsWith("/")) {
            InputStream is = Product.class.getResourceAsStream(path);
            if (is == null) {
                LOGGER.log(Level.SEVERE, "**CompanyProducts** Classpath image not found: {0}", path);
                throw new IllegalArgumentException("Classpath image not found: " + path);
            }
            return new Image(is);
        }

        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) {
            LOGGER.log(Level.SEVERE, "**CompanyProducts** File image not found: {0}", path);
            throw new IllegalArgumentException("File image not found: " + path);
        }

        LOGGER.info("**CompanyProducts** Image loaded successfully");
        return new Image(filePath.toUri().toString());
    }

    @FXML
    private void showContextMenu(ContextMenuEvent event) {
        LOGGER.fine("**CompanyProducts** Showing context menu");
        contextMenu.show(productContainer, event.getScreenX(), event.getScreenY());
        event.consume();
    }

    private void handleImprimirAction() {
        LOGGER.info("**CompanyProducts** Generating company products report");
        if (company != null && products != null && !products.isEmpty()) {
            LOGGER.log(Level.INFO, "**CompanyProducts** Generating report for {0} products", products.size());
            ReportService reportService = new ReportService();
            reportService.generateCompanyProductsReport(company, products);
            LOGGER.info("**CompanyProducts** Report generated successfully");
        } else {
            LOGGER.warning("**CompanyProducts** No products available to generate report");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Data");
            alert.setHeaderText(null);
            alert.setContentText("No products available to generate report.");
            alert.showAndWait();
        }
    }

    /**
     * Opens the user manual PDF file.
     */
    @FXML
    private void openUserManual(ActionEvent event) {
        LOGGER.info("**CompanyProducts** Opening user manual");

        try {
            File pdf = new File("pdfs/User_Manual.pdf");
            if (!pdf.exists()) {
                LOGGER.warning("**CompanyProducts** User manual not found at: pdfs/User_Manual.pdf");
                return;
            }

            Desktop.getDesktop().open(pdf);
            LOGGER.info("**CompanyProducts** User manual opened successfully");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "**CompanyProducts** Error opening user manual: ", ex);
        }
    }
}