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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Company;
import model.DBImplementation;
import model.Profile;

/**
 * FXML Controller class
 *
 * @author Mosi
 */
public class ProductCreationWindowController implements Initializable {
    private Controller cont = new Controller(new DBImplementation());
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
    private TextField nameTextField1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;
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
    private void goToProducts(ActionEvent event) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @FXML
    private void insertImage(ActionEvent event) {
    }

    @FXML
    private void createProduct(ActionEvent event) {
    }
    
}
