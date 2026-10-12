package com.smartmall.entity;
import com.fasterxml.jackson.annotation.JsonIgnore; import jakarta.persistence.*; import java.math.BigDecimal;
@Entity public class OrderItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @ManyToOne(optional=false) @JsonIgnore Order order; @ManyToOne(optional=false) Product product; Integer quantity; @Column(precision=12,scale=2) BigDecimal unitPrice;
 public OrderItem(){} public OrderItem(Order o,Product p,Integer q,BigDecimal price){order=o;product=p;quantity=q;unitPrice=price;}
 public Long getId(){return id;} public Order getOrder(){return order;} public Product getProduct(){return product;} public Integer getQuantity(){return quantity;} public BigDecimal getUnitPrice(){return unitPrice;}
}
