/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Admin;
import model.Company;
import model.DBImplementation;
import model.Profile;
import model.User;
import report.ReportService;

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
    @FXML
    private Menu helpMenu;
    @FXML
    private MenuItem viewManualItem;

    private Admin loggedAdmin;
    private Profile profile;
    private Controller cont;
    private ObservableList<User> userList = FXCollections.observableArrayList();
    
    private static final Logger LOGGER = Logger.getLogger(UserTableController.class.getName());

    private ContextMenu contextMenu;
    private MenuItem reportItem;
    @FXML
    private Button addButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("**UserTable** Initializing User Table Window Controller");
        contextMenu = new ContextMenu();

        reportItem = new MenuItem("Report");
        reportItem.setOnAction(e -> handleImprimirAction());

        contextMenu.getItems().add(reportItem);
        tableView.setOnContextMenuRequested(event -> {
            LOGGER.fine("**UserTable** Showing context menu");
            contextMenu.show(
                    tableView.getScene().getWindow(),
                    event.getScreenX(),
                    event.getScreenY()
            );
            event.consume();
        });
    }

    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;
        
        LOGGER.log(Level.INFO, "**UserTable** Profile received: {0}", profile);
        LOGGER.log(Level.INFO, "**UserTable** Controller received: {0}", cont);
        
        checkbox();
        setupColumns();
        setupEditableColumns();
        setupDeleteColumn();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        userList = cont.findAll();
        tableView.setItems(userList);
        LOGGER.log(Level.INFO, "**UserTable** Finished loading window data. Users loaded: {0}", userList.size());
    }

    public void checkbox() {
        boolean isSelected = editCheckBox.isSelected();
        LOGGER.log(Level.INFO, "**UserTable** Configuring edit checkbox. Initial state: {0}", isSelected);
        
        tableView.setEditable(isSelected);
        updateColumnEditability(isSelected);

        editCheckBox.selectedProperty().addListener((obs, oldValue, isSelectedNew) -> {
            tableView.setEditable(isSelectedNew);
            updateColumnEditability(isSelectedNew);
            LOGGER.log(Level.INFO, "**UserTable** Edit mode changed to: {0}", isSelectedNew);
        });
    }

    private void updateColumnEditability(boolean isEditable) {
        emailCol.setEditable(isEditable);
        nameCol.setEditable(isEditable);
        surnameCol.setEditable(isEditable);
        telephoneCol.setEditable(isEditable);
        genderCol.setEditable(isEditable);
        passwordCol.setEditable(isEditable);
        LOGGER.log(Level.FINE, "**UserTable** Column editability updated to: {0}", isEditable);
    }

    private void setupEditableColumns() {
        LOGGER.info("**UserTable** Setting up editable columns");
        
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        surnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        telephoneCol.setCellFactory(TextFieldTableCell.forTableColumn());
        genderCol.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordCol.setCellFactory(TextFieldTableCell.forTableColumn());

        emailCol.setOnEditCommit(event -> {
            User user = event.getRowValue();
            LOGGER.log(Level.INFO, "**UserTable** Editing user email: {0} -> {1}", new Object[]{user.getEmail(), event.getNewValue()});
            user.setEmail(event.getNewValue());
            cont.updateUser(user);
            refreshTable();
        });

        nameCol.setOnEditCommit(event -> {
            User user = event.getRowValue();
            LOGGER.log(Level.INFO, "**UserTable** Editing user name: {0} -> {1}", new Object[]{user.getName(), event.getNewValue()});
            user.setName(event.getNewValue());
            cont.updateUser(user);
            refreshTable();
        });

        surnameCol.setOnEditCommit(event -> {
            User user = event.getRowValue();
            LOGGER.log(Level.INFO, "**UserTable** Editing user surname: {0} -> {1}", new Object[]{user.getSurname(), event.getNewValue()});
            user.setSurname(event.getNewValue());
            cont.updateUser(user);
            refreshTable();
        });

        telephoneCol.setOnEditCommit(event -> {
            User user = event.getRowValue();
            LOGGER.log(Level.INFO, "**UserTable** Editing user telephone: {0} -> {1}", new Object[]{user.getTelephone(), event.getNewValue()});
            user.setTelephone(event.getNewValue());
            cont.updateUser(user);
            refreshTable();
        });

        genderCol.setOnEditCommit(event -> {
            User user = event.getRowValue();
            LOGGER.log(Level.INFO, "**UserTable** Editing user gender: {0} -> {1}", new Object[]{user.getGender(), event.getNewValue()});
            user.setGender(event.getNewValue());
            cont.updateUser(user);
            refreshTable();
        });

        passwordCol.setOnEditCommit(event -> {
            User user = event.getRowValue();
            LOGGER.log(Level.INFO, "**UserTable** Editing user password: {0} -> {1}", new Object[]{user.getPassword(), event.getNewValue()});
            user.setPassword(event.getNewValue());
            cont.updateUser(user);
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
        
        LOGGER.info("**UserTable** Editable columns setup completed");
    }

    private void setupColumns() {
        LOGGER.info("**UserTable** Setting up table columns");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        LOGGER.info("**UserTable** Table columns setup completed");
    }

    @FXML
    private void addUser() {
        LOGGER.info("**UserTable** Adding new user");
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

        cont.saveUser(newUser);
        userList.add(newUser);
        LOGGER.log(Level.INFO, "**UserTable** New user added: {0}", newUser.getUsername());
    }

    private void setupDeleteColumn() {
        LOGGER.info("**UserTable** Setting up delete column");
        deleteCol.setCellFactory(col -> new TableCell<User, Void>() {
            private final Button btnDelete = new Button("Delete");

            {
                btnDelete.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    LOGGER.log(Level.FINE, "**UserTable** Delete button clicked for user: {0}", user.getUsername());
                    confirmDelete(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
        LOGGER.info("**UserTable** Delete column setup completed");
    }

    @FXML
    private void goToLogin() {
        LOGGER.info("**UserTable** Switching to login window");
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
            
            LOGGER.info("**UserTable** Successfully switched to login window");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**UserTable** Error switching to login window: ", e);
        }
    }

    private void confirmDelete(User user) {
        LOGGER.log(Level.INFO, "**UserTable** Confirming deletion of user: {0}", user.getUsername());
        
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
                LOGGER.log(Level.INFO, "**UserTable** Confirmed deletion of user: {0}", user.getUsername());
                cont.dropOutAdmin(user.getUsername(),
                        controller.getAdminUsername(),
                        controller.getEnteredPassword());
                userList.remove(user);
                tableView.refresh();
                LOGGER.log(Level.INFO, "**UserTable** User deleted successfully: {0}", user.getUsername());
            } else {
                LOGGER.log(Level.INFO, "**UserTable** Deletion cancelled for user: {0}", user.getUsername());
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**UserTable** Error loading confirmation dialog: ", e);
        }
    }

    private void refreshTable() {
        LOGGER.fine("**UserTable** Refreshing table data");
        userList.setAll(cont.findAll());
        LOGGER.log(Level.INFO, "**UserTable** Table refreshed with {0} users", userList.size());
    }

    @FXML
    private void goToCompanies(ActionEvent event) {
        LOGGER.info("**UserTable** Switching to companies window");
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
            
            LOGGER.info("**UserTable** Successfully switched to companies window");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**UserTable** Error switching to companies window: ", e);
        }
    }

    @FXML
    private void goToProducts(ActionEvent event) {
        LOGGER.info("**UserTable** Switching to products window");
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
            
            LOGGER.info("**UserTable** Successfully switched to products window");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**UserTable** Error switching to products window: ", e);
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        LOGGER.info("**UserTable** Logging out, switching to login window");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();
            
            LOGGER.info("**UserTable** Successfully switched to login window");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**UserTable** Error switching to login window: ", e);
        }
    }

    @FXML
    private void showContextMenu(ContextMenuEvent event) {
        LOGGER.fine("**UserTable** Showing context menu");
        contextMenu.show(
                tableView.getScene().getWindow(),
                event.getScreenX(),
                event.getScreenY()
        );
        event.consume();
    }

    private void handleImprimirAction() {
        LOGGER.info("**UserTable** Generating users report");
        List<User> users = cont.findAll();
        LOGGER.log(Level.INFO, "**UserTable** Found {0} users for report", users.size());

        List<Profile> profiles = new ArrayList<>(users);
        ReportService reportService = new ReportService();
        reportService.generateUsersReport((List<Profile>) profiles);

        LOGGER.info("**UserTable** Users report generated successfully");
    }
    
    @FXML
    private void openUserManual(ActionEvent event) {
        LOGGER.info("**UserTable** Opening user manual");
        
        try {
            File pdf = new File("pdfs/User_Manual.pdf");
            if (!pdf.exists()) {
                LOGGER.warning("**UserTable** User manual not found at: pdfs/User_Manual.pdf");
                return;
            }

            Desktop.getDesktop().open(pdf);
            LOGGER.info("**UserTable** User manual opened successfully");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "**UserTable** Error opening user manual: ", ex);
        }
    }
}
