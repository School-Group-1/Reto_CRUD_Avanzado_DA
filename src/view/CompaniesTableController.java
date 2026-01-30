/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Admin;
import model.Company;
import model.DBImplementation;

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

    private Admin loggedAdmin;
    private ObservableList<Company> companyList = FXCollections.observableArrayList();
    private DBImplementation dao = new DBImplementation();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        checkbox();
        setupColumns();
        setupEditableColumns();
        setupDeleteColumn();
        companyList = FXCollections.observableArrayList(dao.findAllCompanies());
        tableView.setItems(companyList);
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
            dao.updateCompany(comp);
            refreshTable();
        });
        
        nieCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            comp.setNie(event.getNewValue());
            dao.updateCompany(comp);
            refreshTable();
        });
        
        locationCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            comp.setLocation(event.getNewValue());
            dao.updateCompany(comp);
            refreshTable();
        });
        
        urlCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            comp.setUrl(event.getNewValue());
            dao.updateCompany(comp);
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
        dao.saveCompany(newComp);
        companyList.add(newComp);
        refreshTable();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void deleteCompany(Company company) {
        try {
            dao.deleteCompany(company);
            companyList.remove(company);
            refreshTable();
            showSuccessMessage("Compañía eliminada correctamente: " + company.getName());
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al eliminar la compañía");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void confirmDelete(Company company) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DeleteConfirmationView.fxml"));
            Parent root = loader.load();

            DeleteConfirmationViewController controller = loader.getController();
            controller.setCompanyToDelete(company);
            controller.setCurrentUser(loggedAdmin); // Admin siempre logueado

            Stage popupStage = new Stage();
            popupStage.setTitle("Confirmar eliminación");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(root));
            controller.setStage(popupStage);

            popupStage.showAndWait();

            if (controller.isConfirmed()) {
                deleteCompany(company);
            }

        } catch (IOException e) {
            e.printStackTrace();
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

    private void refreshTable() {
        companyList.setAll(dao.findAllCompanies());
    }
}
