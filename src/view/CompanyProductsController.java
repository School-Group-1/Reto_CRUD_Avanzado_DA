/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.ClassDAO;
import model.DBImplementation;
import model.Product;
import javafx.scene.image.Image;
import javafx.geometry.Insets;
import model.Company;

/**
 * FXML Controller class
 *
 * @author acer
 */
public class CompanyProductsController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private VBox productContainer;

    private ClassDAO dao = new DBImplementation();

    @FXML
    private ImageView bigImage;

    private Company company;
    
    @FXML
    private Label sizeErrorLabel;
    
    @FXML
    private Button selectedSize;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadProducts();
    }

    public void setCompany(Company company) {
        this.company = company;
        loadProducts();
    }
    

    private void showProductDetail(Product product) {
        bigImage.setImage(
                new Image(getClass().getResourceAsStream(product.getImage()))
        );
    }

    private HBox createProductCard(Product product) {

        ImageView img = new ImageView(
                new Image(getClass().getResourceAsStream(product.getImage()))
        );
        img.setFitWidth(100);
        img.setPreserveRatio(true);

        Label name = new Label(product.getName());
        Label price = new Label(product.getPrice() + " €");

        Button viewBtn = new Button("View");
        viewBtn.setOnAction(e -> showProductDetail(product));

        VBox info = new VBox(name, price, viewBtn);
        info.setSpacing(5);

        HBox card = new HBox(img, info);
        card.setSpacing(15);
        card.setPadding(new Insets(10));

        return card;
    }

    private void loadProducts() {
        List<Product> products = dao.findProductsByCompany(company);
        productContainer.getChildren().clear();

        for (Product p : products) {
            productContainer.getChildren().add(createProductCard(p));
        }
    }

}
