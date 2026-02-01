package main;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import model.HibernateUtil;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Admin;
import model.Company;
import model.Product;
import model.Purchase;
import model.Size;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
     * Generates random purchases for a user and size within a given date range.
     *
     * @param user the user making the purchases
     * @param size the size being purchased
     * @param startDate the first date in the range (inclusive)
     * @param endDate the last date in the range (inclusive)
     * @param maxPerDay the maximum number of purchases per day
     * @param session the Hibernate session used to save the purchases
     */
    public static void generateDailyPurchases(
            User user,
            Size size,
            LocalDate startDate,
            LocalDate endDate,
            int maxPerDay,
            Session session
    ) {
        Random random = new Random();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            int purchasesToday = random.nextInt(maxPerDay + 1);

            for (int i = 0; i < purchasesToday; i++) {
                Purchase p = new Purchase(user, size, date);
                user.getPurchases().add(p);
                session.save(p);
            }
        }
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
            launch(args);
        } finally {
            HibernateUtil.closeSessionFactory();
        }
    }

}
