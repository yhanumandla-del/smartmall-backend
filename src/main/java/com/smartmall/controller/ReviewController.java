package com.smartmall.controller;
import com.smartmall.dto.ReviewRequest; import com.smartmall.entity.*; import com.smartmall.repository.*; import jakarta.validation.Valid; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/reviews")
public class ReviewController {
 private final ReviewRepository repo;private final UserRepository users;private final ProductRepository products;
 public ReviewController(ReviewRepository r,UserRepository u,ProductRepository p){repo=r;users=u;products=p;}
 @GetMapping("/product/{productId}") public List<Review> byProduct(@PathVariable Long productId){return repo.findByProductIdOrderByIdDesc(productId);}
 @PostMapping @PreAuthorize("hasRole('CUSTOMER')") public Review add(Authentication a,@Valid @RequestBody ReviewRequest req){User u=users.findByEmail(a.getName()).orElseThrow();Product p=products.findById(req.productId()).orElseThrow();if(repo.findByCustomerIdAndProductId(u.getId(),p.getId()).isPresent())throw new IllegalArgumentException("You already reviewed this product.");Review r=new Review();r.setCustomer(u);r.setProduct(p);r.setRating(req.rating());r.setComment(req.comment());return repo.save(r);}
}
