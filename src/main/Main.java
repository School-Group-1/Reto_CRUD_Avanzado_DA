package main;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.HibernateUtil;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utilidades.LogConfig;

public class Main extends Application {

    /**
     * Starts the JavaFX application by loading the login window.
     *
     * @param stage the primary stage for this application
     * @throws Exception if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/LogInWindow.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Login Application");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main method to launch the JavaFX application and load test data into the
     * database.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        HibernateUtil.initializeData();
        try {
            LogConfig.setup();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            launch(args);
        } finally {
            HibernateUtil.closeSessionFactory();
        }
    }

}
