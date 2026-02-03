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

    public CartItem(int amount, String productName, double price, Size size) {
        this.amount = amount;
        this.productName = productName;
        this.price = price;
        this.size = size;
    }
    
    
}
