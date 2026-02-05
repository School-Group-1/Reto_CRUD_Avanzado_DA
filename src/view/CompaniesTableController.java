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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Admin;
import model.Company;
import model.Profile;
import report.ReportService;

/**
 * Controller class for managing the Companies Table view.
 * This controller handles the display, editing, and management of company records
 * in a table format with CRUD operations.
 * 
 * @author acer
 * @version 1.0
 * @since 1.0
 */
public class CompaniesTableController implements Initializable {

    /** Checkbox to toggle edit mode for the table */
    @FXML
    private CheckBox editCheckBox;
    
    /** Table view displaying company records */
    @FXML
    private TableView<Company> tableView;
    
    /** Table column for company NIE (tax identification number) */
    @FXML
    private TableColumn<Company, String> nieCol;
    
    /** Table column for company name */
    @FXML
    private TableColumn<Company, String> nameCol;
    
    /** Table column for company location */
    @FXML
    private TableColumn<Company, String> locationCol;
    
    /** Table column for company website URL */
    @FXML
    private TableColumn<Company, String> urlCol;
    
    /** Table column containing delete buttons for each company */
    @FXML
    private TableColumn<Company, Void> deleteCol;
    
    /** Help menu in the application menu bar */
    @FXML
    private Menu helpMenu;
    
    /** Menu item to view the user manual */
    @FXML
    private MenuItem viewManualItem;
    
    /** Logout button */
    @FXML
    private Button logout;
    
    /** Current user profile */
    private Profile profile;
    
    /** Application controller for business logic */
    private Controller cont;
    
    /** Logger for this controller */
    private static final Logger LOGGER = Logger.getLogger(CompaniesTableController.class.getName());
    
    /** Context menu for additional operations */
    private ContextMenu contextMenu;
    
    /** Report menu item in context menu */
    private MenuItem reportItem;
    
    /** Currently logged in admin user */
    private Admin loggedAdmin;
    
    /** Observable list of companies for the table */
    private ObservableList<Company> companyList = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     * Sets up the context menu and prepares the table for display.
     * 
     * @param url The location used to resolve relative paths for the root object
     * @param rb The resources used to localize the root object
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("**CompaniesTable** Initializing Companies Table Window Controller");
        contextMenu = new ContextMenu();

        reportItem = new MenuItem("Report");
        reportItem.setOnAction(e -> handleImprimirAction());

