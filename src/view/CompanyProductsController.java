/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
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

    // Datos de la aplicación
    private Company company;
    private Product selectedProduct;
    private List<Product> products;
    private Profile profile;
    private Controller cont;

    // Flags de estado
    private boolean isDataInitialized = false;
    private boolean isSizeSelected = false;
    private String selectedSizeLabel = "";
    private ContextMenu contextMenu;
    private MenuItem reportItem;

    // ==================== MÉTODOS DE INICIALIZACIÓN ====================
    public void initData(Company company, List<Product> products, Profile profile, Controller cont) {
        this.company = company;
        this.products = products;
        this.profile = profile;
        this.cont = cont;
        this.isDataInitialized = true;
        
        contextMenu = new ContextMenu();

        reportItem = new MenuItem("Report");
        reportItem.setOnAction(e -> handleImprimirAction());

        contextMenu.getItems().add(reportItem);
        productContainer.setOnContextMenuRequested(this::showContextMenu);

        updateUI();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
    }

    // ==================== VALIDACIONES ====================
    private boolean areEssentialComponentsValid() {
        boolean areComponentsValid = true;

        if (titleLabel == null) {
            System.err.println("ERROR: titleLabel es null");
            areComponentsValid = false;
        }

        if (productContainer == null) {
            System.err.println("ERROR: productContainer es null");
            areComponentsValid = false;
        }

        if (company == null) {
            System.err.println("ERROR: company es null");
            areComponentsValid = false;
        }

        return areComponentsValid;
    }

    private boolean isProductSelectionValid() {
        if (selectedProduct == null) {
            System.err.println("ERROR: No hay producto seleccionado");
            return false;
        }

        if (!isSizeSelected) {
            System.err.println("ERROR: No se ha seleccionado una talla");
            return false;
        }

        return true;
    }

    // ==================== GESTIÓN DE PRODUCTOS ====================
    private void loadProducts() {
        clearProductContainer();

        if (hasNoProducts()) {
            displayNoProductsMessage();
            return;
        }

        displayProducts();
    }

    private void clearProductContainer() {
        productContainer.getChildren().clear();
    }

    private boolean hasNoProducts() {
        return products == null || products.isEmpty();
    }

    private void displayNoProductsMessage() {
        Label emptyLabel = new Label("No hay productos disponibles");
        emptyLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
        productContainer.getChildren().add(emptyLabel);
    }

    private void displayProducts() {
        for (Product product : products) {
            HBox productCard = createProductCard(product);

            VBox.setMargin(productCard, new Insets(0, 0, 10, 0));

            productContainer.getChildren().add(productCard);
        }
    }

    private HBox createProductCard(Product product) {
        HBox card = createCardBase();

        ImageView productImage = createProductImageView(product);
        VBox infoBox = createProductInfoBox(product);

        card.getChildren().addAll(productImage, infoBox);
        return card;
    }

    private HBox createCardBase() {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: #e0e0e0;"
                + "-fx-border-radius: 10;"
                + "-fx-border-width: 1;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );
        card.setPrefWidth(350);
        card.setMaxWidth(350);
        return card;
    }

    private ImageView createProductImageView(Product product) {
        ImageView productImage = new ImageView(
                loadProductImage(product.getImage())
        );

        productImage.setFitWidth(120);
        productImage.setFitHeight(120);
        productImage.setPreserveRatio(true);

        return productImage;
    }

    private Image loadDefaultImage() {
        try {
            return new Image(getClass().getResourceAsStream("/images/default-product.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar imagen por defecto");
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
        this.selectedProduct = product;
        resetSizeSelection();

        displayProductImage(product);
        loadSizes(product);
    }

    private void resetSizeSelection() {
        isSizeSelected = false;
        selectedSizeLabel = "";
        disableAddToCartButton();
    }

    private void displayProductImage(Product product) {
        if (bigImage == null) {
            System.err.println("bigImage es null!");
            return;
        }

        try {
            Image image = loadProductImage(product.getImage());
            bigImage.setImage(image);
            bigImage.setFitWidth(300);
            bigImage.setFitHeight(300);
            bigImage.setPreserveRatio(true);
        } catch (Exception e) {
            bigImage.setImage(loadDefaultImage());
        }
    }

    // ==================== GESTIÓN DE TALLAS ====================
    private void loadSizes(Product product) {
        if (sizesContainer == null) {
            System.err.println("sizesContainer es null!");
            return;
        }

        clearSizesContainer();

        List<Size> sizes = product.getSizes();
        if (sizes == null || sizes.isEmpty()) {
            System.out.println("No hay tallas disponibles para este producto");
            return;
        }

        displaySizes(sizes);
    }

    private void clearSizesContainer() {
        sizesContainer.getChildren().clear();
    }

    private void displaySizes(List<Size> sizes) {
        for (Size size : sizes) {
            Button sizeButton = createSizeButton(size);
            sizesContainer.getChildren().add(sizeButton);
        }
    }

    private Button createSizeButton(Size size) {
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
        resetAllSizeButtons();
        selectSizeButton(selectedButton);
        updateSizeSelection(size);
        enableAddToCartButton();
    }

    private void resetAllSizeButtons() {
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
        System.out.println("Talla seleccionada: " + size.getLabel() + " - Stock: " + size.getStock());
    }

    // ==================== GESTIÓN DEL BOTÓN ADD TO CART ====================
    private void disableAddToCartButton() {
        if (selectedSize != null) {
            selectedSize.setDisable(true);
            selectedSize.setStyle(
                    "-fx-border-color: #0f954a; -fx-background-color: #FFFFFF; "
                    + "-fx-text-fill: #0f954a; -fx-font-weight: bold;"
            );
        }
    }

    private void enableAddToCartButton() {
        if (selectedSize != null) {
            selectedSize.setDisable(false);
            selectedSize.setStyle(
                    "-fx-background-color: #0f954a; -fx-text-fill: white; "
                    + "-fx-font-weight: bold; -fx-background-radius: 5;"
            );
        }
    }

    @FXML
    private void addToCart() {
        if (!isProductSelectionValid()) {
            return;
        }

        addProductToCart();
        showConfirmationMessage();
    }

    private void addProductToCart() {
        System.out.println("=== AÑADIENDO AL CARRITO ===");
        System.out.println("Producto: " + selectedProduct.getName());
        System.out.println("Talla: " + selectedSizeLabel);
        System.out.println("Precio: €" + selectedProduct.getPrice());

        // Aquí implementar la lógica para añadir al carrito
        // Ejemplo: cont.addToCart(selectedProduct, selectedSizeLabel);
    }

    private void showConfirmationMessage() {
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

    // ==================== NAVEGACIÓN ====================
    @FXML
    private void goBackToCompanies(ActionEvent event) {
        navigateToWindow("/view/CompanyWindow.fxml", event, CompanyWindowController.class);
    }

    @FXML
    private void goToStore(ActionEvent event) {
        navigateToWindow("/view/ShopWindow.fxml", event, view.ShopWindowController.class);
    }

    @FXML
    private void goToProfile(ActionEvent event) {
        navigateToWindow("/view/ProfileWindow.fxml", event, view.ProfileWindowController.class);
    }

    private <T> void navigateToWindow(String fxmlPath, ActionEvent event, Class<T> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            T controller = loader.getController();
            if (controller instanceof CompanyWindowController) {
                ((CompanyWindowController) controller).initData(profile, cont);
            } else if (controller instanceof view.ShopWindowController) {
                ((view.ShopWindowController) controller).initData(profile, cont);
            } else if (controller instanceof view.ProfileWindowController) {
                ((view.ProfileWindowController) controller).initData(profile, cont);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            closeCurrentWindow(event);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeCurrentWindow(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
    
    public static Image loadProductImage(String path) {

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
    private void showContextMenu(ContextMenuEvent event) {
        contextMenu.show(productContainer, event.getScreenX(), event.getScreenY());
        event.consume();
    }
    
    private void handleImprimirAction() {
        if (company != null && products != null && !products.isEmpty()) {
            ReportService reportService = new ReportService();
            reportService.generateCompanyProductsReport(company, products);

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Data");
            alert.setHeaderText(null);
            alert.setContentText("No products available to generate report.");
            alert.showAndWait();
        }
    }
}
