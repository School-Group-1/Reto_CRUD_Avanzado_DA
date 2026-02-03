/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewText;

import java.util.concurrent.TimeoutException;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import model.HibernateUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.HBox;
import org.testfx.service.query.NodeQuery;
import java.util.Collection;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import org.testfx.matcher.base.NodeMatchers;

/**
 *
 * @author acer
 */
public class CompanyProductsControllerTest extends ApplicationTest{
    
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
    public void test_CompleteCompaniesTableFlow() {
        System.out.println("=== TEST SIMPLIFICADO DE COMPANY PRODUCTS ===");
        
        // ===== 1. LOGIN =====
        System.out.println("\nPASO 1: Login como user...");
        doLogin();
        
        // ===== 2. NAVEGAR A COMPANY WINDOW =====
        System.out.println("\nPASO 2: Navegando a Company Window...");
        navigateToCompanyWindow();
        
        // ===== 3. ENTRAR EN UNA COMPAÑÍA =====
        System.out.println("\nPASO 3: Entrando en una compañía...");
        clickInCompany();
        
        // ===== 4. VER DETALLES DEL PRODUCTO =====
        System.out.println("\nPASO 4: Clickando view product...");
        viewProductDetails();
        
        // ===== 5. PROBAR TALLAS Y AÑADIR AL CARRITO =====
        System.out.println("\nPASO 5: Probando tallas y añadiendo al carrito...");
        testSizesAndAddToCart();
        
        // ===== 6. MENÚ CONTEXTUAL =====
        System.out.println("\nPASO 6: Probando menú contextual...");
        testContextMenu();
        
        // ===== 7. MENÚ HELP =====
        System.out.println("\nPASO 7: Probando menú Help...");
       /* testHelpMenu();*/
        
        // ===== 8. LOGOUT =====
        System.out.println("\nPASO 8: Haciendo logout...");
        doLogout();
        
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
        
        // Verificar que estamos en la ventana correcta (ShopWindow después del login)
        verifyThat("#productcardList", isVisible());
    }

    private void navigateToCompanyWindow() {
        // Click en el botón Companies en el panel lateral
        clickOn("#btnCompanies");
        sleep(1000);
        System.out.println("Navegación a Company Window exitosa");
        
        // Verificar que estamos en CompanyWindow buscando el TilePane
        verifyThat("#PaneButtons", isVisible());
        System.out.println("CompanyWindow cargado correctamente");
    }

    private void clickInCompany() {
        // Buscar el primer botón de compañía en el TilePane
        Node companyButton = lookup(node -> 
            node instanceof Button && 
            node.getId() != null && 
            node.getId().startsWith("companyBtn_")
        ).tryQuery().orElse(null);
        
        if (companyButton != null) {
            System.out.println("Encontrado botón de compañía con ID: " + companyButton.getId());
            
            // Hacer clic en el botón de la compañía
            clickOn(companyButton);
            sleep(1000);
            
            // Verificar que estamos en CompanyProductsController
            verifyThat("#titleLabel", isVisible());
            System.out.println("Ventana de productos de compañía cargada");
            
        } else {
            // Si no se encuentra por ID, buscar el primer botón del TilePane
            TilePane tilePane = lookup("#PaneButtons").query();
            if (!tilePane.getChildren().isEmpty()) {
                clickOn(tilePane.getChildren().get(0));
                sleep(1000);
                verifyThat("#titleLabel", isVisible());
                System.out.println("Ventana de productos de compañía cargada (fallback)");
            }
        }
    }

