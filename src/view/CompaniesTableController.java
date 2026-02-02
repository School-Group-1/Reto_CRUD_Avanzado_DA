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

public class CompaniesTableController implements Initializable {

    @FXML private CheckBox editCheckBox;
    @FXML private TableView<Company> tableView;
    @FXML private TableColumn<Company, String> nieCol, nameCol, locationCol, urlCol;
    @FXML private TableColumn<Company, Void> deleteCol;

    private Profile profile;
    private Controller cont;
    private ObservableList<Company> companyList = FXCollections.observableArrayList();
    private DBImplementation dao = new DBImplementation();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (tableView != null) {
            setupColumns();
            setupEditableColumns();
            setupDeleteColumn();
            loadCompanies();
            tableView.setItems(companyList);
            setupEditMode();
        }
    }

    public void initData(Profile profile, Controller cont) {
        this.profile = profile;
        this.cont = cont;
        if (profile instanceof Admin) {
            System.out.println("Admin: " + ((Admin) profile).getUsername());
            loadCompanies();
        }
    }

    private void setupColumns() {
        setColumnValueFactory(nameCol, "name");
        setColumnValueFactory(nieCol, "nie");
        setColumnValueFactory(locationCol, "location");
        setColumnValueFactory(urlCol, "url");
    }

    private void setColumnValueFactory(TableColumn<Company, String> column, String property) {
        if (column != null) column.setCellValueFactory(new PropertyValueFactory<>(property));
    }

    private void setupEditableColumns() {
        setupEditableColumn(nameCol, Company::setName);
        setupEditableColumn(nieCol, Company::setNie);
        setupEditableColumn(locationCol, Company::setLocation);
        setupEditableColumn(urlCol, Company::setUrl);
    }

    private void setupEditableColumn(TableColumn<Company, String> column, BiConsumer<Company, String> updater) {
        if (column != null) {
            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setOnEditCommit(e -> updateCompany(e, updater));
        }
    }

    private void updateCompany(TableColumn.CellEditEvent<Company, String> event, BiConsumer<Company, String> updater) {
        Company company = event.getRowValue();
        updater.accept(company, event.getNewValue());
        dao.updateCompany(company);
        tableView.refresh();
    }

    private void setupDeleteColumn() {
        if (deleteCol != null) {
            deleteCol.setCellFactory(col -> new TableCell<Company, Void>() {
                private final Button btn = new Button("Delete");
                {
                    btn.setOnAction(e -> {
                        Company company = getTableView().getItems().get(getIndex());
                        deleteCompany(company);
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

    private void deleteCompany(Company company) {
        if (confirmDelete("¿Eliminar compañía " + company.getName() + "?")) {
            dao.deleteCompany(company);
            companyList.remove(company);
            tableView.refresh();
        }
    }

    private boolean confirmDelete(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void loadCompanies() {
        companyList.setAll(dao.findAllCompanies());
        System.out.println("Compañías cargadas: " + companyList.size());
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
        TableColumn<?, ?>[] columns = {nameCol, nieCol, locationCol, urlCol};
        for (TableColumn<?, ?> col : columns) {
            if (col != null) col.setEditable(editable);
        }
    }

    @FXML
    private void addCompany() {
        Company newCompany = new Company(
            "Nueva Compañía",
            "NIE" + System.currentTimeMillis(),
            "Ubicación",
            "http://ejemplo.com"
        );
        dao.saveCompany(newCompany);
        companyList.add(newCompany);
        tableView.refresh();
    }

    @FXML
    private void goToLogin() {
        navigateTo("/view/LogInWindow.fxml");
    }

    @FXML
    private void goToUsers(javafx.event.ActionEvent event) {
        navigateTo("/view/UserTable.fxml", event);
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
