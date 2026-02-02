/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import javafx.scene.control.ToggleGroup;

import exception.passwordequalspassword;

import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Admin;
import model.Profile;

/**
 * Controller for the SignUp window.
 * Handles user registration and navigation to login or main menu.
 */
public class SignUpWindowController implements Initializable {

    @FXML
    private TextField textFieldEmail, textFieldName, textFieldSurname, textFieldTelephone;
    @FXML
    private TextField textFieldCardN;
    @FXML
    private TextField textFieldPassword, textFieldUsername;
    @FXML
    private RadioButton rButtonM, rButtonW, rButtonO;
    @FXML
    private Button buttonSignUp, buttonLogIn;
    @FXML
    private Label errorLbl;

    private Controller cont;
    private ToggleGroup grupOp;
    
    private Profile profile;
    
    public void initData(Controller cont) {
        //Hacer que Items muestre Productos de la base de datos en la vista
        this.cont = cont;
        System.out.println("Controller: " + cont);
    }  

    /**
     * Navigates back to login window.
     */
    @FXML
    private void login() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = fxmlLoader.load();
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Close current window
            Stage currentStage = (Stage) buttonSignUp.getScene().getWindow();
            currentStage.close();
        } catch (IOException ex) {
            Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Signs up a new user and navigates to ProfileWindow if successful.
     */
    @FXML
    private void signup() throws passwordequalspassword {

        String email = textFieldEmail.getText();
        String name = textFieldName.getText();
        String surname = textFieldSurname.getText();
        String telephone = textFieldTelephone.getText();
        String cardN = textFieldCardN.getText();
        String pass = textFieldPassword.getText();
        String username = textFieldUsername.getText();
        String gender = null;

        if (rButtonM.isSelected()) gender = "Man";
        else if (rButtonW.isSelected()) gender = "Woman";
        else if (rButtonO.isSelected()) gender = "Other";

        // Limpiamos el label de error
        errorLbl.setText("");

        // 1. Campos obligatorios
        if (email.isEmpty() || name.isEmpty() || surname.isEmpty() || telephone.isEmpty() || cardN.isEmpty() || pass.isEmpty() || username.isEmpty()) {
            errorLbl.setText("All fields must be filled");
            return;
        }

        // 2. Género obligatorio
        if (gender == null) {
            errorLbl.setText("You must select a gender");
            return;
        }

        // 4. Validación de email completa
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
        if (!email.matches(emailRegex)) {
            errorLbl.setText("Invalid email format");
            return;
        }

        // 5. Teléfono: solo números y 9 dígitos
        if (!telephone.matches("\\d{9}")) {
            errorLbl.setText("Telephone must have exactly 9 digits");
            return;
        }

        // 6. Card number: solo números y 16 dígitos
        if (!cardN.matches("\\d{16}")) {
            errorLbl.setText("Card number must have exactly 16 digits");
            return;
        }

        // ---- SIGN UP ----
        if (cont.signUp(gender, cardN, username, pass, email, name, telephone, surname)) {

            profile = cont.logIn(username, pass);

            try {
                FXMLLoader fxmlLoader;

                if (profile instanceof Admin) {
                    fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProductModifyWindow.fxml"));
                    Parent root = fxmlLoader.load();

                    ProductModifyWindowController controllerWindow = fxmlLoader.getController();
                    controllerWindow.initData(profile, cont);

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    fxmlLoader = new FXMLLoader(getClass().getResource("/view/ShopWindow.fxml"));
                    Parent root = fxmlLoader.load();

                    ShopWindowController controllerWindow = fxmlLoader.getController();
                    controllerWindow.initData(profile, cont);

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                }

                Stage currentStage = (Stage) buttonSignUp.getScene().getWindow();
                currentStage.close();

            } catch (IOException ex) {
                Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
}
