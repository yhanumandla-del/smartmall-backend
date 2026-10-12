package com.smartmall.entity;
import com.fasterxml.jackson.annotation.JsonIgnore; import jakarta.persistence.*;
@Entity @Table(uniqueConstraints=@UniqueConstraint(columnNames={"user_id","product_id"}))
public class Wishlist {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @ManyToOne(optional=false) @JsonIgnore User user; @ManyToOne(optional=false) Product product;
 public Wishlist(){} public Wishlist(User u,Product p){user=u;product=p;} public Long getId(){return id;} public User getUser(){return user;} public Product getProduct(){return product;}
}
