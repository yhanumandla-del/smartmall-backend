package com.smartmall.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
@Entity public class CartItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @ManyToOne(optional=false) @JsonIgnore Cart cart;
 @ManyToOne(optional=false) Product product;
 @Column(nullable=false) Integer quantity;
 public CartItem(){} public CartItem(Cart c,Product p,Integer q){cart=c;product=p;quantity=q;}
 public Long getId(){return id;} public Cart getCart(){return cart;} public Product getProduct(){return product;} public void setProduct(Product v){product=v;}
 public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
}
