package com.smartmall.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
@Entity @Table(name="carts") public class Cart {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @OneToOne(optional=false) @JsonIgnoreProperties({"password"}) User user;
 public Cart(){} public Cart(User u){user=u;} public Long getId(){return id;} public User getUser(){return user;} public void setUser(User v){user=v;}
}
