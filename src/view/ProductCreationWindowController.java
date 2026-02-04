/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Company;
import model.DBImplementation;
import model.Product;
import model.Profile;

/**
 * FXML Controller class
 *
 * @author Mosi
 */
public class ProductCreationWindowController implements Initializable {

    private static final Logger LOGGER =
            Logger.getLogger(ProductCreationWindowController.class.getName());

    private Controller cont = new Controller(new DBImplementation());
    private String selectedFilePath = null;
    private ArrayList<String> sizes = new ArrayList<>();
    private Profile profile;

    @FXML
    private Button insertImageButton;
    @FXML
    private Spinner<Integer> stockSpinner;
    @FXML
    private Spinner<Double> priceSpinner;
    @FXML
    private TextField categoryTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<String> companyComboBox;
    @FXML
    private TextArea descriptionTextField;
    @FXML
    private TextField sizeTextField;
    @FXML
    private FlowPane sizesFlowPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("**ProductCreationWindow** Initializing Product Creation Window Controller");
    }

    public void initData(Profile profile, Controller cont) {
        LOGGER.info("**ProductCreationWindow** initData called");
        this.profile = profile;
        this.cont = cont;

        // Add companies to the combobox
        List<Company> companies = cont.findAllCompanies();
        for (Company comp : companies) {
            companyComboBox.getItems().add(comp.getName());
        }
        LOGGER.log(Level.INFO, "**ProductCreationWindow** Loaded {0} companies into combo box", companies.size());

        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000.0, 19.99, 1);
        priceSpinner.setValueFactory(valueFactory);

        SpinnerValueFactory<Integer> factory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1);
        stockSpinner.setValueFactory(factory);

        LOGGER.info("**ProductCreationWindow** Finished initializing spinners");
    }

    @FXML
    private void goToCompanies(ActionEvent event) {
        LOGGER.info("**ProductCreationWindow** Switching to company window");
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

            LOGGER.info("**ProductCreationWindow** Company window opened");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProductCreationWindow** error switching to company window", e);
        }
    }

    @FXML
    private void goToUsers(ActionEvent event) {
        LOGGER.info("**ProductCreationWindow** Switching to users window");
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

            LOGGER.info("**ProductCreationWindow** Users window opened");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProductCreationWindow** error switching to users window", e);
        }
    }

    @FXML
    private void goToProducts(ActionEvent event) {
        LOGGER.info("**ProductCreationWindow** Switching to products window");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProductModifyWindow.fxml"));
            Parent root = loader.load();

            ProductModifyWindowController viewController = loader.getController();
            viewController.initData(profile, cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

            LOGGER.info("**ProductCreationWindow** Products window opened");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProductCreationWindow** error switching to products window", e);
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        LOGGER.info("**ProductCreationWindow** Logging out, switching to login window");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

            LOGGER.info("**ProductCreationWindow** Logged out and login window opened");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**ProductCreationWindow** error switching to login window", e);
        }
    }

    // Open a file selection screen to choose the image
    @FXML
    private void insertImage(ActionEvent event) {
        LOGGER.info("**ProductCreationWindow** insertImage called");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");

        // This filters for only image files to avoid errors
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image files (*.png, *.jpg, *.jpeg, *.gif)",
                        "*.png", "*.jpg", "*.jpeg", "*.gif"));

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile == null) {
            LOGGER.info("**ProductCreationWindow** No image selected");
            return;
        }

        selectedFilePath = selectedFile.getAbsolutePath();
        LOGGER.log(Level.INFO, "**ProductCreationWindow** Image selected: {0}", selectedFilePath);

        Image image = new Image(selectedFile.toURI().toString(), false);
        ImageView imageView = new ImageView(image);

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);

        insertImageButton.setGraphic(imageView);
        insertImageButton.setText("");

        LOGGER.info("**ProductCreationWindow** Image preview set on button");
    }

    @FXML
    private void createProduct(ActionEvent event) {
        LOGGER.info("**ProductCreationWindow** createProduct called");

        if (sizes.size() <= 0) {
            LOGGER.info("**ProductCreationWindow** No sizes provided for product");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid size data");
            alert.setContentText("Please insert at least one size.");
            alert.setHeaderText("Insert valid data");
            alert.showAndWait();
            return;
        }

        if (selectedFilePath == null) {
            LOGGER.info("**ProductCreationWindow** No image selected for product");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid image data");
            alert.setContentText("Please upload an image.");
            alert.setHeaderText("Insert valid data");
            alert.showAndWait();
            return;
        }

        String companyName = companyComboBox.getValue();
        if (companyName == null || companyName.equals("")) {
            LOGGER.info("**ProductCreationWindow** No company selected");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid company data");
            alert.setContentText("Please select a company.");
            alert.setHeaderText("Insert valid data");
            alert.showAndWait();
            return;
        }

        String description = descriptionTextField.getText().trim();
        String category = categoryTextField.getText().trim();
        String name = nameTextField.getText().trim();

        if (description.equals("") || category.equals("") || name.equals("")) {
            LOGGER.info("**ProductCreationWindow** Missing product fields (description/category/name)");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid product data");
            alert.setContentText("Please fill out all data for the product.");
            alert.setHeaderText("Insert valid data");
            alert.showAndWait();
            return;
        }

        Double price = priceSpinner.getValue();
        int initialStock = stockSpinner.getValue();
        Company company = cont.findCompanyByName(companyName);

        LOGGER.log(Level.INFO,
                "**ProductCreationWindow** Creating product: name={0}, category={1}, company={2}, price={3}, stock={4}",
                new Object[]{name, category, companyName, price, initialStock});

        Product newProduct = new Product(name, price, category, description, selectedFilePath, company);
        cont.saveProductSizes(newProduct, sizes, initialStock);

        LOGGER.log(Level.INFO,
                "**ProductCreationWindow** Product created and saved with {0} sizes",
                sizes.size());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product save confirmation");
        alert.setContentText("Product has been saved");
        alert.setHeaderText("Product saved");
        alert.showAndWait();

        goToProducts(event);
    }

    @FXML
    private void addSize(ActionEvent event) {
        LOGGER.info("**ProductCreationWindow** addSize called");
        String label = sizeTextField.getText().trim();

        if (!label.equals("") && !sizes.contains(label)) {
            sizes.add(label);
            LOGGER.log(Level.INFO, "**ProductCreationWindow** Size added: {0}", label);

            Button btn = createSizeButton(label);
            sizesFlowPane.getChildren().add(btn);

            sizeTextField.setText("");
        } else {
            LOGGER.info("**ProductCreationWindow** Invalid or duplicate size label");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid size data");
            alert.setContentText("Please insert valid data for the new size.");
            alert.setHeaderText("Insert valid data");
            alert.showAndWait();
        }
    }

    private Button createSizeButton(String size) {
        LOGGER.log(Level.INFO, "**ProductCreationWindow** Creating size button for size: {0}", size);

        Button sizeButton = new Button(size);

        sizeButton.setStyle(
                "-fx-background-color: white;"
                        + "-fx-background-radius: 100%;"
                        + "-fx-border-radius: 100%;"
                        + "-fx-border-color: green;"
                        + "-fx-border-width: 1;"
        );

        sizeButton.setMinHeight(100);
        sizeButton.setPrefHeight(100);
        sizeButton.setMaxHeight(100);

        sizeButton.setMinWidth(100);
        sizeButton.setPrefWidth(100);
        sizeButton.setMaxWidth(100);

        // Center the label
        sizeButton.setAlignment(Pos.CENTER);

        return sizeButton;
    }
}
