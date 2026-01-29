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
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Admin;
import model.Company;
import model.DBImplementation;
import model.User;

/**
 * FXML Controller class
 *
 * @author acer
 */
public class CompaniesTableController implements Initializable {

    /**
     * Initializes the controller class.
     */
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
        // TODO
        setupColumns();
        setupEditableColumns();
        setupDeleteColumn();
        checkbox();
        setupColumns();
        setupEditableColumns();
        setupDeleteColumn();
        companyList = FXCollections.observableArrayList(dao.findAllCompanies());
        tableView.setItems(companyList);
    }

    public void checkbox() {
        // Estado inicial de la tabla
        tableView.setEditable(editCheckBox.isSelected());

        // Cuando cambia el checkbox, cambia la edición de la tabla
        editCheckBox.selectedProperty().addListener((obs, oldValue, isSelected) -> {
            tableView.setEditable(isSelected);
        });
    }

    private void setupEditableColumns() {
        // Columnas editables
        nameCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            comp.setName(event.getNewValue());
            dao.updateCompany(comp);
        });
        nieCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            comp.setNie(event.getNewValue());
            dao.updateCompany(comp);
        });
        locationCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            comp.setLocation(event.getNewValue());
            dao.updateCompany(comp);
        });
        urlCol.setOnEditCommit(event -> {
            Company comp = event.getRowValue();
            comp.setUrl(event.getNewValue());
            dao.updateCompany(comp);
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

        Company newComp = new Company(
                "", // name
                "", // nie
                "", // location
                "" // url
        );

        dao.saveCompany(newComp);
        companyList.add(newComp);
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("No se pudo eliminar la compañía");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void deleteCompany(Company company) {
        try {
            // Eliminar de la base de datos
            dao.deleteCompany(company);

            // Eliminar de la lista local
            companyList.remove(company);

            // Actualizar la tabla
            refreshTable();

            // Mostrar mensaje de éxito
            showSuccessMessage("Compañía eliminada correctamente: " + company.getName());

        } catch (Exception e) {
            e.printStackTrace();
            // Mostrar mensaje de error
            showErrorMessage("Error al eliminar la compañía: " + e.getMessage());
        }
    }

    private void confirmDelete(Company company) {
        // Crear un diálogo de confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Eliminar compañía: " + company.getName() + "?");
        alert.setContentText("NIE: " + company.getNie() + "\nEsta acción no se puede deshacer.");

        // Mostrar el diálogo y esperar respuesta
        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                deleteCompany(company);
            }
        });
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

    private void deleteUser(User user) {
        dao.deleteUser(user);
        companyList.remove(user);
    }

    private void refreshTable() {
        companyList.setAll(dao.findAllCompanies());
    }

}
