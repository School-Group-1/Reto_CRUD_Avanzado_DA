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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    private Size selectedSize = null;
    private Profile profile;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    //method to receive profile, controller and data
    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;

        // Add companies to the combobox
        List<Company> companies = cont.findAllCompanies();

        for (Company comp : companies) {
            companyCombobox.getItems().add(comp.getName());
        }

        // Prepare the line chart (https://youtu.be/HWfZPiPu1sI?si=KFfRQ06_IlluRXsj good luck)
        xAxis.setLabel("Days");
        // Automatically sets the ranges aka size of the x axis
        xAxis.setAutoRanging(true);

        yAxis.setLabel("Sales");
        // Automatically sets the ranges aka size of the x axis
        yAxis.setAutoRanging(true);

        linechart.setTitle("Product Sales");
    }

    @FXML
    private void goToCompanies(ActionEvent event) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToUsers(ActionEvent event) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void selectCompany() {
        // Resets linechart, sizes, and stock count to be empty
        // This is becuase no product is selected when a company is first selected
        resetData();

        List<Company> companies = cont.findAllCompanies();
        List<Product> products = null;
        List<Purchase> purchases = null;
        String companyName = companyCombobox.getValue();
        productsVbox.getChildren().clear();

        for (Company comp : companies) {
            if (comp.getName().equals(companyName)) {
                selectedCompany = comp;
            }
        }

        if (selectedCompany != null) {
            products = cont.findProductsByCompany(selectedCompany);

            for (Product prod : products) {
                Node card = createProductCard(prod);
                productsVbox.getChildren().add(card);
            }
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
                loadProductImage(product.getImage())
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

        Spinner<Double> priceSpinner = new Spinner<>();
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000.0, product.getPrice(), 1);
        priceSpinner.setValueFactory(valueFactory);
        priceSpinner.setEditable(true);

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

        card.getChildren().addAll(imageView, textBox, rightBox);
        card.setOnMouseClicked(e -> selectProduct(product));
        editButton.setOnMouseClicked(e -> editProductPrice(product, priceSpinner.getValue()));

        return card;
    }

    private Button createSizeButton(Size size) {
        Button sizeButton = new Button(size.getLabel());

        sizeButton.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 100%;"
                + "-fx-border-radius: 100%;"
                + "-fx-border-color: green;"
                + "-fx-border-width: 1;"
        );

        // Make the button perfectly circular
        sizeButton.setMaxHeight(Double.MAX_VALUE);
        sizeButton.maxWidthProperty().bind(sizeButton.maxHeightProperty());

        // Center the label
        sizeButton.setAlignment(Pos.CENTER);

        // Optional click event
        sizeButton.setOnAction(e -> selectSize(size));

        return sizeButton;
    }

    private void selectSize(Size size) {
        this.selectedSize = size;
        List<Purchase> purchases = cont.findSizePurchases(size);

        linechart.setData(getPurchasesData(purchases));

        SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, size.getStock());
        stockCountSpinner.setValueFactory(factory);

        factory.setValue(size.getStock());
        stockCountSpinner.getEditor().setText(
                String.valueOf(factory.getValue())
        );

        sizeTextField.setText(size.getLabel());
    }

    private void selectProduct(Product product) {
        // Resets data from the previous selected product
        resetData();

        this.selectedProduct = product;

        List<Purchase> purchases = cont.findProductPurchases(product);
        linechart.setData(getPurchasesData(purchases));

        // Add the available sizes
        List<Size> sizes = cont.findProductSizes(product);
        sizesHbox.getChildren().clear();

        for (Size s : sizes) {
            Button btn = createSizeButton(s);
            sizesHbox.getChildren().add(btn);
        }

        // Makes a button to allow the user to add a size and adds it to the end of the list
        Button addSizeButton = new Button("+");

        addSizeButton.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 100%;"
                + "-fx-border-radius: 100%;"
                + "-fx-border-color: green;"
                + "-fx-border-width: 1;"
        );

        addSizeButton.setMaxHeight(Double.MAX_VALUE);
        addSizeButton.maxWidthProperty().bind(addSizeButton.maxHeightProperty());

        addSizeButton.setAlignment(Pos.CENTER);

        addSizeButton.setOnAction(e -> {
            selectedSize = null;

            // Reset the fields to let the user fill them in and add a size
            sizeTextField.setText("");
            stockCountSpinner.getEditor().clear();

            SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1);
            stockCountSpinner.setValueFactory(factory);

            factory.setValue(1);
            stockCountSpinner.getEditor().setText(
                    String.valueOf(factory.getValue())
            );
        });

        sizesHbox.getChildren().add(addSizeButton);
    }

    private void resetData() {
        linechart.getData().clear();
        sizesHbox.getChildren().clear();
        stockCountSpinner.getEditor().clear();
        sizeTextField.setText("");
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
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

    private ObservableList<XYChart.Series<Number, Number>> getPurchasesData(List<Purchase> purchases) {
        ObservableList<XYChart.Series<Number, Number>> data = FXCollections.observableArrayList();
        HashMap<Size, ArrayList<Purchase>> sizes = new HashMap<>();

        // Gets all of the sizes purchased
        for (Purchase p : purchases) {
            Size size = p.getSize();
            sizes.putIfAbsent(size, new ArrayList<>());
            sizes.get(size).add(p);
        }

        // Maps points, for every product the first date of purchase will always be the first point
        // The last date will be the last point
        // Any days in between the first and last will include the amount purchased, wether its above 0 or not
        for (Map.Entry<Size, ArrayList<Purchase>> entry : sizes.entrySet()) {
            Size size = entry.getKey();
            ArrayList<Purchase> size_purchases = entry.getValue();

            // Sorts by date of purchase
            size_purchases.sort(Comparator.comparing(Purchase::getTimeOfPurchase));

            LocalDate firstDate = purchases.get(0).getTimeOfPurchase();
            LocalDate lastDate = purchases.get(purchases.size() - 1).getTimeOfPurchase();

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(size.getProduct().getName() + "-" + size.getLabel());

            // Adds all the days between the first and last purchase
            long totalDays = ChronoUnit.DAYS.between(firstDate, lastDate);

            for (long day = 0; day <= totalDays; day++) {
                // Gets the current date in the loop
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

        return data;
    }

    @FXML
    private void updateCreateSize(ActionEvent event) {
        int newStock = stockCountSpinner.getValue();
        String newLabel = sizeTextField.getText().trim();

        if (newLabel.equals("") || newStock <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid size data");
            alert.setContentText("Please insert valid data for the new size.");
            alert.setHeaderText("Insert valid data");
            alert.showAndWait();
        } else {
            //Updates size one is selected, creates it if not
            if (selectedSize != null) {
                cont.modifySize(selectedSize, newLabel, newStock);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Size stock modification confirmation");
                alert.setContentText("Size information successfully modified!");
                alert.setHeaderText("Size stock");
                alert.showAndWait();
            } else if (selectedProduct != null) {
                selectedSize = cont.createSize(newLabel, newStock, selectedProduct);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Size has been created");
                alert.setContentText("The new size has been added to the product.");
                alert.setHeaderText("Select a size");
                alert.showAndWait();

                // Refreshes data to update the size list
                resetData();
                selectProduct(selectedProduct);
                selectSize(selectedSize);
            }
        }
    }

    @FXML
    private void deleteProduct(ActionEvent event) {
        if (selectedProduct != null) {
            cont.deleteProduct(selectedProduct);

            selectedProduct = null;
            selectedSize = null;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Product deletion confirmation");
            alert.setContentText("Product successfully deleted!");
            alert.setHeaderText("Product deletion");
            alert.showAndWait();

            // Refreshes the product list and resets linechart, size list, etc.
            selectCompany();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("Please select a product to delete.");
            alert.setHeaderText("No product selected");
            alert.showAndWait();
        }
    }

    @FXML
    private void createItem(ActionEvent event) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteSize(ActionEvent event) {
        if (selectedSize != null) {
            cont.deleteSize(selectedSize);

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
        }
    }

    private void editProductPrice(Product product, double newPrice) {
        if (product.getPrice() == newPrice) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Product price change warning");
            alert.setContentText("The products price is the same as before");
            alert.setHeaderText("Same price as before");
            alert.showAndWait();
        } else {
            product.setPrice(newPrice);
            cont.updateProduct(product);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Products price modification");
            alert.setContentText("The products price has been modified");
            alert.setHeaderText("Product price modified");
            alert.showAndWait();
        }
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
    private void openUserManual(ActionEvent event) {
        try {
            File pdf = new File("pdfs/User_Manual.pdf");
            if (!pdf.exists()) {
                System.out.println("No exist el PDF.");
                return;
            }

            Desktop.getDesktop().open(pdf);
        } catch (IOException ex) {
            Logger.getLogger(ProductModifyWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
