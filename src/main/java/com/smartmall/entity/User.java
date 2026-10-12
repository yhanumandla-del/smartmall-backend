package com.smartmall.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="users")
public class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String name;
 @Column(nullable=false,unique=true) private String email;
 @JsonIgnore @Column(nullable=false) private String password;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Enums.Role role;
 private LocalDateTime createdAt;
 public User(){}
 public User(String n,String e,String p,Enums.Role r){name=n;email=e;password=p;role=r;}
 @PrePersist public void prePersist(){if(createdAt==null)createdAt=LocalDateTime.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getName(){return name;} public void setName(String v){name=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPassword(){return password;} public void setPassword(String v){password=v;}
 public Enums.Role getRole(){return role;} public void setRole(Enums.Role v){role=v;} public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
}
