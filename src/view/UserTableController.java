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
import java.util.Optional;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Admin;
import model.Profile;
import model.User;
import report.ReportService;

/**
 * FXML Controller class for the user management window.
 *
 * This controller handles the user table interface where administrators can
 * view, create, modify, and delete users. It includes features like editable
 * table cells, user deletion with admin confirmation, report generation, and
 * navigation to other system windows.
 *
 * @author acer
 */
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

    /**
     * Initializes the controller with profile and controller data.
     */
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

        usernameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        usernameCol.setEditable(false);

        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        surnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        telephoneCol.setCellFactory(TextFieldTableCell.forTableColumn());
        genderCol.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordCol.setCellFactory(TextFieldTableCell.forTableColumn());

        emailCol.setOnEditCommit(event -> {
            User user = event.getRowValue();
            String newEmail = event.getNewValue();
            
            // Validar email antes de guardar
            if (isValidEmail(newEmail)) {
                LOGGER.log(Level.INFO, "**UserTable** Editing user email: {0} -> {1}", new Object[]{user.getEmail(), newEmail});
                user.setEmail(newEmail);
                cont.updateUser(user);
                refreshTable();
            } else {
                showAlert("Invalid Email", "Please enter a valid email address (e.g., user@example.com)", Alert.AlertType.ERROR);
                tableView.refresh(); // Refresca para mostrar el valor original
            }
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
            String newTelephone = event.getNewValue();
            
            // Validar teléfono antes de guardar
            if (isValidTelephone(newTelephone)) {
                LOGGER.log(Level.INFO, "**UserTable** Editing user telephone: {0} -> {1}", new Object[]{user.getTelephone(), newTelephone});
                user.setTelephone(newTelephone);
                cont.updateUser(user);
                refreshTable();
            } else {
                showAlert("Invalid Telephone", "Telephone must contain only numbers (9 digits)", Alert.AlertType.ERROR);
                tableView.refresh(); // Refresca para mostrar el valor original
            }
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

    /**
     * Opens a dialog to add a new user to the system.
     */
    @FXML
    private void addUser() {
        LOGGER.info("**UserTable** Adding new user");

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter username for new user");
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String username = result.get().trim();

            if (cont.findUserByUsername(username) != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Username already exists");
                alert.setContentText("The username '" + username + "' is already taken. Please choose a different username.");
                alert.showAndWait();
                return;
            }

            // Crear nuevo usuario con validaciones
            User newUser = createValidatedNewUser(username);
            if (newUser != null) {
                cont.saveUser(newUser);
                userList.add(newUser);
                LOGGER.log(Level.INFO, "**UserTable** New user added with username: {0}", username);
                refreshTable();
            }
        }
    }

    /**
     * Creates a new user with validated email and telephone.
     * Opens dialogs for user to enter validated information.
     */
    private User createValidatedNewUser(String username) {
        String email = getValidatedEmail();
        if (email == null) return null; 
        
        String telephone = getValidatedTelephone();
        if (telephone == null) return null; 

        User newUser = new User(
                "",
                "",
                username,
                "",
                email,
                "",
                telephone,
                ""
        );
        
        return newUser;
    }

    /**
     * Opens a dialog to get a validated email from user.
     */
    private String getValidatedEmail() {
        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Enter Email");
        emailDialog.setHeaderText("Email address for new user");
        emailDialog.setContentText("Email:");

        while (true) {
            Optional<String> emailResult = emailDialog.showAndWait();
            if (!emailResult.isPresent()) {
                return null; 
            }
            
            String email = emailResult.get().trim();
            if (email.isEmpty()) {
                return "";
            }
            
            if (isValidEmail(email)) {
                return email;
            } else {
                showAlert("Invalid Email", "Please enter a valid email address (e.g., user@example.com)", Alert.AlertType.ERROR);
                emailDialog.getEditor().clear();
            }
        }
    }

    /**
     * Opens a dialog to get a validated telephone from user.
     */
    private String getValidatedTelephone() {
        TextInputDialog telDialog = new TextInputDialog();
        telDialog.setTitle("Enter Telephone");
        telDialog.setHeaderText("Telephone number for new user");
        telDialog.setContentText("Telephone:");

        while (true) {
            Optional<String> telResult = telDialog.showAndWait();
            if (!telResult.isPresent()) {
                return null; // Usuario canceló
            }
            
            String telephone = telResult.get().trim();
            if (telephone.isEmpty()) {
                // Permitir teléfono vacío si el usuario quiere
                return "";
            }
            
            if (isValidTelephone(telephone)) {
                return telephone;
            } else {
                showAlert("Invalid Telephone", "Telephone must contain only numbers (9 digits)", Alert.AlertType.ERROR);
                telDialog.getEditor().clear();
            }
        }
    }

    /**
     * Validates email format.
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return true; // Permitir vacío
        }
        
        // Expresión regular simple para validar email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    /**
     * Validates telephone format.
     */
    private boolean isValidTelephone(String telephone) {
        if (telephone == null || telephone.isEmpty()) {
            return true; // Permitir vacío
        }
        
        return telephone.matches("\\d{9}");
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

    /**
     * Shows a confirmation dialog for user deletion.
     */
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

    /**
     * Navigates to the companies management window.
     */
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

    /**
     * Navigates to the products management window.
     */
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

    /**
     * Generates and opens a PDF report of all users.
     */
    private void handleImprimirAction() {
        LOGGER.info("**UserTable** Generating users report");
        List<User> users = cont.findAll();
        LOGGER.log(Level.INFO, "**UserTable** Found {0} users for report", users.size());

        List<Profile> profiles = new ArrayList<>(users);
        ReportService reportService = new ReportService();
        reportService.generateUsersReport((List<Profile>) profiles);

        LOGGER.info("**UserTable** Users report generated successfully");
    }

    /**
     * Opens the user manual PDF file.
     */
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

    /**
     * Shows an alert dialog.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        LOGGER.log(Level.INFO, "**UserTable** Showing alert: {0} - {1}", new Object[]{title, message});
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}