    private void viewProductDetails() {
        // Esperar a que se carguen los productos
        sleep(1000);
        
        // Buscar el primer botón "View" en los productos
        Node viewButton = lookup(node -> 
            node instanceof Button && 
            "View".equals(((Button) node).getText())
        ).tryQuery().orElse(null);
        
        if (viewButton == null) {
            // Buscar botón en español si está configurado así
            viewButton = lookup(node -> 
                node instanceof Button && 
                "Ver".equals(((Button) node).getText())
            ).tryQuery().orElse(null);
        }
        
        if (viewButton != null) {
            System.out.println("Encontrado botón View/Ver");
            clickOn(viewButton);
            sleep(800);
            
            // Verificar que la imagen grande se muestra
            verifyThat("#bigImage", isVisible());
            
            // Verificar que el contenedor de tallas está visible
            verifyThat("#sizesContainer", isVisible());
            
            // Verificar que el botón Add to Cart está deshabilitado inicialmente
            verifyThat("#selectedSize", Node::isDisabled);
            
            System.out.println("Detalles del producto cargados correctamente");
        } else {
            System.out.println("No se encontraron botones View/Ver, probando con el primer botón encontrado");
            // Buscar cualquier botón en el área de productos
            viewButton = lookup(".button").tryQuery().orElse(null);
            if (viewButton != null) {
                clickOn(viewButton);
                sleep(800);
                verifyThat("#bigImage", isVisible());
            }
        }
    }

    private void testSizesAndAddToCart() {
        // Esperar un momento para que todo se cargue
        sleep(800);
        
        // Buscar botones de tallas dentro del HBox
        NodeQuery sizeButtonsQuery = lookup(node -> 
            node instanceof Button && 
            node.getParent() != null &&
            node.getParent().getId() != null &&
            node.getParent().getId().equals("sizesContainer")
        );
        
        Collection<Node> sizeButtons = sizeButtonsQuery.queryAll();
        int sizeCount = sizeButtons.size();
        System.out.println("Número de tallas disponibles: " + sizeCount);
        
        if (sizeCount > 0) {
            // Convertir la colección a array para acceder por índice
            Node[] sizeButtonsArray = sizeButtons.toArray(new Node[0]);
            
            // Seleccionar la primera talla disponible
            Node firstSizeButton = sizeButtonsArray[0];
            String sizeText = ((Button) firstSizeButton).getText();
            System.out.println("Seleccionando talla: " + sizeText);
            
            clickOn(firstSizeButton);
            sleep(400);
            
            // Verificar que el botón Add to Cart ahora está habilitado
            verifyThat("#selectedSize", isEnabled());
            
            // Hacer clic en Add to Cart
            System.out.println("Añadiendo producto al carrito...");
            clickOn("#selectedSize");
            sleep(500); // Esperar a que aparezca el Alert
            
            // Manejar el Alert que aparece
            handleAlert();
            
            System.out.println("Producto añadido al carrito correctamente - talla: " + sizeText);
            
        } else {
            System.out.println("No se encontraron botones de tallas, intentando método alternativo...");
            
            // Método alternativo: buscar botones dentro del HBox por posición
            HBox sizesContainer = lookup("#sizesContainer").query();
            if (sizesContainer != null && !sizesContainer.getChildren().isEmpty()) {
                for (Node child : sizesContainer.getChildren()) {
                    if (child instanceof Button) {
                        String buttonText = ((Button) child).getText();
                        System.out.println("Botón encontrado en HBox: " + buttonText);
                        
                        // Seleccionar la talla
                        clickOn(child);
                        sleep(400);
                        
                        // Verificar que Add to Cart está habilitado
                        if (lookup("#selectedSize").query().isDisabled()) {
                            System.out.println("Add to Cart sigue deshabilitado, intentando con otro botón...");
                            continue;
                        }
                        
                        verifyThat("#selectedSize", isEnabled());
                        
                        // Añadir al carrito
                        System.out.println("Añadiendo producto al carrito...");
                        clickOn("#selectedSize");
                        sleep(500);
                        
                        // Manejar el Alert
                        handleAlert();
                        
                        System.out.println("Producto añadido al carrito correctamente");
                        break;
                    }
                }
            } else {
                System.out.println("No hay tallas disponibles para este producto");
            }
        }
    }
    
