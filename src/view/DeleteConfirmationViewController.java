/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import model.Company;
import model.User;
import model.Admin;
import model.Profile;
import controller.Controller;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class DeleteConfirmationViewController implements Initializable {

    private static final Logger LOGGER
            = Logger.getLogger(DeleteConfirmationViewController.class.getName());

    @FXML
    private Label lblMessage;
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button deleteButton;

    private Stage stage;
    private User userToDelete;
    private Company companyToDelete;
    private Admin admin;
    private boolean confirmed = false;
    private String adminUsername;
    private String enteredPassword;
    private Controller cont;
    private Stage parentStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("**DeleteConfirmationView** Inicializando controlador de confirmación de borrado");
    }

    public void initData(Profile profile, Controller cont) {
        LOGGER.info("**DeleteConfirmationView** initData llamado");
        this.cont = cont;

        if (profile instanceof Admin) {
            this.admin = (Admin) profile;
            this.adminUsername = admin.getUsername();
            LOGGER.log(Level.INFO, "**DeleteConfirmationView** Admin actual: {0}", adminUsername);
        } else if (profile instanceof User) {
            LOGGER.log(Level.INFO, "**DeleteConfirmationView** Usuario actual: {0}", ((User) profile).getUsername());
        }
    }

    public void setUserToDelete(User user) {
        this.userToDelete = user;
        if (user != null) {
            LOGGER.log(Level.INFO, "**DeleteConfirmationView** Usuario a eliminar: {0}", user.getUsername());
        }
    }

    public void setCompanyToDelete(Company company) {
        this.companyToDelete = company;
        if (company != null) {
            LOGGER.log(Level.INFO, "**DeleteConfirmationView** Empresa a eliminar: {0}", company.getName());
        }
    }

    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
        LOGGER.info("**DeleteConfirmationView** Parent stage recibido");
    }

    private void confirm() {
        enteredPassword = passwordField.getText();
        LOGGER.log(Level.INFO, "**DeleteConfirmationView** confirm llamado. Password introducida (longitud): {0}",
                enteredPassword != null ? enteredPassword.length() : 0);

        if (admin != null && admin.getPassword().equals(enteredPassword)) {
            confirmed = true;
            LOGGER.info("**DeleteConfirmationView** Confirmación correcta como admin");
        }

        closeWindow();
    }

    @FXML
    private void cancel() {
        LOGGER.info("**DeleteConfirmationView** Cancelando borrado, cerrando ventana");
        closeWindow();
    }

    private void closeWindow() {
        if (stage != null) {
            LOGGER.info("**DeleteConfirmationView** Cerrando ventana usando stage inyectado");
            stage.close();
        } else if (passwordField.getScene() != null) {
            LOGGER.info("**DeleteConfirmationView** Cerrando ventana usando escena del passwordField");
            ((Stage) passwordField.getScene().getWindow()).close();
        } else {
            LOGGER.warning("**DeleteConfirmationView** No se pudo cerrar la ventana: stage y scene son null");
        }
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public String getEnteredPassword() {
        return enteredPassword;
    }

    public User getUserToDelete() {
        return userToDelete;
    }

    public Company getCompanyToDelete() {
        return companyToDelete;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        LOGGER.info("**DeleteConfirmationView** Stage asignado al controlador");
    }

    @FXML
    private void delete(ActionEvent event) {
        enteredPassword = passwordField.getText();
        LOGGER.log(Level.INFO, "**DeleteConfirmationView** delete llamado. Password introducida (longitud): {0}",
                enteredPassword != null ? enteredPassword.length() : 0);

        // 1) Usuario borrándose a sí mismo (no es admin)
        if (admin == null && userToDelete != null && userToDelete.getPassword().equals(enteredPassword)) {
            LOGGER.log(Level.INFO, "**DeleteConfirmationView** Usuario se elimina a sí mismo: {0}",
                    userToDelete.getUsername());
            try {
                cont.dropOutUser(userToDelete.getUsername(), userToDelete.getPassword());
                LOGGER.info("**DeleteConfirmationView** Usuario eliminado en BD correctamente");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("LogInWindow");
                stage.show();

                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
                if (parentStage != null) {
                    LOGGER.info("**DeleteConfirmationView** Cerrando ventana padre");
                    parentStage.close();
                }
                LOGGER.info("**DeleteConfirmationView** Ventana actual cerrada tras eliminar usuario");
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "**DeleteConfirmationView** Error al eliminar usuario o abrir LogInWindow", ex);
            }

            // 2) Admin borrando a un usuario
        } else if (admin instanceof Admin
                && userToDelete != null
                && admin.getPassword().equals(enteredPassword)) {

            LOGGER.log(Level.INFO, "**DeleteConfirmationView** Admin {0} elimina usuario: {1}",
                    new Object[]{admin.getUsername(), userToDelete.getUsername()});

            try {
                cont.dropOutUser(userToDelete.getUsername(), userToDelete.getPassword());
                LOGGER.info("**DeleteConfirmationView** Usuario eliminado en BD correctamente por admin");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("LogInWindow");
                stage.show();

                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
                if (parentStage != null) {
                    LOGGER.info("**DeleteConfirmationView** Cerrando ventana padre");
                    parentStage.close();
                }
                LOGGER.info("**DeleteConfirmationView** Ventana actual cerrada tras eliminar usuario (admin)");
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "**DeleteConfirmationView** Error al eliminar usuario o abrir LogInWindow (admin)", ex);
            }

        } else {
            LOGGER.warning("**DeleteConfirmationView** Password incorrecta o datos insuficientes para eliminar usuario");
        }
    }
}
