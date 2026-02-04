/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author 2dami
 */
public class CartItem {
    private int amount;
    private String productName;
    private String price;
    private String size;
    private final int id;

    public CartItem( Product product, Size size) {
        this.amount = 1;
        this.productName = product.getName();
        this.price = product.getPrice()+" €";
        this.size = size.getLabel();
        this.id = size.getSizeId();

    }

    public int getId() {
        return id;
    }
    
    public int getAmount() {
        return amount;
    }

    public String getProductName() {
        return productName;
    }

    public String getPrice() {
        return price;
    }

    public String getSize() {
        return size;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "CartItem{" + "amount=" + amount + ", productName=" + productName + ", price=" + price + ", size=" + size + '}';
    }
    
    
}
