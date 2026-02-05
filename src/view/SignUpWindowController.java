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
 * Controller class for the Sign Up window.
 * 
 * Handles user registration, validation of input fields, and navigation to the login window or main menu depending on the profile type (Admin or User).
 */
public class SignUpWindowController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(SignUpWindowController.class.getName());

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
    public Label errorLbl;
    
    private static final String ERROR_STYLE =
        "-fx-border-color: red; -fx-border-width: 2; -fx-background-radius: 50; -fx-border-radius: 50;";

    private static final String NORMAL_STYLE =
        "-fx-border-color: black; -fx-background-radius: 50; -fx-border-radius: 50;";


    private Controller cont;
    private ToggleGroup grupOp;
    private Profile profile;

    /**
     * Initializes the controller with the main application controller.
     * This method should be called after the FXML is loaded.
     *
     * @param cont the main application controller
     */
    public void initData(Controller cont) {
        this.cont = cont;
        LOGGER.info("**SignUpWindow** Initialized with controller: " + cont);
    }

    /**
     * Navigates back to the login window and closes the current Sign Up window.
     */
    @FXML
    private void login() {
        LOGGER.info("**SignUpWindow** User clicked Log In");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            ((Stage) buttonSignUp.getScene().getWindow()).close();

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "**SignUpWindow** Error opening LogIn window", ex);
        }
    }

    /**
     * Handles the user registration process.
     * Validates input fields, creates a new user profile, and navigates to the appropriate next window depending on the profile type.
     *
     * @throws passwordequalspassword if password validation fails
     */
    @FXML
    private void signup() throws passwordequalspassword {

        LOGGER.info("**SignUpWindow** SignUp process started");
        
        clearError(textFieldEmail);
        clearError(textFieldName);
        clearError(textFieldSurname);
        clearError(textFieldTelephone);
        clearError(textFieldCardN);
        clearError(textFieldPassword);
        clearError(textFieldUsername);


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

        errorLbl.setText("");
        
        boolean hasError = false;

        if (email.isEmpty() || name.isEmpty() || surname.isEmpty() || telephone.isEmpty() || cardN.isEmpty() || pass.isEmpty() || username.isEmpty()) {
            errorLbl.setText("All fields must be filled");
            LOGGER.warning("**SignUpWindow** Validation failed: empty fields");
            hasError=true;
        }

        if (gender == null) {
            errorLbl.setText("You must select a gender");
            LOGGER.warning("**SignUpWindow** Validation failed: gender not selected");
            hasError=true;
        }

        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
        if (!email.matches(emailRegex)) {
            errorLbl.setText("Invalid email format");
            LOGGER.warning("**SignUpWindow** Invalid email format: " + email);
            markError(textFieldEmail);
            hasError=true;
        }

        if (!telephone.matches("\\d{9}")) {
            errorLbl.setText("Telephone must have exactly 9 digits");
            LOGGER.warning("**SignUpWindow** Invalid telephone number: " + telephone);
            markError(textFieldTelephone);
            hasError=true;
        }

        if (!cardN.matches("\\d{16}")) {
            errorLbl.setText("Card number must have exactly 16 digits");
            LOGGER.warning("**SignUpWindow** Invalid card number length");
            markError(textFieldCardN);
            hasError=true;
        }
        
        if (hasError) return;

        LOGGER.info("**SignUpWindow** Attempting sign up for username: " + username);

        try {
            if (cont.signUp(gender, cardN, username, pass, email, name, telephone, surname)) {

                LOGGER.info("**SignUpWindow** SignUp successful for user: " + username);

                profile = cont.logIn(username, pass);

                FXMLLoader fxmlLoader;

                if (profile instanceof Admin) {
                    LOGGER.info("**SignUpWindow** User is Admin, opening ProductModifyWindow");
                    fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProductModifyWindow.fxml"));
                    Parent root = fxmlLoader.load();
                    ProductModifyWindowController controllerWindow = fxmlLoader.getController();
                    controllerWindow.initData(profile, cont);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    LOGGER.info("**SignUpWindow** User is regular profile, opening ShopWindow");
                    fxmlLoader = new FXMLLoader(getClass().getResource("/view/ShopWindow.fxml"));
                    Parent root = fxmlLoader.load();
                    ShopWindowController controllerWindow = fxmlLoader.getController();
                    controllerWindow.initData(profile, cont);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                }
                ((Stage) buttonSignUp.getScene().getWindow()).close();
            } else {
                LOGGER.warning("**SignUpWindow** SignUp failed for username: " + username);
            }

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "**SignUpWindow** Error during sign up navigation", ex);
        }
    }
    
    /**
     * Method to make the textfield border red, sign of error
     * @param field where is the error
     */
    private void markError(TextField field) {
        field.setStyle(ERROR_STYLE);
    }

    /**
     * Method to clear the textfield border
     * @param field that must be cleared
     */
    private void clearError(TextField field) {
        field.setStyle(NORMAL_STYLE);
    }


    /**
     * Initializes the controller class after the FXML file has been loaded.
     *
     * @param url the location used to resolve relative paths, or null if unknown
     * @param rb the resource bundle used for localization, or null if not used
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("**SignUpWindow** Controller initialized");
    }
}
