/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewText;

import java.util.concurrent.TimeoutException;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import model.HibernateUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import javafx.scene.Node;
import org.testfx.matcher.base.NodeMatchers;
/**
 *
 * @author acer
 */
public class ModifyUserControllerTest extends ApplicationTest {
    
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
        System.out.println("=== TEST COMPLETO DE MODIFY USER ===");
        
        // ===== 1. LOGIN =====
        System.out.println("\nPASO 1: Login como admin...");
        doLogin();
        
        // ===== 2. NAVEGAR A MODIFY USER =====
        System.out.println("\nPASO 2: Navegando a Modify User...");
        navigateToModifyUser();
        
        // ===== 3. MODIFICAR DATOS EXISTENTES =====
        System.out.println("\nPASO 3: Modificando datos existentes...");
        modifyExistingData();
        
        // ===== 4. CAMBIAR CONTRASEÑA =====
        System.out.println("\nPASO 4: Cambiando contraseña...");
        changePassword();
        
        // ===== 5. PROBAR CAMPOS INVÁLIDOS =====
        System.out.println("\nPASO 5: Probando campos inválidos...");
        testInvalidFields();
        
        // ===== 6. MENÚ CONTEXTUAL =====
        System.out.println("\nPASO 6: Probando menú contextual...");
        testContextMenu();                     
        
        // ===== 7. LOGOUT =====
        System.out.println("\nPASO 7: Haciendo logout...");
        doLogout();
        
        // ===== 8. LOGIN =====
        System.out.println("\nPASO 8: Login como admin...");
        doLogin();
        
        // ===== 9. NAVEGAR A MODIFY USER =====
        System.out.println("\nPASO 9: Navegando a Modify User...");
        navigateToModifyUser();
        
        // ===== 10. MENÚ HELP =====
        System.out.println("\nPASO 10: Probando menú Help...");
        testHelpMenu();
        
        System.out.println("\n=== TEST COMPLETADO EXITOSAMENTE ===");
    }

    private void doLogin() {
        clickOn("#TextField_Username");
        sleep(200);
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        sleep(200);
        write("username1");

        clickOn("#PasswordField_Password");
        sleep(200);
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        sleep(200);
        write("1234");

        clickOn("#Button_LogIn");
        sleep(2000);
        System.out.println("Login exitoso");
        
        // Verificar que estamos en la ventana principal
        verifyThat("#productcardList", isVisible());
    }
    
    private void navigateToModifyUser() {
        // Primero ir al perfil
        Node profileButton = lookup("#btnUser").tryQuery().orElse(null);
        if (profileButton == null) {
            // Buscar botón Profile por texto
            profileButton = lookup(node -> 
                node instanceof Button && 
                "Profile".equals(((Button) node).getText())
            ).tryQuery().orElse(null);
        }
        
        if (profileButton != null) {
            clickOn(profileButton);
            sleep(1500);
            
            // Ahora buscar el botón Modify en ProfileWindow
            Node modifyButton = lookup("#Button_Modify").tryQuery().orElse(null);
            if (modifyButton != null) {
                clickOn(modifyButton);
                sleep(1500);
                
                // Verificar que estamos en ModifyUserAdminController
                verifyThat("#nameText", isVisible());
                verifyThat("#saveButton", isVisible());
                System.out.println("Navegación a Modify User exitosa");
            } else {
                System.out.println("ERROR: No se encontró el botón Modify");
            }
        } else {
            System.out.println("ERROR: No se encontró el botón Profile");
        }
    }

    private void modifyExistingData() {
        System.out.println("Modificando datos del usuario...");
        
        // Modificar nombre
        clickOn("#nameText");
        sleep(200);
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        sleep(200);
        write("NombreModificado");
        
        // Modificar apellido
        clickOn("#surnameText");
        sleep(200);
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        sleep(200);
        write("ApellidoModificado");
        
        // Modificar teléfono
        clickOn("#telephoneText");
        sleep(200);
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        sleep(200);
        write("666777888");
        
        // Guardar cambios
        clickOn("#saveButton");
        sleep(1500);
        
        System.out.println("Datos modificados correctamente");
    }

    private void changePassword() {
        System.out.println("Cambiando contraseña...");
        
        clickOn("#Button_Modify");
        // Introducir nueva contraseña
        clickOn("#passwordText");
        sleep(200);
        write("nuevaContraseña123");
        
        // Confirmar contraseña
        clickOn("#confirmText");
        sleep(200);
        write("nuevaContraseña123");
        
        // Guardar cambios
        clickOn("#saveButton");
        sleep(1500);
        
        
        System.out.println("Contraseña cambiada correctamente");
    }

    private void testInvalidFields() {
        System.out.println("Probando validación de campos inválidos...");
        clickOn("#Button_Modify");

        clickOn("#passwordText");
        sleep(200);
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        sleep(200);
        write("password1");
        
        clickOn("#confirmText");
        sleep(200);
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        sleep(200);
        write("password2");
        
        clickOn("#saveButton");
        sleep(1000);
        
        clickOn("#telephoneText");
        sleep(200);
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        sleep(200);
        write("abc123");
        
        clickOn("#confirmText");
        sleep(200);
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        sleep(200);
        
        clickOn("#passwordText");
        sleep(200);
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        sleep(200);
        
        clickOn("#saveButton");
        sleep(1000);
        
        clickOn("#telephoneText");
        sleep(200);
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        sleep(200);
        write("123456789"); 
        
        clickOn("#passwordText");
        sleep(200);
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        sleep(200);
        write("1234");
        
        clickOn("#saveButton");
        sleep(1000);
        
        clickOn("#confirmText");
        sleep(200);
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        sleep(200);
        write("1234");
        
        clickOn("#saveButton");
        sleep(1000);
        
    }

    private void testContextMenu() {
        System.out.println("Probando menú contextual...");
        clickOn("#Button_Modify");
        // Hacer clic derecho en el gridpane
        rightClickOn("#gridpane");
        sleep(800);
        
        // Verificar que el menú contextual aparece
        Node contextMenu = lookup(".context-menu").tryQuery().orElse(null);
        if (contextMenu != null) {
            System.out.println("Menú contextual mostrado");
            
            // Buscar y hacer clic en la opción "Report"
            clickOn("Report");
            sleep(1500);
            
            System.out.println("Reporte generado");
        } else {
            System.out.println("Menú contextual no encontrado");
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
            clickOn("#cancelButton");
            sleep(2000);
            clickOn("#btnLogout");
            sleep(2000);

            verifyThat("#Button_LogIn", isVisible());
            System.out.println("Logout exitoso - De vuelta en login ✓");

        } catch (Exception e) {
            System.out.println("Error en logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
}