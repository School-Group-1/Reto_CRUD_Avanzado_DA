/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Mosi
 */
public class ProductModifyWindowController implements Initializable {

    ImageView imageView = new ImageView(new Image("/images/LogoProjectoDin.png"));
    @FXML
    VBox vbox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        // Allow resizing
        imageView.setFitWidth(0); // important
        imageView.setFitHeight(0);

        imageView.fitWidthProperty().bind(vbox.widthProperty());
    }

}
