package com.smartmall.controller;
import com.smartmall.repository.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/admin") @PreAuthorize("hasRole('ADMIN')")
public class AdminController {
 private final UserRepository users;private final StoreRepository stores;private final ProductRepository products;private final OrderRepository orders;
 public AdminController(UserRepository u,StoreRepository s,ProductRepository p,OrderRepository o){users=u;stores=s;products=p;orders=o;}
 @GetMapping("/stats") public Map<String,Long> stats(){return Map.of("users",users.count(),"stores",stores.count(),"products",products.count(),"orders",orders.count());}
 @GetMapping("/users") public List<?> allUsers(){return users.findAll();}
}
