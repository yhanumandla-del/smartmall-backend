package com.smartmall.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; import jakarta.persistence.*;
@Entity @Table(uniqueConstraints=@UniqueConstraint(columnNames={"customer_id","product_id"})) public class Review {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @ManyToOne(optional=false) @JsonIgnoreProperties({"password"}) User customer; @ManyToOne(optional=false) Product product;
 @Column(nullable=false) Integer rating; @Column(length=1200) String comment;
 public Review(){} public Long getId(){return id;} public User getCustomer(){return customer;} public void setCustomer(User v){customer=v;} public Product getProduct(){return product;} public void setProduct(Product v){product=v;}
 public Integer getRating(){return rating;} public void setRating(Integer v){rating=v;} public String getComment(){return comment;} public void setComment(String v){comment=v;}
}
