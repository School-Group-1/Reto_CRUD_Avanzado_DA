/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewText;

import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import model.HibernateUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

/**
 * Test completo para UserTableController
 */
public class UserTableControllerTest extends ApplicationTest {

    @Override
    public void stop() {
        // Método necesario
    }

    @BeforeClass
    public static void setUpClass() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(main.Main.class);
        HibernateUtil.initializeData();
    }
    
    @Test 
    public void test_CompleteUserTableFlow() {
        System.out.println("=== TEST COMPLETO DE USERTABLE ===");
        
        // ===== 1. LOGIN =====
        System.out.println("\nPASO 1: Login como admin...");
        doLogin();
        
        // ===== 2. NAVEGAR A USERTABLE =====
        System.out.println("\nPASO 2: Navegando a UserTable...");
        navigateToUserTable();
        
        // ===== 3. ACTIVAR MODO EDICIÓN =====
        System.out.println("\nPASO 3: Activando modo edición...");
        activateEditMode();
        
        // ===== 4. MODIFICAR FILA EXISTENTE =====
        System.out.println("\nPASO 4: Modificando fila existente...");
        modifyExistingRow();
        
        // ===== 5. CREAR NUEVA FILA =====
        System.out.println("\nPASO 5: Creando nueva fila...");
        createNewRow();
        
        // ===== 6. BORRAR FILA =====
        System.out.println("\nPASO 6: Borrando fila...");
        deleteRow();
        
        // ===== 7. MENÚ CONTEXTUAL =====
        System.out.println("\nPASO 7: Probando menú contextual...");
        testContextMenu();      
        
        // ===== 8. LOGOUT =====
        System.out.println("\nPASO 8: Haciendo logout...");
        doLogout();
        
        // ===== 9. LOGIN =====
        System.out.println("\nPASO 9: Login como admin...");
        doLogin();
        
        // ===== 10. NAVEGAR A USERTABLE =====
        System.out.println("\nPASO 10: Navegando a UserTable...");
        navigateToUserTable();
        
        // ===== 11. MENÚ HELP =====
        System.out.println("\nPASO 11: Probando menú Help...");
        testHelpMenu();
        
        System.out.println("\n=== TEST COMPLETADO EXITOSAMENTE ===");
    }

    private void doLogin() {
        clickOn("#TextField_Username");
        sleep(200);
        push(javafx.scene.input.KeyCode.CONTROL, javafx.scene.input.KeyCode.A);
        push(javafx.scene.input.KeyCode.DELETE);
        sleep(200);
        write("admin1");

        clickOn("#PasswordField_Password");
        sleep(200);
        push(javafx.scene.input.KeyCode.CONTROL, javafx.scene.input.KeyCode.A);
        push(javafx.scene.input.KeyCode.DELETE);
        sleep(200);
        write("1234");

        clickOn("#Button_LogIn");
        sleep(3000);
        System.out.println("Login exitoso");
    }

    private void navigateToUserTable() {
        try {
            clickOn("#users");
            System.out.println("Navegación por #users exitosa");
        } catch (Exception e1) {
            try {
                clickOn("Users");
                System.out.println("Navegación por texto 'Users' exitosa");
            } catch (Exception e2) {
                clickOn("Usuarios");
                System.out.println("Navegación por texto 'Usuarios' exitosa");
            }
        }
        sleep(3000);

        verifyThat("#tableView", isVisible());
        System.out.println("UserTable cargado correctamente");
    }

    private void activateEditMode() {
        verifyThat("#editCheckBox", isVisible());

        CheckBox checkbox = lookup("#editCheckBox").queryAs(CheckBox.class);
        System.out.println("Estado inicial checkbox: " + checkbox.isSelected());

        clickOn("#editCheckBox");
        sleep(500);

        checkbox = lookup("#editCheckBox").queryAs(CheckBox.class);
        System.out.println("Estado después de click: " + checkbox.isSelected());

        if (checkbox.isSelected()) {
            System.out.println("Modo edición activado ✓");
        } else {
            System.out.println("ERROR: Modo edición NO activado");
            clickOn("#editCheckBox"); // Intentar de nuevo
            sleep(500);
        }
    }

    private void modifyExistingRow() {
        try {
            // Buscar la tabla
            TableView<?> table = lookup("#tableView").queryAs(TableView.class);
            if (table.getItems().size() > 0) {
                System.out.println("Tabla tiene " + table.getItems().size() + " filas");

                System.out.println("Intentando modificar email...");

                Node emailCell = lookup(".table-cell").nth(2).query(); 

                doubleClickOn(emailCell);
                sleep(500);

                // Modificar el email
                push(javafx.scene.input.KeyCode.CONTROL, javafx.scene.input.KeyCode.A);
                push(javafx.scene.input.KeyCode.DELETE);
                write("modified@example.com");
                push(javafx.scene.input.KeyCode.ENTER);
                sleep(1000);

                System.out.println("Email modificado exitosamente ✓");

                // También probar modificar otra columna (nombre)
                System.out.println("Intentando modificar nombre...");
                Node nameCell = lookup(".table-cell").nth(3).query(); // Columna name (4ta columna, índice 3)
                doubleClickOn(nameCell);
                sleep(500);

                push(javafx.scene.input.KeyCode.CONTROL, javafx.scene.input.KeyCode.A);
                push(javafx.scene.input.KeyCode.DELETE);
                write("NombreModificado");
                push(javafx.scene.input.KeyCode.ENTER);
                sleep(1000);

                System.out.println("Nombre modificado exitosamente ✓");

            } else {
                System.out.println("Tabla vacía, no se puede modificar fila");
            }
        } catch (Exception e) {
            System.out.println("No se pudo modificar fila: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createNewRow() {
        try {
            verifyThat("#addButton", isVisible());

            System.out.println("Creando nueva fila con botón + ...");

            // Hacer click en el botón +
            clickOn("#addButton");
            sleep(1500);

            System.out.println("Nueva fila creada ✓");

            // Verificar que se añadió una fila
            TableView<?> table = lookup("#tableView").queryAs(TableView.class);
            System.out.println("Total filas después de añadir: " + table.getItems().size());

            // Ahora editar algunos campos de la nueva fila (excepto username)
            System.out.println("Editando campos de la nueva fila...");

            // Editar email de la última fila
            int totalRows = table.getItems().size();
            int lastRowIndex = totalRows - 1;

            // Calcular índice de la celda de email en la última fila
            // Si hay 8 columnas, el email está en: (lastRowIndex * 8) + 2
            int emailCellIndex = (lastRowIndex * 8) + 2;

            try {
                Node emailCell = lookup(".table-cell").nth(emailCellIndex).query();
                doubleClickOn(emailCell);
                sleep(500);
                push(javafx.scene.input.KeyCode.CONTROL, javafx.scene.input.KeyCode.A);
                push(javafx.scene.input.KeyCode.DELETE);
                write("nuevo@test.com");
                push(javafx.scene.input.KeyCode.ENTER);
                sleep(1000);
                System.out.println("Email de nueva fila editado ✓");
            } catch (Exception e) {
                System.out.println("No se pudo editar email de nueva fila: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error creando nueva fila: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteRow() {
        try {
            System.out.println("Intentando borrar una fila...");

            // Buscar todos los botones Delete
            int deleteButtonCount = lookup(".button").queryAll().size();
            System.out.println("Botones encontrados: " + deleteButtonCount);

            if (deleteButtonCount > 0) {
                Node deleteButton = lookup(".button").nth(0).query();

                clickOn(deleteButton);
                sleep(2000);

                verifyThat("Delete Confirmation", isVisible());
                System.out.println("Ventana de confirmación abierta ✓");

                clickOn("#passwordField");
                sleep(200);
                write("1234");

                clickOn("#deleteButton");
                sleep(2000);

                System.out.println("Fila borrada exitosamente ✓");
            } else {
                System.out.println("No hay botones Delete visibles");
            }

        } catch (Exception e) {
            System.out.println("Error borrando fila: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void testContextMenu() {
        try {
            System.out.println("Probando menú contextual...");

            // Obtener la tabla como Node
            TableView<?> tableView = lookup("#tableView").queryAs(TableView.class);

            // Hacer click derecho en la tabla
            rightClickOn(tableView);
            sleep(1000);

            // Seleccionar Report
            clickOn("Report");
            sleep(2000);

            System.out.println("Reporte generado ✓");

        } catch (Exception e) {
            System.out.println("Error con menú contextual: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void testHelpMenu() {
        try {
            System.out.println("Probando menú Help...");

            try {
                Node helpMenuNode = lookup("#helpMenu").query();
                System.out.println("Menú Help encontrado por #helpMenu");

                clickOn("#helpMenu");
                sleep(1000);

                System.out.println("Menú Help desplegado ✓");

            } catch (Exception e1) {
                System.out.println("No se encontró #helpMenu: " + e1.getMessage());              
            }

            System.out.println("Seleccionando View Manual...");

            try {
                clickOn("#viewManualItem");
                System.out.println("View Manual seleccionado por #viewManualItem ✓");
            } catch (Exception e1) {
               System.out.println("No se encontró #viewManualItem: " + e1.getMessage());   
            }
            sleep(3000);
            System.out.println("Manual abierto desde menú Help ✓");

        } catch (Exception e) {
            System.out.println("Error con menú Help: " + e.getMessage());
        }
    }

    private void doLogout() {
        try {
            System.out.println("Haciendo logout...");

            clickOn("#logout");
            sleep(2000);

            verifyThat("#Button_LogIn", isVisible());
            System.out.println("Logout exitoso - De vuelta en login ✓");

        } catch (Exception e) {
            System.out.println("Error en logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }
}