        contextMenu.getItems().add(reportItem);
        tableView.setOnContextMenuRequested(this::showContextMenu);
    }

    /**
     * Initializes the controller with user data and business logic controller.
     * Loads company data and sets up table columns and edit functionality.
     * 
     * @param profile The current user's profile
     * @param cont The application controller for business logic
     */
    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;

        LOGGER.log(Level.INFO, "**CompaniesTable** Profile received: {0}", profile);
        LOGGER.log(Level.INFO, "**CompaniesTable** Controller received: {0}", cont);

        checkbox();
        setupColumns();
        setupEditableColumns();
        setupDeleteColumn();
        companyList = FXCollections.observableArrayList(cont.findAllCompanies());
        tableView.setItems(companyList);
        LOGGER.log(Level.INFO, "**CompaniesTable** Finished loading window data. Companies loaded: {0}", companyList.size());
    }

    /**
     * Navigates to the products management window.
     * 
     * @param event The action event triggered by the navigation button
     */
    @FXML
    private void goToProducts(ActionEvent event) {
        LOGGER.log(Level.INFO, "**CompaniesTable** Switching to products window");
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
            LOGGER.log(Level.SEVERE, "**CompaniesTable** Error switching to products window: ", e);
        }
    }

    /**
     * Navigates to the users management window.
     * 
     * @param event The action event triggered by the navigation button
     */
    @FXML
    private void goToUsers(ActionEvent event) {
        LOGGER.log(Level.INFO, "**CompaniesTable** Switching to users window");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserTable.fxml"));
            Parent root = loader.load();

            UserTableController viewController = loader.getController();
            viewController.initData(profile, cont);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**CompaniesTable** Error switching to users window: ", e);
        }
    }

    /**
     * Configures the edit checkbox functionality.
     * When selected, enables editing of table cells; when deselected, disables editing.
     */
    public void checkbox() {
        LOGGER.log(Level.INFO, "**CompaniesTable** Configuring edit checkbox. Initial state: {0}", editCheckBox.isSelected());
        
        // Estado inicial
        tableView.setEditable(editCheckBox.isSelected());

        // Hacer columnas editables según checkbox
        updateColumnEditability(editCheckBox.isSelected());

        // Listener para cambios
        editCheckBox.selectedProperty().addListener((obs, oldValue, isSelected) -> {
            tableView.setEditable(isSelected);
            updateColumnEditability(isSelected);
            LOGGER.log(Level.INFO, "**CompaniesTable** Edit mode changed to: {0}", isSelected);
        });
    }

    /**
     * Updates the editability of table columns based on checkbox state.
     * 
     * @param isEditable {@code true} to make columns editable, {@code false} otherwise
     */
    private void updateColumnEditability(boolean isEditable) {
        nameCol.setEditable(isEditable);
        nieCol.setEditable(isEditable);
        locationCol.setEditable(isEditable);
        urlCol.setEditable(isEditable);
        LOGGER.log(Level.FINE, "**CompaniesTable** Column editability updated to: {0}", isEditable);
    }

    /**
     * Sets the logged in admin user.
     * 
     * @param admin The currently logged in admin user
     */
    public void setLoggedAdmin(Admin admin) {
        this.loggedAdmin = admin;
        LOGGER.log(Level.INFO, "**CompaniesTable** Logged admin set: {0}", admin);
    }

    /**
     * Sets up editable columns with cell factories and edit commit handlers.
     * Configures text field cell factories for editable columns and defines
     * behavior when cell editing is committed.
     */
    private void setupEditableColumns() {
        LOGGER.info("**CompaniesTable** Setting up editable columns");
        
        // Hacer celdas editables
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nieCol.setCellFactory(TextFieldTableCell.forTableColumn());
        locationCol.setCellFactory(TextFieldTableCell.forTableColumn());
        urlCol.setCellFactory(TextFieldTableCell.forTableColumn());

        // Eventos de edición
        nameCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            LOGGER.log(Level.INFO, "**CompaniesTable** Editing company name: {0} -> {1}", new Object[]{comp.getName(), event.getNewValue()});
            comp.setName(event.getNewValue());
            cont.updateCompany(comp);
            refreshTable();
        });

        nieCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            LOGGER.log(Level.INFO, "**CompaniesTable** Editing company NIE: {0} -> {1}", new Object[]{comp.getNie(), event.getNewValue()});
            comp.setNie(event.getNewValue());
            cont.updateCompany(comp);
            refreshTable();
        });

        locationCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            LOGGER.log(Level.INFO, "**CompaniesTable** Editing company location: {0} -> {1}", new Object[]{comp.getLocation(), event.getNewValue()});
            comp.setLocation(event.getNewValue());
            cont.updateCompany(comp);
            refreshTable();
        });

        urlCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            LOGGER.log(Level.INFO, "**CompaniesTable** Editing company URL: {0} -> {1}", new Object[]{comp.getUrl(), event.getNewValue()});
            comp.setUrl(event.getNewValue());
            cont.updateCompany(comp);
            refreshTable();
        });
        
        LOGGER.info("**CompaniesTable** Editable columns setup completed");
    }

    /**
     * Sets up table columns with their respective cell value factories.
     * Maps each column to a property of the Company class.
     */
    private void setupColumns() {
        LOGGER.info("**CompaniesTable** Setting up table columns");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nieCol.setCellValueFactory(new PropertyValueFactory<>("nie"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
        LOGGER.info("**CompaniesTable** Table columns setup completed");
    }

    /**
     * Adds a new empty company to the table and database.
     * Creates a new Company object with empty fields and persists it.
     */
    @FXML
    private void addUser() {
        LOGGER.info("**CompaniesTable** Adding new company");
        Company newComp = new Company("", "", "", "");
        cont.saveCompany(newComp);
        companyList.add(newComp);
        refreshTable();
        LOGGER.log(Level.INFO, "**CompaniesTable** New company added with ID: {0}", newComp.getNie());
    }

    /**
     * Shows a confirmation dialog for company deletion.
     * If confirmed, deletes the company from the database and table.
     * 
     * @param company The company to be deleted
     */
    private void confirmDelete(Company company) {
        LOGGER.log(Level.INFO, "**CompaniesTable** Confirming deletion of company: {0}", company.getName());
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DeleteConfirmationView.fxml"));
            Parent root = loader.load();

            DeleteConfirmationViewController controller = loader.getController();
            controller.initData(profile, cont);

            controller.setCompanyToDelete(company);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(root));

            popupStage.showAndWait();

            if (controller.isConfirmed()) {
                LOGGER.log(Level.INFO, "**CompaniesTable** Confirmed deletion of company: {0}", company.getName());
                cont.deleteCompany(company);
                companyList.remove(company);
                tableView.refresh();
                LOGGER.log(Level.INFO, "**CompaniesTable** Company deleted successfully: {0}", company.getName());
            } else {
                LOGGER.log(Level.INFO, "**CompaniesTable** Deletion cancelled for company: {0}", company.getName());
            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "**CompaniesTable** Error loading confirmation dialog, proceeding with deletion: ", e);
            cont.deleteCompany(company);
            companyList.remove(company);
            tableView.refresh();
            LOGGER.log(Level.INFO, "**CompaniesTable** Company deleted without confirmation: {0}", company.getName());
        }
    }

    /**
     * Sets up the delete column with delete buttons for each row.
     * Creates a button in each row that triggers the delete confirmation process.
     */
    private void setupDeleteColumn() {
        LOGGER.info("**CompaniesTable** Setting up delete column");
        deleteCol.setCellFactory(col -> new TableCell<Company, Void>() {
            private final Button btnDelete = new Button("Delete");

            {
                btnDelete.setOnAction(e -> {
                    Company company = getTableView().getItems().get(getIndex());
                    LOGGER.log(Level.FINE, "**CompaniesTable** Delete button clicked for company: {0}", company.getName());
                    confirmDelete(company);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
        LOGGER.info("**CompaniesTable** Delete column setup completed");
    }

    /**
     * Logs out the current user and navigates to the login window.
     * 
     * @param event The action event triggered by the logout button
     */
    @FXML
    private void logout(ActionEvent event) {
        LOGGER.log(Level.INFO, "**CompaniesTable** Logging out, switching to login window");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();
            
            LOGGER.info("**CompaniesTable** Successfully switched to login window");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "**CompaniesTable** Error switching to login window: ", e);
        }
    }
    
    /**
     * Shows the context menu at the specified location.
     * 
     * @param event The context menu event containing screen coordinates
     */
    @FXML
    private void showContextMenu(ContextMenuEvent event) {
        LOGGER.fine("**CompaniesTable** Showing context menu");
        contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
        event.consume();
    }
    
    /**
     * Handles the report generation action.
     * Generates a PDF report of all companies in the system.
     */
    private void handleImprimirAction() {
        LOGGER.info("**CompaniesTable** Generating companies report");
        List<Company> companies = cont.findAllCompanies();
        LOGGER.log(Level.INFO, "**CompaniesTable** Found {0} companies for report", companies.size());

        ReportService reportService = new ReportService();
        reportService.generateCompaniesReport(companies);

        LOGGER.info("**CompaniesTable** Companies report generated successfully");
    }
    
    /**
     * Opens the user manual PDF file.
     * 
     * @param event The action event triggered by the view manual menu item
     */
    @FXML
    private void openUserManual(ActionEvent event) {
        LOGGER.info("**CompaniesTable** Opening user manual");
        
        try {
            File pdf = new File("pdfs/User_Manual.pdf");
            if (!pdf.exists()) {
                LOGGER.warning("**CompaniesTable** User manual not found at: pdfs/User_Manual.pdf");
                return;
            }

            Desktop.getDesktop().open(pdf);
            LOGGER.info("**CompaniesTable** User manual opened successfully");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "**CompaniesTable** Error opening user manual: ", ex);
        }
    }

    /**
     * Refreshes the table data by reloading companies from the database.
     */
    private void refreshTable() {
        LOGGER.fine("**CompaniesTable** Refreshing table data");
        companyList.setAll(cont.findAllCompanies());
        LOGGER.log(Level.INFO, "**CompaniesTable** Table refreshed with {0} companies", companyList.size());
    }
}