package com.smartmall.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
@Entity public class Store {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @Column(nullable=false) String name; @Column(length=1000) String description; String location; String phone; String openingHours; @Column(length=80) String category;
 @JsonIgnore @ManyToOne User owner;
 public Store(){} public Store(String n,String d,String l,String p,String h,User o){name=n;description=d;location=l;phone=p;openingHours=h;owner=o;}
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getName(){return name;} public void setName(String v){name=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;} public String getLocation(){return location;} public void setLocation(String v){location=v;}
 public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getOpeningHours(){return openingHours;} public void setOpeningHours(String v){openingHours=v;} public String getCategory(){return category;} public void setCategory(String v){category=v;}
 public User getOwner(){return owner;} public void setOwner(User v){owner=v;}
}
