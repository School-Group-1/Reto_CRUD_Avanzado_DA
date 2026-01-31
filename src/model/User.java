/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 * Represents a standard user in the system. Extends Profile and adds gender and
 * card number attributes.
 */
@Entity
@Table(name = "user_")
@PrimaryKeyJoinColumn(name = "username")
public class User extends Profile {

    @Column(name = "gender")
    private String gender;

    @Column(name = "card_number")
    private String cardNumber;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Purchase> purchases;

    public User(String gender, String cardNumber, String username, String password, String email, String name, String telephone, String surname) {
        super(username, password, email, name, telephone, surname);
        this.gender = gender;
        this.cardNumber = cardNumber;
        this.purchases = new ArrayList<>();
    }

    public User() {
        super();
        this.gender = "";
        this.cardNumber = "";
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }

    public void purchaseItem(Size size, LocalDate date) {
        this.purchases.add(new Purchase(this, size, date));
    }

    @Override
    public void logIn() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "User{" + super.toString() + "gender=" + gender + ", cardNumber=" + cardNumber + '}';
    }
}
