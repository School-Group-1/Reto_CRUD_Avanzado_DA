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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class UserTableController implements Initializable {

    @FXML private CheckBox editCheckBox;
    @FXML private TableView<User> tableView;
    @FXML private TableColumn<User, String> emailCol, nameCol, passwordCol, usernameCol, genderCol, surnameCol, telephoneCol;
    @FXML private TableColumn<User, Void> deleteCol;

    private Profile profile;
    private Controller cont;
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private DBImplementation dao = new DBImplementation();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (tableView != null) {
            setupColumns();
            setupEditableColumns();
            setupDeleteColumn();
            loadUsers();
            tableView.setItems(userList);
            setupEditMode();
        }
    }

    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;
        if (profile instanceof Admin) {
            System.out.println("Admin: " + ((Admin) profile).getUsername());
            loadUsers();
        }
    }

    private void setupColumns() {
        setColumnValueFactory(usernameCol, "username");
        setColumnValueFactory(passwordCol, "password");
        setColumnValueFactory(emailCol, "email");
        setColumnValueFactory(nameCol, "name");
        setColumnValueFactory(surnameCol, "surname");
        setColumnValueFactory(telephoneCol, "telephone");
        setColumnValueFactory(genderCol, "gender");
    }

    private void setColumnValueFactory(TableColumn<User, String> column, String property) {
        if (column != null) column.setCellValueFactory(new PropertyValueFactory<>(property));
    }

    private void setupEditableColumns() {
        setupEditableColumn(emailCol, User::setEmail);
        setupEditableColumn(nameCol, User::setName);
        setupEditableColumn(surnameCol, User::setSurname);
        setupEditableColumn(telephoneCol, User::setTelephone);
        setupEditableColumn(genderCol, User::setGender);
        setupEditableColumn(passwordCol, User::setPassword);
        if (usernameCol != null) usernameCol.setEditable(false);
    }

    private void setupEditableColumn(TableColumn<User, String> column, BiConsumer<User, String> updater) {
        if (column != null) {
            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setOnEditCommit(e -> updateUser(e, updater));
        }
    }

    private void updateUser(TableColumn.CellEditEvent<User, String> event, BiConsumer<User, String> updater) {
        User user = event.getRowValue();
        updater.accept(user, event.getNewValue());
        dao.updateUser(user);
        tableView.refresh();
    }

    private void setupDeleteColumn() {
        if (deleteCol != null) {
            deleteCol.setCellFactory(col -> new TableCell<User, Void>() {
                private final Button btn = new Button("Delete");
                {
                    btn.setOnAction(e -> {
                        User user = getTableView().getItems().get(getIndex());
                        deleteUser(user);
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btn);
                }
            });
        }
    }

    private void deleteUser(User user) {
        if (confirmDelete("¿Eliminar usuario " + user.getUsername() + "?")) {
            dao.deleteUser(user);
            userList.remove(user);
            tableView.refresh();
        }
    }

    private boolean confirmDelete(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void loadUsers() {
        userList.setAll(dao.findAll());
        System.out.println("Usuarios cargados: " + userList.size());
    }

    private void setupEditMode() {
        if (editCheckBox != null && tableView != null) {
            tableView.setEditable(editCheckBox.isSelected());
            updateEditability(editCheckBox.isSelected());
            
            editCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                tableView.setEditable(newVal);
                updateEditability(newVal);
            });
        }
    }

    private void updateEditability(boolean editable) {
        TableColumn<?, ?>[] columns = {emailCol, nameCol, surnameCol, telephoneCol, genderCol, passwordCol};
        for (TableColumn<?, ?> col : columns) {
            if (col != null) col.setEditable(editable);
        }
    }

    @FXML
    private void addUser() {
        User newUser = new User("", "", "", "", "", "", "", "");
        dao.saveUser(newUser);
        userList.add(newUser);
        tableView.refresh();
    }

    @FXML
    private void goToLogin() {
        navigateTo("/view/LogInWindow.fxml");
    }

    @FXML
    private void goToCompanies(javafx.event.ActionEvent event) {
        navigateTo("/view/CompaniesTable.fxml", event);
    }

    @FXML
    private void goToProducts(javafx.event.ActionEvent event) {
        navigateTo("/view/ProductModifyWindow.fxml", event);
    }

    private void navigateTo(String fxml) {
        try {
            Stage stage = (Stage) tableView.getScene().getWindow();
            stage.close();
            loadNewStage(fxml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateTo(String fxml, javafx.event.ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            loadNewStage(fxml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadNewStage(String fxml) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxml))));
        stage.show();
    }
}
