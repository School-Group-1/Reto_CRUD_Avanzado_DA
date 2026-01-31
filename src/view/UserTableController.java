/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserTableController implements Initializable {

    @FXML
    private CheckBox editCheckBox;
    @FXML
    private TextField tfUser;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfSurname;
    @FXML
    private TextField tfTel;
    @FXML
    private TableView<User> tableView;
    @FXML
    private TableColumn<User, String> emailCol;
    @FXML
    private TableColumn<User, String> nameCol;
    @FXML
    private TableColumn<User, String> passwordCol;
    @FXML
    private TableColumn<User, String> usernameCol;
    @FXML
    private TableColumn<User, String> genderCol;
    @FXML
    private TableColumn<User, String> surnameCol;
    @FXML
    private TableColumn<User, String> telephoneCol;
    @FXML
    private TableColumn<User, Void> deleteCol;

    private Profile profile;
    private Controller cont;
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private DBImplementation dao = new DBImplementation();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("=== INICIALIZANDO UserTableController ===");

        // Debug: verificar qué variables son null
        checkVariables();

        // Configurar solo si tableView existe
        if (tableView != null) {
            setupColumns();
            setupEditableColumns();
            setupDeleteColumn();
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            loadUsers();
            tableView.setItems(userList);
            setupEditMode();
        }
    }

    private void checkVariables() {
        System.out.println("tableView: " + (tableView != null ? "OK" : "NULL"));
        System.out.println("usernameCol: " + (usernameCol != null ? "OK" : "NULL"));
        System.out.println("emailCol: " + (emailCol != null ? "OK" : "NULL"));
        System.out.println("nameCol: " + (nameCol != null ? "OK" : "NULL"));
        System.out.println("surnameCol: " + (surnameCol != null ? "OK" : "NULL"));
        System.out.println("telephoneCol: " + (telephoneCol != null ? "OK" : "NULL"));
        System.out.println("genderCol: " + (genderCol != null ? "OK" : "NULL"));
        System.out.println("passwordCol: " + (passwordCol != null ? "OK" : "NULL"));
        System.out.println("deleteCol: " + (deleteCol != null ? "OK" : "NULL"));
        System.out.println("editCheckBox: " + (editCheckBox != null ? "OK" : "NULL"));
    }

    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;

        if (!(profile instanceof Admin)) {
            System.out.println("Acceso denegado. Solo administradores.");
            return;
        }

        System.out.println("Admin: " + ((Admin) profile).getUsername());
        loadUsers();
    }

    private void setupColumns() {
        if (usernameCol != null) {
            usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        }
        if (emailCol != null) {
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        }
        if (nameCol != null) {
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        }
        if (surnameCol != null) {
            surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        }
        if (telephoneCol != null) {
            telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        }
        if (genderCol != null) {
            genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        }
        if (passwordCol != null) {
            passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        }
    }

    private void setupEditableColumns() {
        if (emailCol != null) {
            emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
            emailCol.setOnEditCommit(e -> updateField(e, User::setEmail));
        }

        if (nameCol != null) {
            nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            nameCol.setOnEditCommit(e -> updateField(e, User::setName));
        }

        if (surnameCol != null) {
            surnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            surnameCol.setOnEditCommit(e -> updateField(e, User::setSurname));
        }

        if (telephoneCol != null) {
            telephoneCol.setCellFactory(TextFieldTableCell.forTableColumn());
            telephoneCol.setOnEditCommit(e -> updateField(e, User::setTelephone));
        }

        if (genderCol != null) {
            genderCol.setCellFactory(TextFieldTableCell.forTableColumn());
            genderCol.setOnEditCommit(e -> updateField(e, User::setGender));
        }

        if (passwordCol != null) {
            passwordCol.setCellFactory(TextFieldTableCell.forTableColumn());
            passwordCol.setOnEditCommit(e -> updateField(e, User::setPassword));
        }

        if (usernameCol != null) {
            usernameCol.setEditable(false);
        }
    }

    private void updateField(TableColumn.CellEditEvent<User, String> event, FieldUpdater updater) {
        User user = event.getRowValue();
        updater.update(user, event.getNewValue());
        dao.updateUser(user);
        refreshTable();
    }

    private void setupDeleteColumn() {
        if (deleteCol != null) {
            deleteCol.setCellFactory(col -> new TableCell<User, Void>() {
                private final Button btnDelete = new Button("Delete");

                {
                    btnDelete.setOnAction(e -> {
                        User user = getTableView().getItems().get(getIndex());
                        confirmDelete(user);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btnDelete);
                }
            });
        }
    }

    private void confirmDelete(User user) {
        try {
            System.out.println("Abriendo popup de confirmación para usuario: " + user.getUsername());

            // Cargar el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DeleteConfirmationView.fxml"));
            Parent root = loader.load();

            // Obtener el controlador
            DeleteConfirmationViewController controller = loader.getController();
            System.out.println("Controlador obtenido: " + (controller != null ? "OK" : "NULL"));

            controller.setUser(user);

            if (profile != null && profile instanceof Admin) {
                controller.setCurrentUser(profile);

                Admin admin = (Admin) profile;
                controller.setAdminPassword(admin.getPassword());
            }

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(root));

            controller.setStage(popupStage);

            popupStage.showAndWait();

            if (controller.isConfirmed()) {
                System.out.println("Confirmado - Eliminando usuario: " + user.getUsername());
                deleteUser(user);
            } else {
                System.out.println("Cancelado - No se elimina el usuario");
            }

        } catch (Exception e) {
            System.err.println("ERROR en confirmDelete:");
            e.printStackTrace();

            System.out.println("Error al abrir popup. ¿Eliminar usuario " + user.getUsername() + "? (S/N)");
            deleteUser(user);
        }
    }

    private void deleteUser(User user) {
        dao.deleteUser(user);
        userList.remove(user);
        refreshTable();
        System.out.println("Usuario eliminado: " + user.getUsername());
    }

    private void loadUsers() {
        userList.setAll(dao.findAll());
        System.out.println("Usuarios cargados: " + userList.size());
    }

    private void refreshTable() {
        tableView.refresh();
    }

    private void setupEditMode() {
        if (editCheckBox != null && tableView != null) {
            // Estado inicial
            tableView.setEditable(editCheckBox.isSelected());
            updateColumnEditability(editCheckBox.isSelected());

            // Listener para cambios
            editCheckBox.selectedProperty().addListener((obs, oldValue, isSelected) -> {
                tableView.setEditable(isSelected);
                updateColumnEditability(isSelected);
                System.out.println("Modo edición: " + isSelected);
            });
        }
    }

    private void updateColumnEditability(boolean isEditable) {
        if (emailCol != null) {
            emailCol.setEditable(isEditable);
        }
        if (nameCol != null) {
            nameCol.setEditable(isEditable);
        }
        if (surnameCol != null) {
            surnameCol.setEditable(isEditable);
        }
        if (telephoneCol != null) {
            telephoneCol.setEditable(isEditable);
        }
        if (genderCol != null) {
            genderCol.setEditable(isEditable);
        }
        if (passwordCol != null) {
            passwordCol.setEditable(isEditable);
        }
    }

    @FXML
    private void addUser() {
        User newUser = new User(
                "", // username
                "", // password
                "", // email
                "", // user_code
                "", // name
                "", // telephone
                "", // surname
                "" // card number
        );

        dao.saveUser(newUser);
        userList.add(newUser);
        refreshTable();
        System.out.println("Nuevo usuario añadido");
    }

    @FXML
    private void goToLogin() {
        try {
            Stage currentStage = (Stage) tableView.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToCompanies(ActionEvent event) {
        changeWindow("/view/CompaniesTable.fxml", event);
    }

    @FXML
    private void goToProducts(ActionEvent event) {
        changeWindow("/view/ProductModifyWindow.fxml", event);
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

    @FunctionalInterface
    private interface FieldUpdater {

        void update(User user, String value);
    }
}
