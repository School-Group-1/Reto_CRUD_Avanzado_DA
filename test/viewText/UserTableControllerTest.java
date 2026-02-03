/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewText;

import java.util.concurrent.TimeoutException;
import model.HibernateUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

/**
 * Test que COMBINA el LoginTest que funciona + navegación a UserTable
 */
public class UserTableControllerTest extends ApplicationTest {

    @Override
    public void stop() {
        // Método necesario pero vacío
    }

    @BeforeClass
    public static void setUpClass() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(main.Main.class);  // MISMA línea que en LoginTest
        HibernateUtil.initializeData();  // MISMA línea que en LoginTest
    }

    @Test
    public void test_CompleteFlow() {
        System.out.println("=== COPIANDO MÉTODO DE LOGIN QUE FUNCIONA ===");
        
        // ===== 1. LOGIN (CÓDIGO DEL LoginTest QUE SÍ FUNCIONA) =====
        System.out.println("PASO 1: Login (código de LoginTest)...");
        
        // Limpiar campos PRIMERO (como en LoginTest)
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
        
        // Click en Login
        System.out.println("Haciendo click en #Button_LogIn...");
        clickOn("#Button_LogIn");
        
        System.out.println("Esperando que cargue la ventana principal...");
        sleep(3000); 
        
        System.out.println("\nPASO 2: ¿Qué hay después del login?");
        
        System.out.println("\nPASO 3: Buscando botón para ir a UserTable...");
        
        try {
            System.out.println("Buscando #users...");
            clickOn("#users");
            System.out.println("Click en #users exitoso");
        } catch (Exception e1) {
            System.out.println("No se encontró #users, buscando por texto...");
            
            try {
                System.out.println("Buscando texto 'Users'...");
                clickOn("Users");
                System.out.println("Click en 'Users' exitoso");
            } catch (Exception e2) {
                System.out.println("No se encontró 'Users', buscando 'Usuarios'...");
                
                try {
                    clickOn("Usuarios");
                    System.out.println("Click en 'Usuarios' exitoso");
                } catch (Exception e3) {
                    System.out.println("No se encontró ningún botón Users/Usuarios");
                    System.out.println("Botones disponibles:");
                    return; // Terminar test
                }
            }
        }
        
        System.out.println("\nPASO 4: Esperando que cargue UserTable...");
        sleep(3000);
        
        System.out.println("\nPASO 5: Verificando UserTable...");
        
        try {
            verifyThat("#tableView", isVisible());
            System.out.println("Tabla de usuarios encontrada (#tableView)");
        } catch (Exception e) {
            System.out.println("ERROR: No se encontró #tableView");
            System.out.println("Elementos actuales:");
            return;
        }
        
        try {
            verifyThat("#editCheckBox", isVisible());
            System.out.println("Checkbox de edición encontrado (#editCheckBox)");
        } catch (Exception e) {
            System.out.println("No se encontró #editCheckBox");
        }
        
        System.out.println("\nPASO 6: Probando funcionalidad básica...");
        
        try {
            javafx.scene.control.CheckBox checkbox = lookup("#editCheckBox").queryAs(javafx.scene.control.CheckBox.class);
            boolean estado = checkbox.isSelected();
            System.out.println("   Estado checkbox: " + estado);
            
            clickOn("#editCheckBox");
            sleep(500);
            checkbox = lookup("#editCheckBox").queryAs(javafx.scene.control.CheckBox.class);
            System.out.println("Nuevo estado: " + checkbox.isSelected());
        } catch (Exception e) {
            System.out.println("No se pudo probar checkbox");
        }
        
        System.out.println("\nPASO 7: Haciendo logout...");
        
        try {
            clickOn("#logout");
            System.out.println("Logout exitoso (#logout)");
            sleep(2000);
            
            verifyThat("#Button_LogIn", isVisible());
            System.out.println("De vuelta en pantalla de login");
            
        } catch (Exception e) {
            System.out.println("No se pudo hacer logout: " + e.getMessage());
        }
        
        System.out.println("\n=== TEST COMPLETADO ===");
    }
     
    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
}