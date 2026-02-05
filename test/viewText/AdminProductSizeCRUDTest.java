/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewText;

import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import model.HibernateUtil;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 *
 * @author Mosi
 */
public class AdminProductSizeCRUDTest extends ApplicationTest {

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
    public void testCreateProductSizesMain() {
        // Login
        doLogin();

        // Click create product button
        openProductCreation();

        // Fill out product form
        fillOutForm();

        // Tests the back button works in the window
        openProductCreation();
        clickOn("#backButton");

        // Verify product was created and all elements in modify window are initialized right
        CRUDProductSize();
        
        // Tests the reports context menu
        testContextMenu();
        
        // Tests the menu bar
        testHelpMenu();
        
        // Tests the logout
        doLogout();
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

    private void openProductCreation() {
        clickOn("#createItemButton");

        sleep(1000);

        verifyThat("#insertImageButton", isVisible());
        verifyThat("#stockSpinner", isVisible());
        verifyThat("#priceSpinner", isVisible());
        verifyThat("#categoryTextField", isVisible());
        verifyThat("#nameTextField", isVisible());
        verifyThat("#companyComboBox", isVisible());
        verifyThat("#descriptionTextField", isVisible());
        verifyThat("#sizeTextField", isVisible());
        verifyThat("#createSizeButton", isVisible());
        verifyThat("#sizesFlowPane", isVisible());
        verifyThat("#createProductButton", isVisible());
        verifyThat("#backButton", isVisible());
    }

    private void fillOutForm() {
        clickOn("#nameTextField");
        sleep(200);
        write("TestProduct");

        clickOn("#descriptionTextField");
        sleep(200);
        write("This is a description for a test product");

        clickOn("#stockSpinner");
        sleep(200);
        push(javafx.scene.input.KeyCode.CONTROL, javafx.scene.input.KeyCode.A);
        push(javafx.scene.input.KeyCode.DELETE);
        write("10");

        clickOn("#priceSpinner");
        sleep(200);
        push(javafx.scene.input.KeyCode.CONTROL, javafx.scene.input.KeyCode.A);
        push(javafx.scene.input.KeyCode.DELETE);
        write("39.99");

        clickOn("#categoryTextField");
        sleep(200);
        write("Shoes");

        clickOn("#companyComboBox");
        sleep(200);
        clickOn("Company 1");

        clickOn("#sizeTextField");
        sleep(200);
        write("SM");

        clickOn("#createSizeButton");

        clickOn("#sizeTextField");
        sleep(200);
        write("MD");

        clickOn("#createSizeButton");

        clickOn("#sizeTextField");
        sleep(200);
        write("L");

        clickOn("#createSizeButton");

        verifyThat("#SM", isVisible());
        verifyThat("#MD", isVisible());
        verifyThat("#L", isVisible());

        clickOn("#createProductButton");

        verifyThat("Product saved", isVisible());
        clickOn(ButtonType.OK.getText());
    }

    private void CRUDProductSize() {
        // Selects the company the test product was created on
        verifyThat("#companyCombobox", isVisible());
        clickOn("#companyCombobox");
        sleep(200);
        clickOn("Company 1");

        verifyThat("#TestProductCard", isVisible());
        verifyThat("#TestProductImage", isVisible());
        verifyThat("#TestProductPrice", isVisible());
        verifyThat("#TestProductSavePriceButton", isVisible());

        // Modifies the test product price
        clickOn("#TestProductPrice");
        sleep(200);
        push(javafx.scene.input.KeyCode.CONTROL, javafx.scene.input.KeyCode.A);
        push(javafx.scene.input.KeyCode.DELETE);
        write("10");
        push(javafx.scene.input.KeyCode.ENTER);
        clickOn("#TestProductSavePriceButton");

        verifyThat("Product price modified", isVisible());
        clickOn(ButtonType.OK.getText());

        // Modifies a size from the test product
        clickOn("#TestProductCard");
        verifyThat("#linechart", isVisible());
        verifyThat("#SMButton", isVisible());
        verifyThat("#MDButton", isVisible());
        verifyThat("#LButton", isVisible());
        verifyThat("#addSizeButton", isVisible());
        verifyThat("#sizeTextField", isVisible());
        verifyThat("#stockCountSpinner", isVisible());
        verifyThat("#saveSizeButton", isVisible());
        verifyThat("#deleteSizeButton", isVisible());

        clickOn("#MDButton");
        sleep(200);

        clickOn("#sizeTextField");
        sleep(200);
        push(javafx.scene.input.KeyCode.CONTROL, javafx.scene.input.KeyCode.A);
        push(javafx.scene.input.KeyCode.DELETE);
        write("M");

        clickOn("#stockCountSpinner");
        sleep(200);
        push(javafx.scene.input.KeyCode.CONTROL, javafx.scene.input.KeyCode.A);
        push(javafx.scene.input.KeyCode.DELETE);
        write("5");
        push(javafx.scene.input.KeyCode.ENTER);

        clickOn("#saveSizeButton");
        verifyThat("Size modification", isVisible());
        clickOn(ButtonType.OK.getText());

        verifyThat("#MButton", isVisible());

        // Deletes the size
        clickOn("#deleteSizeButton");
        verifyThat("Size deleted.", isVisible());
        clickOn(ButtonType.OK.getText());

        // Deletes the product
        clickOn("#deleteItemButton");
        verifyThat("Product deletion", isVisible());
        clickOn(ButtonType.OK.getText());
    }

    private void testContextMenu() {
        try {
            rightClickOn("#mainGridPane");
            sleep(200);

            clickOn("Report");
            sleep(200);
        } catch (Exception e) {
            System.out.println("Error con menú contextual: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void testHelpMenu() {
        Node helpMenuNode = lookup("#helpMenu").query();

        clickOn("#helpMenu");
        sleep(200);

        clickOn("#viewManualItem");

        sleep(200);
    }

    private void doLogout() {
        clickOn("#logout");
        sleep(2000);

        verifyThat("#Button_LogIn", isVisible());
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }

}
