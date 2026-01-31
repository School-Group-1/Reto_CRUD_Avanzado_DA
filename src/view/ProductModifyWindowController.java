/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
    
    private Profile profile;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    //method to receive profile, controller and data
    public void initData(Profile profile, Controller cont){
        this.profile=profile;
        this.cont=cont;
        
        System.out.println("Perfil: " + profile);
        System.out.println("Controller: " + cont);
        
        // Add companies to the combobox
        List<Company> companies = cont.findAllCompanies();
        System.out.println(companies);

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
            
            view.CompaniesTableController viewController = loader.getController();
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
            
            view.UserTableController viewController = loader.getController();
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
        Company company = null;
        List<Company> companies = cont.findAllCompanies();
        List<Product> products = null;
        List<Purchase> purchases = null;
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

        Label priceLabel = new Label(product.getPrice() + "€");
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

    private Button createSizeButton(Size size) {
        Button sizeButton = new Button(size.getLabel());

        sizeButton.setStyle(
                "-fx-background-color: #e0e0e0;"
                + "-fx-background-radius: 50%;"
                + "-fx-border-radius: 50%;"
                + "-fx-border-color: #b0b0b0;"
                + "-fx-border-width: 1;"
        );

        // Make the button perfectly circular
        sizeButton.setMinSize(50, 50);
        sizeButton.setPrefSize(50, 50);
        sizeButton.setMaxSize(50, 50);

        // Center the label
        sizeButton.setAlignment(Pos.CENTER);

        // Optional click event
        sizeButton.setOnAction(e -> selectSize(size));

        return sizeButton;
    }
     
    private void selectSize(Size size) {
        List<Purchase> purchases = cont.findSizePurchases(size);
        
        linechart.setData(getPurchasesData(purchases));
    }

    private void selectProduct(Product product) {
        List<Purchase> purchases = cont.findProductPurchases(product);
        linechart.setData(getPurchasesData(purchases));
        
        // Add the available sizes
        List<Size> sizes = cont.findProductSizes(product);
        sizesHbox.getChildren().clear();

        for(Size s:sizes) {
            Button btn = createSizeButton(s);
            sizesHbox.getChildren().add(btn);
        }
    }
    
    /*@FXML
    private void goToCompanies(ActionEvent event) {
        changeWindow("/view/ShopWindow.fxml", event);
    }*/
    @FXML
    private void goToCompanies(ActionEvent event) {
        changeWindow("/view/CompaniesTable.fxml", event);
    }
    @FXML
    private void goToUsers(ActionEvent event) {
        changeWindow("/view/UserTable.fxml", event);
    }
    @FXML
    private void goToLogin(ActionEvent event) {
        changeWindow("/view/LogInWindow.fxml", event);
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

    private ObservableList<XYChart.Series<Number, Number>> getPurchasesData(List<Purchase> purchases) {
        ObservableList<XYChart.Series<Number, Number>> data = FXCollections.observableArrayList();
        HashMap<Size, ArrayList<Purchase>> sizes = new HashMap<>();
        
        // Gets all of the sizes purchased
        for(Purchase p:purchases) {
            Size size = p.getSize();
            sizes.putIfAbsent(size, new ArrayList<>());
            sizes.get(size).add(p);
        }
        
        // Maps points, for every product the first date of purchase will always be the first point
        // The last date will be the last point
        // Any days in between the first and last will include the amount purchased, wether its above 0 or not
        for(Map.Entry<Size, ArrayList<Purchase>> entry : sizes.entrySet()) {
            Size size = entry.getKey();
            ArrayList<Purchase> size_purchases = entry.getValue();
            
            // Sorts by date of purchase
            size_purchases.sort(Comparator.comparing(Purchase::getTimeOfPurchase));
            
            LocalDate firstDate = purchases.get(0).getTimeOfPurchase();
            LocalDate lastDate = purchases.get(purchases.size()-1).getTimeOfPurchase();
            
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(size.getProduct().getName()+ "-" +size.getLabel());
            
            // Adds all the days between the first and last purchase
            long totalDays = ChronoUnit.DAYS.between(firstDate, lastDate);
            
            for (long day = 0; day <= totalDays; day++) {
                // Gets the current date in the loop
                LocalDate currentDate = firstDate.plusDays(day);
                
                int counter = 0;
                for(Purchase p:size_purchases) {
                    if(p.getTimeOfPurchase().equals(currentDate)) {
                        counter += 1;
                    }
                }
                
                series.getData().add(new XYChart.Data<>(day, counter));
            }
            
            data.add(series);
        }
        
        return data;
    }
}
