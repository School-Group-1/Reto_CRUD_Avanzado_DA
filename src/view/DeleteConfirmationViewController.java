/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import model.User;

/**
 * FXML Controller class
 *
 * @author acer
 */
public class DeleteConfirmationViewController implements Initializable {
    
    @FXML
    private Label lblMessage;
    
    @FXML
    private Label lblError;

    @FXML
    private PasswordField passwordField;

    private Stage stage;
    private User user;
    private boolean confirmed = false;
    
    private String adminPassword;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    @FXML
    private void isconfirm() {
        if (passwordField.getText().equals(adminPassword)) {
            confirmed = true;
            stage.close();
        } else {
            lblError.setVisible(true);
            passwordField.clear();
        }
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void confirm() {
        confirmed = true;
        stage.close();
    }

    @FXML
    private void cancel() {
        stage.close();
    }
    
}
