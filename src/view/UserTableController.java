/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
    private TextField tfUser ;
    @FXML
    private TextField tfEmail ;
    @FXML
    private TextField tfName ;
    @FXML
    private TextField tfSurname ;
    @FXML
    private TextField tfTel ;
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
    
    
    private ObservableList<User> userList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        userList= add();
        checkbox();
        tableView.setItems(userList);
        setupColumns();
        setupEditableColumns();
        setupDeleteColumn();

    }    
    
    public void checkbox () {
        // Estado inicial de la tabla
        tableView.setEditable(editCheckBox.isSelected());

        // Cuando cambia el checkbox, cambia la edición de la tabla
        editCheckBox.selectedProperty().addListener((obs, oldValue, isSelected) -> {
        tableView.setEditable(isSelected);
        });
    }
    
    private void setupEditableColumns() {
    // Columnas editables
    emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
    nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
    surnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
    telephoneCol.setCellFactory(TextFieldTableCell.forTableColumn());
    genderCol.setCellFactory(TextFieldTableCell.forTableColumn());
    passwordCol.setCellFactory(TextFieldTableCell.forTableColumn());
    
    // Columnas NO editables
    usernameCol.setEditable(false);
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
    
    private ObservableList<User> add () {
        User newUser = new User("a","q","a@a.com",12,"uwu","123456789","r","male","12123454678");
        userList.add(newUser);
        return userList;
    }
    
    @FXML
    private void addUser() {

    User newUser = new User(
        "",     // username
        "",     // password
        "",     // email
        0,      // user_code (si no lo usas ahora)
        "",     // name
        "",     // telephone
        "",     // surname
        "",     // gender
        ""      // card number
    );

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
   
   private void confirmDelete(User user) {

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Eliminar usuario");
    alert.setHeaderText("¿Eliminar al usuario " + user.getUsername() + "?");

    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("Contraseña admin");

    alert.getDialogPane().setContent(passwordField);

    alert.showAndWait().ifPresent(result -> {
        if (result == ButtonType.OK && isPasswordCorrect(passwordField.getText())) {
            deleteUser(user);
        }
    });
    }

    private boolean isPasswordCorrect(String text) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void deleteUser(User user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
