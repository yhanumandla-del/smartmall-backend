package com.smartmall.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; import jakarta.persistence.*; import java.math.BigDecimal; import java.time.LocalDate;
@Entity public class Offer {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Column(nullable=false) String title; String description;
 @Column(precision=5,scale=2) BigDecimal discountPercent; LocalDate startDate; LocalDate endDate;
 @ManyToOne @JsonIgnoreProperties({"owner"}) Store store;
 public Offer(){} public Long getId(){return id;} public void setId(Long v){id=v;} public String getTitle(){return title;} public void setTitle(String v){title=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public BigDecimal getDiscountPercent(){return discountPercent;} public void setDiscountPercent(BigDecimal v){discountPercent=v;} public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;} public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;}
 public Store getStore(){return store;} public void setStore(Store v){store=v;}
}
