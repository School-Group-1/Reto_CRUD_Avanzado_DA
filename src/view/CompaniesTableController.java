/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Controller;
import java.io.IOException;
import java.net.URL;
import java.util.List;
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
import javafx.scene.control.ContextMenu;
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
import model.DBImplementation;
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

    private Profile profile;

    private Controller cont;
    
    private ContextMenu contextMenu;
    private MenuItem reportItem;
    private Admin loggedAdmin;
    private ObservableList<Company> companyList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        contextMenu = new ContextMenu();

        reportItem = new MenuItem("Report");
        reportItem.setOnAction(e -> handleImprimirAction());

        contextMenu.getItems().add(reportItem);
        tableView.setOnContextMenuRequested(this::showContextMenu);
        
        
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
        companyList = FXCollections.observableArrayList(cont.findAllCompanies());
        tableView.setItems(companyList);
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
    private void goToUsers(ActionEvent event) {
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
            e.printStackTrace();
        }
    }

    public void checkbox() {
        // Estado inicial
        tableView.setEditable(editCheckBox.isSelected());

        // Hacer columnas editables según checkbox
        updateColumnEditability(editCheckBox.isSelected());

        // Listener para cambios
        editCheckBox.selectedProperty().addListener((obs, oldValue, isSelected) -> {
            tableView.setEditable(isSelected);
            updateColumnEditability(isSelected);
        });
    }

    private void updateColumnEditability(boolean isEditable) {
        nameCol.setEditable(isEditable);
        nieCol.setEditable(isEditable);
        locationCol.setEditable(isEditable);
        urlCol.setEditable(isEditable);
    }

    public void setLoggedAdmin(Admin admin) {
        this.loggedAdmin = admin;
    }

    private void setupEditableColumns() {
        // Hacer celdas editables
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nieCol.setCellFactory(TextFieldTableCell.forTableColumn());
        locationCol.setCellFactory(TextFieldTableCell.forTableColumn());
        urlCol.setCellFactory(TextFieldTableCell.forTableColumn());

        // Eventos de edición
        nameCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            comp.setName(event.getNewValue());
            cont.updateCompany(comp);
            refreshTable();
        });

        nieCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            comp.setNie(event.getNewValue());
            cont.updateCompany(comp);
            refreshTable();
        });

        locationCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            comp.setLocation(event.getNewValue());
            cont.updateCompany(comp);
            refreshTable();
        });

        urlCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            comp.setUrl(event.getNewValue());
            cont.updateCompany(comp);
            refreshTable();
        });
    }

    private void setupColumns() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nieCol.setCellValueFactory(new PropertyValueFactory<>("nie"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
    }

    @FXML
    private void addUser() {
        Company newComp = new Company("", "", "", "");
        cont.saveCompany(newComp);
        companyList.add(newComp);
        refreshTable();
    }

    private void confirmDelete(Company company) {
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
                cont.deleteCompany(company);
                companyList.remove(company);
                tableView.refresh();
            }

        } catch (IOException e) {
            cont.deleteCompany(company);
            companyList.remove(company);
            tableView.refresh();
        }
    }

    private void setupDeleteColumn() {
        deleteCol.setCellFactory(col -> new TableCell<Company, Void>() {
            private final Button btnDelete = new Button("Delete");

            {
                btnDelete.setOnAction(e -> {
                    Company company = getTableView().getItems().get(getIndex());
                    confirmDelete(company);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
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
    
    @FXML
    private void showContextMenu(ContextMenuEvent event) {
        contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
        event.consume();
    }
    
    private void handleImprimirAction() {
        List<Company> companies = cont.findAllCompanies();

        ReportService reportService = new ReportService();
        reportService.generateCompaniesReport(companies);

        System.out.println("Reporte generado correctamente");
    }

    private void refreshTable() {
        companyList.setAll(cont.findAllCompanies());
    }
}