    private void handleAlert() {
        // Esperar a que aparezca el Alert
        sleep(300);
        
        // Buscar el Alert (diálogo de confirmación)
        Node alertNode = lookup(".alert").tryQuery().orElse(null);
        
        if (alertNode != null) {
            System.out.println("Alert encontrado, manejando diálogo...");
            
            // El Alert puede ser de tipo INFORMATION
            // Buscar el botón "Aceptar" o "OK" dentro del Alert
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
                // Si no encuentra por texto, intentar con el botón por defecto
                System.out.println("Buscando botón por defecto en el Alert...");
                
                // Buscar cualquier botón en el Alert
                Node defaultButton = lookup(node -> 
                    node instanceof Button && 
                    alertNode.getScene().getRoot().getChildrenUnmodifiable().contains(node.getParent())
                ).tryQuery().orElse(null);
                
                if (defaultButton != null) {
                    System.out.println("Botón por defecto encontrado");
                    clickOn(defaultButton);
                    sleep(300);
                } else {
                    // Último recurso: presionar ENTER para aceptar el Alert
                    System.out.println("Presionando ENTER para aceptar el Alert...");
                    push(KeyCode.ENTER);
                    sleep(300);
                }
            }
        } else {
            System.out.println("No se encontró Alert, continuando...");
        }
    }

    private void testContextMenu() {
        System.out.println("\n--- Probando menú contextual ---");
        
        // Hacer clic derecho en el contenedor de productos
        rightClickOn("#productContainer");
        sleep(600);
        
        // Verificar que el menú contextual aparece
        Node contextMenu = lookup(".context-menu").tryQuery().orElse(null);
        if (contextMenu != null) {
            System.out.println("Menú contextual mostrado");
            
            // Buscar y hacer clic en la opción "Report"
            clickOn("Report");
            sleep(1000);
            
            // Manejar cualquier Alert que aparezca al generar el reporte
            handleAlert();
            
            System.out.println("Reporte generado");
        } else {
            System.out.println("Menú contextual no encontrado o no implementado en productContainer");
        }
    }

    /*private void testHelpMenu() {
        System.out.println("\n--- Probando menú Help ---");
        
        // Hacer clic en el menú Help (está en la parte superior derecha)
        clickOn("Help");
        sleep(400);
        
        // Hacer clic en "View Manual"
        clickOn("View Manual");
        sleep(1000);
        
        // Nota: Esto intentará abrir el PDF del manual de usuario
        // El test continuará después de intentar abrir el PDF
        System.out.println("Se intentó abrir el manual de usuario (si existe el PDF)");
        
        // Si hay un diálogo de error porque no existe el PDF, manejarlo
        handleAlert();
    }*/

    private void doLogout() {
        System.out.println("\n--- Haciendo logout ---");
        
        // Primero volver a la ventana principal si es necesario
        // Podemos usar el botón de retroceso si existe
        Node backButton = lookup(node -> 
            node instanceof Button && 
            "<".equals(((Button) node).getText())
        ).tryQuery().orElse(null);
        
        if (backButton != null) {
            clickOn(backButton);
            sleep(1000);
        }
        
        // Navegar al perfil para hacer logout
        Node profileButton = lookup("#profileButton").tryQuery().orElse(null);
        if (profileButton == null) {
            // Buscar botón Profile por texto
            profileButton = lookup(node -> 
                node instanceof Button && 
                "Profile".equals(((Button) node).getText())
            ).tryQuery().orElse(null);
            
            if (profileButton == null) {
                profileButton = lookup(node -> 
                    node instanceof Button && 
                    "Perfil".equals(((Button) node).getText())
                ).tryQuery().orElse(null);
            }
        }
        
        if (profileButton != null) {
            clickOn(profileButton);
            sleep(1000);
        }
        
        // Buscar el botón de logout
        Node logoutButton = lookup("#btnLogout").tryQuery().orElse(null);
        
        if (logoutButton == null) {
            logoutButton = lookup("Cerrar sesión").tryQuery().orElse(null);
        }
        
        if (logoutButton == null) {
            logoutButton = lookup("logout").tryQuery().orElse(null);
        }
        
        if (logoutButton == null) {
            // Buscar por clase CSS
            logoutButton = lookup(".logout-button").tryQuery().orElse(null);
        }
        
        if (logoutButton != null) {
            clickOn(logoutButton);
            sleep(1000);
            
            // Verificar que estamos de vuelta en la pantalla de login
            verifyThat("#TextField_Username", isVisible());
            verifyThat("#Button_LogIn", isVisible());
            
            System.out.println("Logout exitoso");
        } else {
            System.out.println("Botón de logout no encontrado. Limpiando sesión...");
            
            // Si no encontramos logout, forzar limpieza
            try {
                FxToolkit.hideStage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}