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

public class CompanyProductsController implements Initializable {

    // Componentes FXML
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
        boolean areComponentsValid = true;

        if (titleLabel == null) {
            LOGGER.warning("**CompanyProducts** ERROR: titleLabel es null");
            areComponentsValid = false;
        }

        if (productContainer == null) {
            LOGGER.warning("**CompanyProducts** ERROR: productContainer es null");
            areComponentsValid = false;
        }

        if (company == null) {
            LOGGER.warning("**CompanyProducts** ERROR: company es null");
            areComponentsValid = false;
        }

        return areComponentsValid;
    }

    private void loadProducts() {
        clearProductContainer();

        if (hasNoProducts()) {
            displayNoProductsMessage();
            return;
        }

        displayProducts();
        LOGGER.log(Level.INFO, "**CompanyProducts** Loaded {0} products", products.size());
    }

    private void clearProductContainer() {
        LOGGER.fine("**CompanyProducts** Clearing product container");
        productContainer.getChildren().clear();
    }

    private boolean hasNoProducts() {
        boolean noProducts = products == null || products.isEmpty();
        if (noProducts) {
            LOGGER.info("**CompanyProducts** No products available");
        }
        return noProducts;
    }

    private void displayNoProductsMessage() {
        LOGGER.info("**CompanyProducts** Displaying 'no products' message");
        Label emptyLabel = new Label("No hay productos disponibles");
        emptyLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
        productContainer.getChildren().add(emptyLabel);
    }

    private void displayProducts() {
        LOGGER.log(Level.FINE, "**CompanyProducts** Displaying {0} products", products.size());
        for (Product product : products) {
            HBox productCard = createProductCard(product);
            VBox.setMargin(productCard, new Insets(0, 0, 10, 0));
            productContainer.getChildren().add(productCard);
        }
    }

    private HBox createProductCard(Product product) {
        LOGGER.log(Level.FINE, "**CompanyProducts** Creating card for product: {0}", product.getName());
        HBox card = createCardBase();

        ImageView productImage = createProductImageView(product);
        VBox infoBox = createProductInfoBox(product);

        card.getChildren().addAll(productImage, infoBox);
        return card;
    }

    private HBox createCardBase() {
        return new HBox(15);
    }

    private ImageView createProductImageView(Product product) {
        ImageView productImage = new ImageView(loadProductImage(product.getImage()));
        productImage.setFitWidth(120);
        productImage.setFitHeight(120);
        productImage.setPreserveRatio(true);
        return productImage;
    }

    private Image loadDefaultImage() {
        try {
            return new Image(getClass().getResourceAsStream("/images/default-product.png"));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "**CompanyProducts** Could not load default image", e);
            return null;
        }
    }

    private VBox createProductInfoBox(Product product) {
        VBox infoBox = new VBox(10);
        infoBox.setPrefWidth(200);

        Label nameLabel = createProductNameLabel(product);
        Label descriptionLabel = createProductDescriptionLabel(product);
        HBox bottomBox = createProductBottomBox(product);

        infoBox.getChildren().addAll(nameLabel, descriptionLabel, bottomBox);
        return infoBox;
    }

    private Label createProductNameLabel(Product product) {
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(180);
        return nameLabel;
    }

    private Label createProductDescriptionLabel(Product product) {
        String description = formatProductDescription(product.getDescription());
        Label descriptionLabel = new Label(description);
        descriptionLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(180);
        return descriptionLabel;
    }

    private String formatProductDescription(String description) {
        if (description == null) {
            return "Sin descripción";
        }
        if (description.length() > 80) {
            return description.substring(0, 77) + "...";
        }
        return description;
    }

    private HBox createProductBottomBox(Product product) {
        HBox bottomBox = new HBox();
        bottomBox.setSpacing(20);
        bottomBox.setAlignment(Pos.CENTER_LEFT);

        Label priceLabel = createPriceLabel(product);
        Button viewButton = createViewButton(product);

        bottomBox.getChildren().addAll(priceLabel, viewButton);
        return bottomBox;
    }

    private Label createPriceLabel(Product product) {
        Label priceLabel = new Label(String.format("€%.2f", product.getPrice()));
        priceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f954a;");
        return priceLabel;
    }

    private Button createViewButton(Product product) {
        Button viewButton = new Button("View");
        viewButton.setStyle(
                "-fx-background-color: #0f954a;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 5;"
                + "-fx-padding: 8 15 8 15;"
        );
        viewButton.setOnAction(e -> showProductDetail(product));
        return viewButton;
    }

    // ==================== DETALLES DEL PRODUCTO ====================
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

    // ==================== GESTIÓN DE TALLAS ====================
    private void loadSizes(Product product) {
        if (sizesContainer == null) {
            LOGGER.warning("**CompanyProducts** sizesContainer is null!");
            return;
        }

        clearSizesContainer();
        List<Size> sizes = product.getSizes();

        if (sizes == null || sizes.isEmpty()) {
            LOGGER.info("**CompanyProducts** No sizes available for this product");
            return;
        }

        displaySizes(sizes);
        LOGGER.log(Level.INFO, "**CompanyProducts** Loaded {0} sizes for product: {1}",
                new Object[]{sizes.size(), product.getName()});
    }

    private void clearSizesContainer() {
        LOGGER.fine("**CompanyProducts** Clearing sizes container");
        sizesContainer.getChildren().clear();
    }

    private void displaySizes(List<Size> sizes) {
        for (Size size : sizes) {
            Button sizeButton = createSizeButton(size);
            sizesContainer.getChildren().add(sizeButton);
        }
    }

    private Button createSizeButton(Size size) {
        LOGGER.log(Level.FINE, "**CompanyProducts** Creating button for size: {0}", size.getLabel());
        Button sizeButton = new Button(size.getLabel());
        applySizeButtonStyle(sizeButton, false);
        sizeButton.setTooltip(createSizeTooltip(size));
        sizeButton.setOnAction(e -> handleSizeSelection(sizeButton, size));
        return sizeButton;
    }

    private Tooltip createSizeTooltip(Size size) {
        return new Tooltip("Stock disponible: " + size.getStock());
    }

    private void handleSizeSelection(Button selectedButton, Size size) {
        LOGGER.log(Level.INFO, "**CompanyProducts** Size selected: {0}", size.getLabel());
        resetAllSizeButtons();
        selectSizeButton(selectedButton);
        updateSizeSelection(size);
        enableAddToCartButton();
    }

    private void resetAllSizeButtons() {
        LOGGER.fine("**CompanyProducts** Resetting all size buttons");
        for (Node node : sizesContainer.getChildren()) {
            if (node instanceof Button) {
                applySizeButtonStyle((Button) node, false);
            }
        }
    }

    private void selectSizeButton(Button button) {
        applySizeButtonStyle(button, true);
    }

    private void applySizeButtonStyle(Button button, boolean isSelected) {
        if (isSelected) {
            button.setStyle(
                    "-fx-background-color: #0f954a;"
                    + "-fx-border-color: #0f954a;"
                    + "-fx-border-radius: 5;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-weight: bold;"
                    + "-fx-padding: 8 15 8 15;"
            );
        } else {
            button.setStyle(
                    "-fx-background-color: white;"
                    + "-fx-border-color: #0f954a;"
                    + "-fx-border-radius: 5;"
                    + "-fx-text-fill: #0f954a;"
                    + "-fx-font-weight: bold;"
                    + "-fx-padding: 8 15 8 15;"
            );
        }
    }

    private void updateSizeSelection(Size size) {
        isSizeSelected = true;
        selectedSizeLabel = size.getLabel();
        LOGGER.log(Level.INFO, "**CompanyProducts** Size selected: {0} - Stock: {1}",
                new Object[]{size.getLabel(), size.getStock()});
    }

    // ==================== GESTIÓN DEL BOTÓN ADD TO CART ====================
    private void disableAddToCartButton() {
        if (selectedSize != null) {
            selectedSize.setDisable(true);
            selectedSize.setStyle(
                    "-fx-border-color: #0f954a; -fx-background-color: #FFFFFF; "
                    + "-fx-text-fill: #0f954a; -fx-font-weight: bold;"
            );
            LOGGER.fine("**CompanyProducts** Add to cart button disabled");
        }
    }

    private void enableAddToCartButton() {
        if (selectedSize != null) {
            selectedSize.setDisable(false);
            selectedSize.setStyle(
                    "-fx-background-color: #0f954a; -fx-text-fill: white; "
                    + "-fx-font-weight: bold; -fx-background-radius: 5;"
            );
            LOGGER.fine("**CompanyProducts** Add to cart button enabled");
        }
    }

    @FXML
    private void addToCart() {
        LOGGER.info("**CompanyProducts** Adding product to cart");

        addProductToCart();
        showConfirmationMessage();
    }

    private void addProductToCart() {
        LOGGER.log(Level.INFO, "**CompanyProducts** === ADDING TO CART === Product: {0}, Size: {1}, Price: €{2}",
                new Object[]{selectedProduct.getName(), selectedSizeLabel, selectedProduct.getPrice()});

        // Aquí implementar la lógica para añadir al carrito
        // Ejemplo: cont.addToCart(selectedProduct, selectedSizeLabel);
    }

    private void showConfirmationMessage() {
        LOGGER.info("**CompanyProducts** Showing confirmation message");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Producto añadido");
        alert.setHeaderText(null);
        alert.setContentText(
                selectedProduct.getName()
                + " (Talla: " + selectedSizeLabel
                + ") añadido al carrito"
        );
        alert.showAndWait();
    }

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


    public static Image loadProductImage(String path) {
        LOGGER.log(Level.INFO, "**CompanyProducts** Loading product image from: {0}", path);

        // 1) Classpath resource (starts with /)
        if (path.startsWith("/")) {
            InputStream is = Product.class.getResourceAsStream(path);
            if (is == null) {
                LOGGER.log(Level.SEVERE, "**CompanyProducts** Classpath image not found: {0}", path);
                throw new IllegalArgumentException("Classpath image not found: " + path);
            }
            return new Image(is);
        }

        // 2) File system path (relative or absolute)
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
