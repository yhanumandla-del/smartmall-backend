package com.smartmall.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity public class Product {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @Column(nullable=false) String name; @Column(length=1500) String description;
 @Column(nullable=false,precision=12,scale=2) BigDecimal price;
 @Column(nullable=false) Integer stock=0; String imageUrl;
 @ManyToOne @JsonIgnoreProperties({"owner"}) Store store; @ManyToOne Category category;
 public Product(){} public Product(String n,String d,BigDecimal p,Integer s,String i,Store st,Category c){name=n;description=d;price=p;stock=s;imageUrl=i;store=st;category=c;}
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getName(){return name;} public void setName(String v){name=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;} public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
 public Integer getStock(){return stock;} public void setStock(Integer v){stock=v;} public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
 public Store getStore(){return store;} public void setStore(Store v){store=v;} public Category getCategory(){return category;} public void setCategory(Category v){category=v;}
}
