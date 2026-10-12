package com.smartmall.entity;
import jakarta.persistence.*;
@Entity public class Category {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @Column(nullable=false,unique=true) String name;
 public Category(){} public Category(String name){this.name=name;}
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getName(){return name;} public void setName(String v){name=v;}
}
