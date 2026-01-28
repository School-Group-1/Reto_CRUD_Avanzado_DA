/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Company;
import model.DBImplementation;
import model.Product;

/**
 * FXML Controller class
 *
 * @author Mosi
 */
public class ProductModifyWindowController implements Initializable {

    private Controller cont = new Controller(new DBImplementation());
    @FXML
    private ComboBox<String> companyCombobox;
    @FXML
    private VBox productsVbox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Add companies to the combobox
        List<Company> companies = cont.findAllCompanies();
        System.out.println(companies);

        for (Company comp : companies) {
            companyCombobox.getItems().add(comp.getName());
        }
    }

    @FXML
    private void selectCompany() {
        Company company = null;
        List<Company> companies = cont.findAllCompanies();
        List<Product> products = null;
        String companyName = companyCombobox.getValue();
        productsVbox.getChildren().clear();

        for (Company comp : companies) {
            if (comp.getName().equals(companyName)) {
                company = comp;
            }
        }

        if (company != null) {
            products = cont.findProductsByCompany(company);

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
        card.setOnMouseClicked(e -> selectProduct(product));
        return card;
    }
    
    private void selectProduct(Product product) {
        System.out.println(product.toString());
    }
    
    /*@FXML
    private void goToCompanies(ActionEvent event) {
        changeWindow("/view/ShopWindow.fxml", event);
    }*/
    
    @FXML
    private void goToUsers(ActionEvent event) {
        changeWindow("/view/UserTable.fxml", event);
    }
    
    /*@FXML
    private void goToProfile(ActionEvent event) {
        changeWindow("/view/ModifyUserAdmin.fxml", event);
    }*/
    
    private void changeWindow(String fxml, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
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
}
