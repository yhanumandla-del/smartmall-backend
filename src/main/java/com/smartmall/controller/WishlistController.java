package com.smartmall.controller;
import com.smartmall.entity.*; import com.smartmall.repository.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/wishlist") @PreAuthorize("hasRole('CUSTOMER')")
public class WishlistController {
 private final WishlistRepository repo;private final UserRepository users;private final ProductRepository products;
 public WishlistController(WishlistRepository r,UserRepository u,ProductRepository p){repo=r;users=u;products=p;}
 @GetMapping public List<Wishlist> all(Authentication a){return repo.findByUserId(users.findByEmail(a.getName()).orElseThrow().getId());}
 @PostMapping("/{productId}") public Wishlist add(Authentication a,@PathVariable Long productId){User u=users.findByEmail(a.getName()).orElseThrow();Product p=products.findById(productId).orElseThrow();return repo.findByUserIdAndProductId(u.getId(),p.getId()).orElseGet(()->repo.save(new Wishlist(u,p)));}
 @DeleteMapping("/{productId}") public void remove(Authentication a,@PathVariable Long productId){User u=users.findByEmail(a.getName()).orElseThrow();repo.findByUserIdAndProductId(u.getId(),productId).ifPresent(x->repo.deleteById(x.getId()));}
}
