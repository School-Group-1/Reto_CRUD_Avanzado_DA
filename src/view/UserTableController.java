/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Admin;
import model.DBImplementation;
import model.Profile;
import model.User;

/**
 * FXML Controller class
 *
 * @author acer
 */
public class UserTableController implements Initializable {

    /**
     * Initializes the controller class.
     */
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

    private Admin loggedAdmin;

    private Profile profile;

    private Controller cont;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    private DBImplementation dao = new DBImplementation();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;
        System.out.println("Perfil: " + profile);
        System.out.println("Controller: " + cont);
        checkbox();
        setupColumns();
        setupEditableColumns();
        setupDeleteColumn();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        userList = dao.findAll();
        tableView.setItems(userList);
    }

    public void checkbox() {
        tableView.setEditable(editCheckBox.isSelected());

        updateColumnEditability(editCheckBox.isSelected());

        editCheckBox.selectedProperty().addListener((obs, oldValue, isSelected) -> {
            tableView.setEditable(isSelected);
            updateColumnEditability(isSelected);
            System.out.println("DEBUG: Modo edición = " + isSelected);
        });
    }

    private void updateColumnEditability(boolean isEditable) {
        emailCol.setEditable(isEditable);
        nameCol.setEditable(isEditable);
        surnameCol.setEditable(isEditable);
        telephoneCol.setEditable(isEditable);
        genderCol.setEditable(isEditable);
        passwordCol.setEditable(isEditable);
    }

    private void setupEditableColumns() {
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        surnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        telephoneCol.setCellFactory(TextFieldTableCell.forTableColumn());
        genderCol.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordCol.setCellFactory(TextFieldTableCell.forTableColumn());

        emailCol.setOnEditCommit(event -> {
            System.out.println("DEBUG: Editando email");
            User user = event.getRowValue();
            System.out.println("Usuario: " + user.getUsername() + ", Email nuevo: " + event.getNewValue());
            user.setEmail(event.getNewValue());
            dao.updateUser(user);
            refreshTable();
        });

        nameCol.setOnEditCommit(event -> {
            System.out.println("DEBUG: Editando nombre");
            User user = event.getRowValue();
            user.setName(event.getNewValue());
            dao.updateUser(user);
            refreshTable();
        });

        surnameCol.setOnEditCommit(event -> {
            System.out.println("DEBUG: Editando apellido");
            User user = event.getRowValue();
            user.setSurname(event.getNewValue());
            dao.updateUser(user);
            refreshTable();
        });

        telephoneCol.setOnEditCommit(event -> {
            System.out.println("DEBUG: Editando teléfono");
            User user = event.getRowValue();
            user.setTelephone(event.getNewValue());
            dao.updateUser(user);
            refreshTable();
        });

        genderCol.setOnEditCommit(event -> {
            System.out.println("DEBUG: Editando género");
            User user = event.getRowValue();
            user.setGender(event.getNewValue());
            dao.updateUser(user);
            refreshTable();
        });

        passwordCol.setOnEditCommit(event -> {
            System.out.println("DEBUG: Editando contraseña");
            User user = event.getRowValue();
            user.setPassword(event.getNewValue());
            dao.updateUser(user);
            refreshTable();
        });

        usernameCol.setEditable(false);
        usernameCol.setCellFactory(col -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                setGraphic(null);
            }
        });
    }

    private void setupColumns() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
    }

    @FXML
    private void addUser() {

        User newUser = new User(
                "", // username
                "", // password
                "", // email
                "", // user_code (si no lo usas ahora)
                "", // name
                "", // telephone
                "", // surname
                "" // card number
        );

        dao.saveUser(newUser);
        userList.add(newUser);
    }

    private void setupDeleteColumn() {

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

    /*
        SE PUEDE SUSTITUIR POR LOGOUT?
     */
    @FXML
    private void goToLogin() {
        try {
            Stage currentStage = (Stage) tableView.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/LogInWindow.fxml")
            );
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void confirmDelete(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DeleteConfirmationView.fxml"));
            Parent root = loader.load();

            DeleteConfirmationViewController controller = loader.getController();

            controller.initData(profile, cont);

            controller.setUserToDelete(user);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(root));
            controller.setStage(popupStage);

            popupStage.showAndWait();

            if (controller.isConfirmed()) {
                cont.dropOutAdmin(user.getUsername(),
                controller.getAdminUsername(), 
                controller.getEnteredPassword());
                userList.remove(user);
                tableView.refresh();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshTable() {
        userList.setAll(dao.findAll());
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
}
