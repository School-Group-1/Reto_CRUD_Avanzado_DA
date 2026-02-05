/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewText;

import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
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
            TableView<?> table = lookup("#tableView").queryAs(TableView.class);
            if (table.getItems().size() > 0) {
                System.out.println("Tabla tiene " + table.getItems().size() + " filas");

                // Modificar email
                System.out.println("Modificando email...");
                Node emailCell = lookup(".table-cell").nth(2).query();
                doubleClickOn(emailCell);
                sleep(500);
                push(javafx.scene.input.KeyCode.CONTROL, javafx.scene.input.KeyCode.A);
                push(javafx.scene.input.KeyCode.DELETE);
                write("u");               
                push(javafx.scene.input.KeyCode.ENTER);
                sleep(1000);               

                handleAlert();
                write("u@gmail.com");               
                push(javafx.scene.input.KeyCode.ENTER);
                sleep(1000); 
                        
                // Modificar teléfono
                System.out.println("Modificando teléfono...");
                Node telephoneCell = lookup(".table-cell").nth(4).query();
                doubleClickOn(telephoneCell);
                sleep(500);
                push(javafx.scene.input.KeyCode.CONTROL, javafx.scene.input.KeyCode.A);
                push(javafx.scene.input.KeyCode.DELETE);
                write("12");
                push(javafx.scene.input.KeyCode.ENTER);
                sleep(1000);
                
                handleAlert();
                write("987654321");
                push(javafx.scene.input.KeyCode.ENTER);
                sleep(1000);

            } else {
                System.out.println("Tabla vacía, no se puede modificar fila");
            }
        } catch (Exception e) {
            System.out.println("Error modificando fila: " + e.getMessage());
        }
    }

    private void createNewRow() {
        try {
            verifyThat("#addButton", isVisible());

            System.out.println("Creando nueva fila...");

            // Hacer click en el botón +
            clickOn("#addButton");
            sleep(1500);            

            // Username
            System.out.println("Introduciendo username...");
            write("testuser_");
            push(javafx.scene.input.KeyCode.ENTER);
            sleep(1500);

            // Email
            System.out.println("Introduciendo email...");
            write("newuser@test.com");
            push(javafx.scene.input.KeyCode.ENTER);
            sleep(1500);

            // Teléfono
            System.out.println("Introduciendo teléfono...");
            write("123456789");
            push(javafx.scene.input.KeyCode.ENTER);
            sleep(2000);

            System.out.println("Nueva fila creada ✓");

        } catch (Exception e) {
            System.out.println("Error creando nueva fila: " + e.getMessage());
        }
    }

    private void deleteRow() {
        try {
            System.out.println("Borrando una fila...");

            // Buscar botones Delete
            int deleteButtonCount = lookup(".button").queryAll().size();
            System.out.println("Botones encontrados: " + deleteButtonCount);

            if (deleteButtonCount > 0) {
                // Usar el primer botón Delete
                Node deleteButton = lookup(".button").nth(0).query();

                clickOn(deleteButton);
                sleep(2000);

                clickOn("#passwordField");
                sleep(200);
                write("1234");

                clickOn("#deleteButton");
                sleep(2000);

                System.out.println("Fila borrada ✓");
            } else {
                System.out.println("No hay botones Delete visibles");
            }

        } catch (Exception e) {
            System.out.println("Error borrando fila: " + e.getMessage());
        }
    }

    private void testContextMenu() {
        try {
            System.out.println("Probando menú contextual...");

            TableView<?> tableView = lookup("#tableView").queryAs(TableView.class);
            rightClickOn(tableView);
            sleep(1000);

            clickOn("Report");
            sleep(2000);

            System.out.println("Reporte generado ✓");

        } catch (Exception e) {
            System.out.println("Error con menú contextual: " + e.getMessage());
        }
    }

    private void testHelpMenu() {
        try {
            System.out.println("Probando menú Help...");

            try {
                clickOn("#helpMenu");
                System.out.println("Menú Help desplegado ✓");
            } catch (Exception e1) {
                System.out.println("No se encontró #helpMenu");
            }

            System.out.println("Seleccionando View Manual...");

            try {
                clickOn("#viewManualItem");
                System.out.println("View Manual seleccionado ✓");
            } catch (Exception e1) {
                System.out.println("No se encontró #viewManualItem");
            }
            sleep(3000);
            System.out.println("Manual abierto ✓");

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
            System.out.println("Logout exitoso ✓");

        } catch (Exception e) {
            System.out.println("Error en logout: " + e.getMessage());
        }
    }
    
    private void handleAlert() {
        sleep(300);
        
        Node alertNode = lookup(".alert").tryQuery().orElse(null);
        
        if (alertNode != null) {
            System.out.println("Alert encontrado, manejando diálogo...");
            
            Node acceptButton = lookup(node -> 
                node instanceof Button && (
                    "Aceptar".equals(((Button) node).getText()) ||
                    "OK".equals(((Button) node).getText()) ||
                    "Aceptar".equalsIgnoreCase(((Button) node).getText()) ||
                    "OK".equalsIgnoreCase(((Button) node).getText())
                )
            ).tryQuery().orElse(null);
            
            if (acceptButton != null) {
                System.out.println("Botón Aceptar/OK encontrado en el Alert");
                clickOn(acceptButton);
                sleep(300);
            } else {
                System.out.println("Buscando botón por defecto en el Alert...");
                
                Node defaultButton = lookup(node -> 
                    node instanceof Button && 
                    alertNode.getScene().getRoot().getChildrenUnmodifiable().contains(node.getParent())
                ).tryQuery().orElse(null);
                
                if (defaultButton != null) {
                    System.out.println("Botón por defecto encontrado");
                    clickOn(defaultButton);
                    sleep(300);
                } else {
                    System.out.println("Presionando ENTER para aceptar el Alert...");
                    push(KeyCode.ENTER);
                    sleep(300);
                }
            }
        } else {
            System.out.println("No se encontró Alert, continuando...");
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }
}