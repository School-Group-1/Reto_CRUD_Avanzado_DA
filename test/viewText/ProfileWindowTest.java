package viewText;

import controller.Controller;
import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.DBImplementation;
import model.Profile;
import model.HibernateUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.api.FxToolkit;
import org.testfx.matcher.base.WindowMatchers;

public class ProfileWindowTest extends ApplicationTest {

    private final Controller cont = new Controller(new DBImplementation());
    private Profile profile;

    @Override
    public void start(Stage stage) throws Exception {
        profile = cont.logIn("username1", "1234"); // perfil de prueba

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileWindow.fxml"));
        Parent root = loader.load();

        view.ProfileWindowController controller = loader.getController();
        controller.initData(profile, cont);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeClass
    public static void setUpClass() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        HibernateUtil.initializeData();
    }

    @Test
    public void usernameLabelShowsCorrectProfile() {
        Label label = lookup("#label_Username").queryAs(Label.class);
        assertEquals(profile.getUsername(), label.getText());
    }

    @Test
    public void storeButtonOpensShopWindow() {
        clickOn("#btnStore");
        verifyThat(window("Shop"), WindowMatchers.isShowing());
    }

    @Test
    public void companiesButtonOpensCompaniesWindow() {
        clickOn("#btnCompanies");
        verifyThat(window("Companies"), WindowMatchers.isShowing());
    }

    @Test
    public void logoutButtonOpensLoginWindow() {
        clickOn("#btnLogout");
        verifyThat(window("LogIn"), WindowMatchers.isShowing());
    }

    @Test
    public void modifyButtonOpensModifyWindow() {
        clickOn("#Button_Modify");
        verifyThat(window("Modify User"), WindowMatchers.isShowing());
    }

    @Test
    public void deleteButtonOpensDeleteConfirmation() {
        clickOn("#Button_Delete");
        verifyThat(window("Delete Confirmation"), WindowMatchers.isShowing());
    }
    
    //Añadir settitle al abrir ventanas
}
