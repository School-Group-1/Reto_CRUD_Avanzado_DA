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
    private double price;
    private Size size;

    public CartItem(int amount, Product product, Size size) {
        this.amount = amount;
        this.productName = product.getName();
        this.price = product.getPrice();
        this.size = size;
    }

    public int getAmount() {
        return amount;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public Size getSize() {
        return size;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "CartItem{" + "amount=" + amount + ", productName=" + productName + ", price=" + price + ", size=" + size + '}';
    }
    
    
}
