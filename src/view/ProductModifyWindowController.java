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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Company;
import model.DBImplementation;
import model.Product;
import model.Profile;
import model.Purchase;
import model.Size;

/**
 * FXML Controller class
 *
 * @author Mosi
 */
public class ProductModifyWindowController implements Initializable {

    private Controller cont = new Controller(new DBImplementation());
    private Company selectedCompany = null;
    private Product selectedProduct = null;
    private List<Size> selectedProductSizes = new ArrayList<>();
    private Size selectedSize = null;
    private Profile profile;

    private static final Logger LOGGER = Logger.getLogger(ProductModifyWindowController.class.getName());

    @FXML
    private ComboBox<String> companyCombobox;
    @FXML
    private VBox productsVbox;
    @FXML
    private HBox sizesHbox;
    @FXML
    private LineChart<Number, Number> linechart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Spinner<Integer> stockCountSpinner;
    @FXML
    private TextField sizeTextField;
    @FXML
    private Button users;
    
    @FXML
    private Button companies;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("**ProductModifyWindow** Initializing Product Modify Window Controller");
    }

    //method to receive profile, controller and data
    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;

        // Add companies to the combobox
        List<Company> companies = cont.findAllCompanies();
        LOGGER.log(Level.INFO, "**ProductModifyWindow** Loaded {0} companies", companies.size());

        for (Company comp : companies) {
            companyCombobox.getItems().add(comp.getName());
        }

        // Prepare the line chart (https://youtu.be/HWfZPiPu1sI?si=KFfRQ06_IlluRXsj good luck)
        xAxis.setLabel("Days");
        xAxis.setAutoRanging(true);

        yAxis.setLabel("Sales");
        yAxis.setAutoRanging(true);

        linechart.setTitle("Product Sales");

        stockCountSpinner.disableProperty();

        LOGGER.info("**ProductModifyWindow** Finished loading window data");
    }

    @FXML
    private void goToCompanies(ActionEvent event) {
        LOGGER.info("**ProductModifyWindow** Switching to company window");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CompaniesTable.fxml"));
            Parent root = loader.load();

            CompaniesTableController viewController = loader.getController();
            viewController.initData(profile, cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

            LOGGER.info("**ProductModifyWindow** Company window opened successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProductModifyWindow** error switching to company window", e);
        }
    }

    @FXML
    private void goToUsers(ActionEvent event) {
        LOGGER.info("**ProductModifyWindow** Switching to users window");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserTable.fxml"));
            Parent root = loader.load();

            UserTableController viewController = loader.getController();
            viewController.initData(profile, cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

            LOGGER.info("**ProductModifyWindow** Users window opened successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProductModifyWindow** error switching to users window", e);
        }
    }

    @FXML
    private void selectCompany() {
        LOGGER.info("**ProductModifyWindow** Selecting a company...");
        resetData();

        List<Company> companies = cont.findAllCompanies();
        List<Product> products = null;
        String companyName = companyCombobox.getValue();
        productsVbox.getChildren().clear();

        for (Company comp : companies) {
            if (comp.getName().equals(companyName)) {
                selectedCompany = comp;
                break;
            }
        }

        if (selectedCompany != null) {
            products = cont.findProductsByCompany(selectedCompany);
            LOGGER.log(Level.INFO, "**ProductModifyWindow** Found {0} products for company {1}",
                    new Object[]{products.size(), selectedCompany.getName()});

            for (Product prod : products) {
                Node card = createProductCard(prod);
                productsVbox.getChildren().add(card);
            }
        }

        LOGGER.log(Level.INFO, "**ProductModifyWindow** Company selected: {0}", selectedCompany != null ? selectedCompany.getName() : "null");
    }

    private Node createProductCard(Product product) {
        LOGGER.log(Level.INFO, "**ProductModifyWindow** Creating card for product: {0}", product.getName());

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
                loadProductImage(product.getImage())
        );
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        imageView.setPreserveRatio(true);

        // Text container
        VBox textBox = new VBox(5);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        // This is to show ... when the label is to small and hover over it to show the full name
        nameLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
        nameLabel.setTooltip(new Tooltip(product.getName()));

        Label descLabel = new Label(product.getDescription());
        descLabel.setWrapText(true);
        descLabel.maxWidthProperty().bind(textBox.widthProperty());
        descLabel.setMaxHeight(48);
        // This is to show ... when the label is to small and hover over it to show the full description
        descLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
        descLabel.setTooltip(new Tooltip(product.getDescription()));

        textBox.getChildren().addAll(nameLabel, descLabel);

        // Price + button
        VBox rightBox = new VBox(8);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setMinWidth(120);
        HBox.setHgrow(rightBox, Priority.NEVER);

        Spinner<Double> priceSpinner = new Spinner<>();
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000.0, product.getPrice(), 1);
        priceSpinner.setValueFactory(valueFactory);
        priceSpinner.setEditable(true);
        priceSpinner.setMaxWidth(Region.USE_PREF_SIZE);
        priceSpinner.setPrefWidth(Region.USE_COMPUTED_SIZE);
        // Sets the size of the spinner to 5 characters to make space for other stuff in the card
        TextField editor = priceSpinner.getEditor();
        editor.setPrefColumnCount(5);

        priceSpinner.setStyle(
                "-fx-background-color: #e5e5e5;"
                + "-fx-padding: 4 8 4 8;"
                + "-fx-font-weight: bold;"
        );

        Button editButton = new Button("Save price");
        editButton.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-border-color: green;"
                + "-fx-text-fill: green;"
                + "-fx-border-radius: 6;"
        );

        rightBox.getChildren().addAll(priceSpinner, editButton);

        HBox.setHgrow(textBox, Priority.ALWAYS);

        card.setMinHeight(130);
        card.setPrefHeight(130);
        card.getChildren().addAll(imageView, textBox, rightBox);
        card.setOnMouseClicked(e -> selectProduct(product));
        editButton.setOnMouseClicked(e -> editProductPrice(product, priceSpinner.getValue()));

        LOGGER.info("**ProductModifyWindow** Card successfully created for product");
        return card;
    }

    private Button createSizeButton(Size size) {
        LOGGER.log(Level.INFO, "**ProductModifyWindow** Creating size button for size: {0}", size.getLabel());
        Button sizeButton = new Button(size.getLabel());
        // Show size on hover of the button if the label is to big and shows...
        sizeButton.setTextOverrun(OverrunStyle.ELLIPSIS);
        Tooltip tooltip = new Tooltip(size.getLabel());
        Tooltip.install(sizeButton, tooltip);

        double d = 40;

        sizeButton.setMinSize(d, d);
        sizeButton.setPrefSize(d, d);
        sizeButton.setMaxSize(d, d);

        sizeButton.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 100%;"
                + "-fx-border-radius: 100%;"
                + "-fx-border-color: green;"
                + "-fx-border-width: 1;"
                + "-fx-font-size: 11px;"
                + "-fx-padding: 0;"
        );

        sizeButton.setAlignment(Pos.CENTER);
        sizeButton.setOnAction(e -> selectSize(size));

        LOGGER.info("**ProductModifyWindow** Size button created successfully");
        return sizeButton;
    }

    private void selectSize(Size size) {
        LOGGER.log(Level.INFO, "**ProductModifyWindow** Selecting size: {0}", size.getLabel());
        this.selectedSize = size;
        List<Purchase> purchases = cont.findSizePurchases(size);

        linechart.setData(getPurchasesData(purchases));

        SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, size.getStock());
        stockCountSpinner.setValueFactory(factory);
        factory.setValue(size.getStock());
        stockCountSpinner.getEditor().setText(String.valueOf(factory.getValue()));
        sizeTextField.setText(size.getLabel());

        LOGGER.info("**ProductModifyWindow** Size selected successfully");
    }

    private void selectProduct(Product product) {
        LOGGER.log(Level.INFO, "**ProductModifyWindow** Selecting product: {0}", product.getName());
        resetData();

        selectedProduct = product;
        List<Purchase> purchases = cont.findProductPurchases(product);
        linechart.setData(getPurchasesData(purchases));

        List<Size> sizes = cont.findProductSizes(product);
        selectedProductSizes = sizes;
        sizesHbox.getChildren().clear();

        for (Size s : sizes) {
            Button btn = createSizeButton(s);
            sizesHbox.getChildren().add(btn);
        }

        Button addSizeButton = new Button("+");

        double d = 40;

        addSizeButton.setMinSize(d, d);
        addSizeButton.setPrefSize(d, d);
        addSizeButton.setMaxSize(d, d);

        addSizeButton.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 100%;"
                + "-fx-border-radius: 100%;"
                + "-fx-border-color: green;"
                + "-fx-border-width: 1;"
                + "-fx-font-size: 11px;"
                + "-fx-padding: 0;"
        );
        addSizeButton.setAlignment(Pos.CENTER);

        addSizeButton.setOnAction(e -> {
            selectedSize = null;
            sizeTextField.setText("");
            stockCountSpinner.getEditor().clear();
            SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1);
            stockCountSpinner.setValueFactory(factory);
            factory.setValue(1);
            stockCountSpinner.getEditor().setText("1");
        });

        sizesHbox.getChildren().add(addSizeButton);
        LOGGER.info("**ProductModifyWindow** Product selected successfully");
    }

    private void resetData() {
        LOGGER.info("**ProductModifyWindow** Resetting data...");
        linechart.getData().clear();
        sizesHbox.getChildren().clear();
        stockCountSpinner.getEditor().clear();
        sizeTextField.setText("");
        LOGGER.info("**ProductModifyWindow** Data reset complete");
    }

    @FXML
    private void logout(ActionEvent event) {
        LOGGER.info("**ProductModifyWindow** Switching to login window");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

            LOGGER.info("**ProductModifyWindow** Login window opened successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProductModifyWindow** error switching to login window", e);
        }
    }

    private ObservableList<XYChart.Series<Number, Number>> getPurchasesData(List<Purchase> purchases) {
        LOGGER.log(Level.INFO, "**ProductModifyWindow** Loading chart data for {0} purchases", purchases.size());

        ObservableList<XYChart.Series<Number, Number>> data = FXCollections.observableArrayList();
        HashMap<Size, ArrayList<Purchase>> sizes = new HashMap<>();

        for (Purchase p : purchases) {
            Size size = p.getSize();
            sizes.putIfAbsent(size, new ArrayList<>());
            sizes.get(size).add(p);
        }

        for (Map.Entry<Size, ArrayList<Purchase>> entry : sizes.entrySet()) {
            Size size = entry.getKey();
            ArrayList<Purchase> size_purchases = entry.getValue();
            size_purchases.sort(Comparator.comparing(Purchase::getTimeOfPurchase));

            LocalDate firstDate = purchases.get(0).getTimeOfPurchase();
            LocalDate lastDate = purchases.get(purchases.size() - 1).getTimeOfPurchase();

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(size.getProduct().getName() + "-" + size.getLabel());

            long totalDays = ChronoUnit.DAYS.between(firstDate, lastDate);
            for (long day = 0; day <= totalDays; day++) {
                LocalDate currentDate = firstDate.plusDays(day);
                int counter = 0;
                for (Purchase p : size_purchases) {
                    if (p.getTimeOfPurchase().equals(currentDate)) {
                        counter += 1;
                    }
                }
                series.getData().add(new XYChart.Data<>(day, counter));
            }
            data.add(series);
        }

        LOGGER.info("**ProductModifyWindow** Chart data loaded successfully");
        return data;
    }

    @FXML
    private void updateCreateSize(ActionEvent event) {
        LOGGER.info("**ProductModifyWindow** Updating or creating size...");

        if (selectedProduct == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Select a product");
            alert.setContentText("Must select a product before adding a size.");
            alert.setHeaderText("Must select a product");
            alert.showAndWait();

            LOGGER.info("**ProductModifyWindow** Tried to add a size without selecting a product");
            return;
        }

        int newStock = stockCountSpinner.getValue();
        String newLabel = sizeTextField.getText().trim();

        if (newLabel.equals("") || newStock <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid size data");
            alert.setContentText("Please insert valid data for the new size.");
            alert.setHeaderText("Insert valid data");
            alert.showAndWait();

            LOGGER.info("**ProductModifyWindow** Invalid size data provided");
        } else {
            if (selectedSize != null) {
                cont.modifySize(selectedSize, newLabel, newStock);
                LOGGER.log(Level.INFO, "**ProductModifyWindow** Size modified: {0} stock={1}",
                        new Object[]{newLabel, newStock});

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Size stock modification confirmation");
                alert.setContentText("Size information successfully modified!");
                alert.setHeaderText("Size stock");
                alert.showAndWait();

                resetData();
                selectProduct(selectedProduct);
                selectSize(selectedSize);
            } else if (selectedProduct != null) {
                for (Size size : selectedProductSizes) {
                    if (size.getLabel().equalsIgnoreCase(newLabel)) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Size already exists.");
                        alert.setContentText("The new size already exists.");
                        alert.setHeaderText("Duplicate size");
                        alert.showAndWait();
                        LOGGER.log(Level.INFO, "**ProductModifyWindow** Attempt to create a duplicate size");
                        return;
                    }
                }

                selectedSize = cont.createSize(newLabel, newStock, selectedProduct);
                LOGGER.log(Level.INFO, "**ProductModifyWindow** Size created: {0} stock={1}",
                        new Object[]{newLabel, newStock});

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Size has been created");
                alert.setContentText("The new size has been added to the product.");
                alert.setHeaderText("Size created");
                alert.showAndWait();

                resetData();
                selectProduct(selectedProduct);
                selectSize(selectedSize);
            }
        }
    }

    @FXML
    private void deleteProduct(ActionEvent event) {
        LOGGER.info("**ProductModifyWindow** Deleting product...");

        if (selectedProduct != null) {
            cont.deleteProduct(selectedProduct);
            LOGGER.log(Level.INFO, "**ProductModifyWindow** Product deleted: {0}", selectedProduct.getName());

            selectedProduct = null;
            selectedSize = null;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Product deletion confirmation");
            alert.setContentText("Product successfully deleted!");
            alert.setHeaderText("Product deletion");
            alert.showAndWait();

            selectCompany();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("Please select a product to delete.");
            alert.setHeaderText("No product selected");
            alert.showAndWait();

            LOGGER.info("**ProductModifyWindow** No product selected to delete");
        }
    }

    @FXML
    private void createItem(ActionEvent event) {
        LOGGER.info("**ProductModifyWindow** Switching to create item window");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProductCreationWindow.fxml"));
            Parent root = loader.load();

            ProductCreationWindowController viewController = loader.getController();
            viewController.initData(profile, cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

            LOGGER.info("**ProductCreationWindow** Create item window opened successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProductModifyWindow** error switching to create item window", e);
        }
    }

    @FXML
    private void deleteSize(ActionEvent event) {
        LOGGER.info("**ProductModifyWindow** Deleting size...");

        if (selectedSize != null) {
            cont.deleteSize(selectedSize);
            LOGGER.log(Level.INFO, "**ProductModifyWindow** Size deleted: {0}", selectedSize.getLabel());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Size deletion confirmation");
            alert.setContentText("The selected size has been deleted.");
            alert.setHeaderText("Size deleted.");
            alert.showAndWait();

            selectedSize = null;
            resetData();
            selectProduct(selectedProduct);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Select a size");
            alert.setContentText("No size has been selected to delete.");
            alert.setHeaderText("Select a size to delete.");
            alert.showAndWait();

            LOGGER.info("**ProductModifyWindow** No size selected to delete");
        }
    }

    private void editProductPrice(Product product, double newPrice) {
        LOGGER.log(Level.INFO, "**ProductModifyWindow** Editing price for product: {0} new price={1}",
                new Object[]{product.getName(), newPrice});

        if (product.getPrice() == newPrice) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Product price change warning");
            alert.setContentText("The products price is the same as before");
            alert.setHeaderText("Same price as before");
            alert.showAndWait();

            LOGGER.info("**ProductModifyWindow** Price unchanged for product");
        } else {
            product.setPrice(newPrice);
            cont.updateProduct(product);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Products price modification");
            alert.setContentText("The products price has been modified");
            alert.setHeaderText("Product price modified");
            alert.showAndWait();

            LOGGER.log(Level.INFO, "**ProductModifyWindow** Price updated: {0} -> {1}",
                    new Object[]{product.getName(), newPrice});
        }
    }

    public static Image loadProductImage(String path) {
        LOGGER.log(Level.INFO, "**ProductModifyWindow** Loading product image: {0}", path);

        if (path.startsWith("/")) {
            InputStream is = Product.class.getResourceAsStream(path);
            if (is == null) {
                LOGGER.log(Level.SEVERE, "**ProductModifyWindow** Image path not found: {0}", path);
            }
            return new Image(is);
        }

        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) {
            LOGGER.log(Level.SEVERE, "**ProductModifyWindow** Image file does not exist: {0}", path);
        }

        return new Image(filePath.toUri().toString());
    }

    @FXML
    private void openUserManual(ActionEvent event) {
        LOGGER.info("**ProductModifyWindow** Opening user manual");

        try {
            File pdf = new File("pdfs/User_Manual.pdf");
            if (!pdf.exists()) {
                LOGGER.info("**ProductModifyWindow** User manual file not found: pdfs/User_Manual.pdf");
                return;
            }

            Desktop.getDesktop().open(pdf);
            LOGGER.info("**ProductModifyWindow** User manual opened successfully");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "**ProductModifyWindow** error opening user manual", ex);
        }
    }
}
