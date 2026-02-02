/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewText;

import controller.Controller;
import java.util.concurrent.TimeoutException;
import model.HibernateUtil;
import org.testfx.framework.junit.ApplicationTest;
import javafx.scene.layout.TilePane;
import model.DBImplementation;
import model.Profile;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.testfx.api.FxToolkit;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.matcher.base.WindowMatchers;
import view.CompanyWindowController;


/**
 *
 * @author kssal
 */
public class CompanyWindowTest extends ApplicationTest{
    
    private final Controller cont = new Controller(new DBImplementation());
    private Profile profile;
    
    @Override
    public void start(Stage stage) throws Exception {
        profile = cont.logIn("admin1", "1234");
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CompanyWindow.fxml"));
        Parent root = loader.load();

        CompanyWindowController controller = loader.getController();
        controller.initData(profile, cont); // o cont real

        stage.setScene(new Scene(root));
        stage.show();
    }
    
    /**
     * Inicializa la aplicación JavaFX antes de los tests.
     */
    @BeforeClass
    public static void setUpClass() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        HibernateUtil.initializeData();
    }
    
    @Test
    public void companiesAreLoadedInTilePane() {
        TilePane pane = lookup("#PaneButtons").queryAs(TilePane.class);

        assertNotNull(pane);
        assertTrue(pane.getChildren().size() > 0);
    }
    
    @Test
    public void clickingCompanyButtonWorks() {
        clickOn("Nike");

        verifyThat(window("CompanyProducts"), WindowMatchers.isShowing());
        verifyThat("#selectedCompanyLabel", hasText("Nike"));
    }
}
