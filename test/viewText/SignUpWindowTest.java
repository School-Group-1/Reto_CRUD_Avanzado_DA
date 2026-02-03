package viewText;

import controller.Controller;
import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.DBImplementation;
import model.HibernateUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import view.SignUpWindowController;

public class SignUpWindowTest extends ApplicationTest {

    private final Controller cont = new Controller(new DBImplementation());
    private SignUpWindowController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignUpWindow.fxml"));
        Parent root = loader.load();

        controller = loader.getController();
        controller.initData(cont); // Controller REAL (como en CompanyWindowTest)

        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Inicializa JavaFX y la BD antes de los tests
     */
    @BeforeClass
    public static void setUpClass() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        HibernateUtil.initializeData();
    }

    // ---------------- VALIDACIONES ----------------

    @Test
    public void signUpWithEmptyFieldsShowsError() {
        clickOn("#buttonSignUp");

        verifyThat("#errorLbl", isVisible());
        verifyThat("#errorLbl", hasText("All fields must be filled"));
    }

    @Test
    public void signUpWithoutGenderShowsError() {
        fillCommonFields();
        clickOn("#buttonSignUp");

        verifyThat("#errorLbl", hasText("You must select a gender"));
    }

    @Test
    public void signUpWithInvalidEmailShowsError() {
        fillCommonFields();
        clickOn("#textFieldEmail").eraseText(20).write("invalidEmail");
        clickOn("#rButtonM");
        clickOn("#buttonSignUp");

        verifyThat("#errorLbl", hasText("Invalid email format"));
    }

    @Test
    public void signUpWithInvalidTelephoneShowsError() {
        fillCommonFields();
        clickOn("#textFieldTelephone").eraseText(20).write("12");
        clickOn("#rButtonM");
        clickOn("#buttonSignUp");

        verifyThat("#errorLbl", hasText("Telephone must have exactly 9 digits"));
    }

    @Test
    public void signUpWithInvalidCardNumberShowsError() {
        fillCommonFields();
        clickOn("#textFieldCardN").eraseText(20).write("123");
        clickOn("#rButtonM");
        clickOn("#buttonSignUp");

        verifyThat("#errorLbl", hasText("Card number must have exactly 16 digits"));
    }

    // ---------------- REGISTRO CORRECTO ----------------

    @Test
    public void validSignUpOpensNextWindow() {
        fillCommonFields();
        clickOn("#rButtonM");
        clickOn("#buttonSignUp");

        // Si el usuario NO es Admin → ShopWindow
        // Si es Admin → ProductModifyWindow
        // Solo comprobamos que el errorLbl esté vacío
        assertEquals("", controller.errorLbl.getText());
    }

    // ---------------- MÉTODO AUXILIAR ----------------

    private void fillCommonFields() {
        clickOn("#textFieldUsername").write("testuser_fx");
        clickOn("#textFieldName").write("Test");
        clickOn("#textFieldSurname").write("User");
        clickOn("#textFieldEmail").write("testfx@email.com");
        clickOn("#textFieldTelephone").write("123456789");
        clickOn("#textFieldCardN").write("1234567812345678");
        clickOn("#textFieldPassword").write("1234");
    }
}
