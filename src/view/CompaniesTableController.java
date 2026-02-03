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
 * FXML Controller class
 *
 * @author acer
 */
public class CompaniesTableController implements Initializable {

    @FXML
    private CheckBox editCheckBox;
    @FXML
    private TableView<Company> tableView;
    @FXML
    private TableColumn<Company, String> nieCol;
    @FXML
    private TableColumn<Company, String> nameCol;
    @FXML
    private TableColumn<Company, String> locationCol;
    @FXML
    private TableColumn<Company, String> urlCol;
    @FXML
    private TableColumn<Company, Void> deleteCol;
    @FXML
    private Menu helpMenu;
    @FXML
    private MenuItem viewManualItem;
    @FXML
    private Button logout;
    
    private Profile profile;
    private Controller cont;
    
    private static final Logger LOGGER = Logger.getLogger(CompaniesTableController.class.getName());
    
    private ContextMenu contextMenu;
    private MenuItem reportItem;
    private Admin loggedAdmin;
    private ObservableList<Company> companyList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("**CompaniesTable** Initializing Companies Table Window Controller");
        contextMenu = new ContextMenu();

        reportItem = new MenuItem("Report");
        reportItem.setOnAction(e -> handleImprimirAction());

        contextMenu.getItems().add(reportItem);
        tableView.setOnContextMenuRequested(this::showContextMenu);
    }

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

    private void updateColumnEditability(boolean isEditable) {
        nameCol.setEditable(isEditable);
        nieCol.setEditable(isEditable);
        locationCol.setEditable(isEditable);
        urlCol.setEditable(isEditable);
        LOGGER.log(Level.FINE, "**CompaniesTable** Column editability updated to: {0}", isEditable);
    }

    public void setLoggedAdmin(Admin admin) {
        this.loggedAdmin = admin;
        LOGGER.log(Level.INFO, "**CompaniesTable** Logged admin set: {0}", admin);
    }

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

    private void setupColumns() {
        LOGGER.info("**CompaniesTable** Setting up table columns");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nieCol.setCellValueFactory(new PropertyValueFactory<>("nie"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
        LOGGER.info("**CompaniesTable** Table columns setup completed");
    }

    @FXML
    private void addUser() {
        LOGGER.info("**CompaniesTable** Adding new company");
        Company newComp = new Company("", "", "", "");
        cont.saveCompany(newComp);
        companyList.add(newComp);
        refreshTable();
        LOGGER.log(Level.INFO, "**CompaniesTable** New company added with ID: {0}", newComp.getNie());
    }

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
    
    @FXML
    private void showContextMenu(ContextMenuEvent event) {
        LOGGER.fine("**CompaniesTable** Showing context menu");
        contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
        event.consume();
    }
    
    private void handleImprimirAction() {
        LOGGER.info("**CompaniesTable** Generating companies report");
        List<Company> companies = cont.findAllCompanies();
        LOGGER.log(Level.INFO, "**CompaniesTable** Found {0} companies for report", companies.size());

        ReportService reportService = new ReportService();
        reportService.generateCompaniesReport(companies);

        LOGGER.info("**CompaniesTable** Companies report generated successfully");
    }
    
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

    private void refreshTable() {
        LOGGER.fine("**CompaniesTable** Refreshing table data");
        companyList.setAll(cont.findAllCompanies());
        LOGGER.log(Level.INFO, "**CompaniesTable** Table refreshed with {0} companies", companyList.size());
    }
}
