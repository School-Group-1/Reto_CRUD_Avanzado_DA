/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewText;

import controller.Controller;
import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.DBImplementation;
import model.HibernateUtil;
import model.Profile;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import view.ShopWindowController;

/**
 *
 * @author 2dami
 */
public class ShopWindowTest  extends ApplicationTest{
      private final Controller cont = new Controller(new DBImplementation());
    private Profile profile;
    
    @Override
    public void start(Stage stage) throws Exception {
        profile = cont.logIn("username1", "1234");
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ShopWindow.fxml"));
        Parent root = loader.load();

        ShopWindowController controller = loader.getController();
        controller.initData(profile, cont); // o cont real

        stage.setScene(new Scene(root));
        stage.show();
    }
     @BeforeClass
    public static void setUpClass() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        HibernateUtil.initializeData();
    }
    @Test
    public void Resize() {
           }
}
