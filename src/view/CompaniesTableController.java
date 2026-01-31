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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Admin;
import model.Company;
import model.DBImplementation;
import model.Profile;

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
    private ObservableList<Company> companyList = FXCollections.observableArrayList();
    private DBImplementation dao = new DBImplementation();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("=== INICIALIZANDO CompaniesTableController ===");
        
        // Verificar qué variables son null
        checkVariables();
        
        // Configurar solo si tableView existe
        if (tableView != null) {
            setupColumns();
            setupEditableColumns();
            setupDeleteColumn();
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            setupEditMode();
        }
    }

    private void checkVariables() {
        System.out.println("tableView: " + (tableView != null ? "OK" : "NULL"));
        System.out.println("nameCol: " + (nameCol != null ? "OK" : "NULL"));
        System.out.println("nieCol: " + (nieCol != null ? "OK" : "NULL"));
        System.out.println("locationCol: " + (locationCol != null ? "OK" : "NULL"));
        System.out.println("urlCol: " + (urlCol != null ? "OK" : "NULL"));
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
        loadCompanies();
    }

    private void setupColumns() {
        if (nameCol != null) nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (nieCol != null) nieCol.setCellValueFactory(new PropertyValueFactory<>("nie"));
        if (locationCol != null) locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        if (urlCol != null) urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
        
        if (tableView != null) {
            tableView.setItems(companyList);
        }
    }

    private void setupEditableColumns() {
        if (nameCol != null) {
            nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            nameCol.setOnEditCommit(e -> updateField(e, Company::setName));
        }
        
        if (nieCol != null) {
            nieCol.setCellFactory(TextFieldTableCell.forTableColumn());
            nieCol.setOnEditCommit(e -> updateField(e, Company::setNie));
        }
        
        if (locationCol != null) {
            locationCol.setCellFactory(TextFieldTableCell.forTableColumn());
            locationCol.setOnEditCommit(e -> updateField(e, Company::setLocation));
        }
        
        if (urlCol != null) {
            urlCol.setCellFactory(TextFieldTableCell.forTableColumn());
            urlCol.setOnEditCommit(e -> updateField(e, Company::setUrl));
        }
    }

    private void updateField(TableColumn.CellEditEvent<Company, String> event, CompanyFieldUpdater updater) {
        Company company = event.getRowValue();
        updater.update(company, event.getNewValue());
        dao.updateCompany(company);
        refreshTable();
    }

    private void setupDeleteColumn() {
        if (deleteCol != null) {
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
    }

    private void confirmDelete(Company company) {
        try {
            System.out.println("Mostrando popup para eliminar: " + company.getName());
            
            // Cargar el FXML del popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DeleteConfirmationView.fxml"));
            Parent root = loader.load();
            
            // Obtener el controlador del popup
            DeleteConfirmationViewController controller = loader.getController();
            
            // Pasar la compañía a eliminar
            controller.setCompanyToDelete(company);
            
            // Pasar el admin actual si existe
            if (profile != null && profile instanceof Admin) {
                controller.setCurrentUser(profile);
            }
            
            // Crear y mostrar el popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Confirmar Eliminación");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(root));
            
            // Pasar el stage al controlador
            controller.setStage(popupStage);
            
            // Mostrar y esperar
            popupStage.showAndWait();
            
            // Verificar si se confirmó la eliminación
            if (controller.isConfirmed()) {
                System.out.println("Confirmado - Eliminando compañía: " + company.getName());
                deleteCompany(company);
            } else {
                System.out.println("Cancelado - No se elimina la compañía");
            }
            
        } catch (Exception e) {
            System.err.println("ERROR al abrir popup de confirmación:");
            e.printStackTrace();
            
            // Si hay error, eliminar directamente
            deleteCompany(company);
        }
    }

    private void deleteCompany(Company company) {
        dao.deleteCompany(company);
        companyList.remove(company);
        refreshTable();
        System.out.println("Compañía eliminada: " + company.getName());
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
        if (nameCol != null) nameCol.setEditable(isEditable);
        if (nieCol != null) nieCol.setEditable(isEditable);
        if (locationCol != null) locationCol.setEditable(isEditable);
        if (urlCol != null) urlCol.setEditable(isEditable);
    }

    private void loadCompanies() {
        companyList.setAll(dao.findAllCompanies());
        System.out.println("Compañías cargadas: " + companyList.size());
        
        if (tableView != null) {
            tableView.setItems(companyList);
            tableView.refresh();
        }
    }

    @FXML
    private void addUser() {
        Company newComp = new Company(
            "Nueva Compañía",
            "NIE" + System.currentTimeMillis(),
            "Ubicación",
            "http://ejemplo.com"
        );
        
        dao.saveCompany(newComp);
        companyList.add(newComp);
        refreshTable();
        System.out.println("Nueva compañía añadida: " + newComp.getName());
    }

    private void refreshTable() {
        tableView.refresh();
    }

    @FXML
    private void goToLogin() {
        try {
            Stage currentStage = (Stage) tableView.getScene().getWindow();
            currentStage.close();

            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/LogInWindow.fxml"))));
            loginStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToUsers(ActionEvent event) {
        navigate("/view/UserTable.fxml", event);
    }
    
    @FXML
    private void goToProducts(ActionEvent event) {
        navigate("/view/ProductModifyWindow.fxml", event);
    }
    
    private void navigate(String fxml, ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            
            Stage newStage = new Stage();
            newStage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxml))));
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    private interface CompanyFieldUpdater {
        void update(Company company, String value);
    }
}
