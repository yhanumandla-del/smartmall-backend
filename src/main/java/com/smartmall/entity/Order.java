package com.smartmall.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; import jakarta.persistence.*; import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.*;
@Entity @Table(name="orders") public class Order {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @ManyToOne(optional=false) @JsonIgnoreProperties({"password"}) User customer;
 @Enumerated(EnumType.STRING) @Column(nullable=false) Enums.OrderStatus status=Enums.OrderStatus.PLACED;
 @Column(nullable=false,precision=12,scale=2) BigDecimal totalAmount=BigDecimal.ZERO; LocalDateTime createdAt;
 @OneToMany(mappedBy="order",cascade=CascadeType.ALL,orphanRemoval=true) List<OrderItem> items=new ArrayList<>();
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now(); if(status==null)status=Enums.OrderStatus.PLACED;}
 public Long getId(){return id;} public User getCustomer(){return customer;} public void setCustomer(User v){customer=v;} public Enums.OrderStatus getStatus(){return status;} public void setStatus(Enums.OrderStatus v){status=v;}
 public BigDecimal getTotalAmount(){return totalAmount;} public void setTotalAmount(BigDecimal v){totalAmount=v;} public LocalDateTime getCreatedAt(){return createdAt;} public List<OrderItem> getItems(){return items;}
}
