package com.smartmall.controller;
import com.smartmall.dto.OrderStatusRequest; import com.smartmall.entity.*; import com.smartmall.repository.*; import jakarta.transaction.Transactional; import jakarta.validation.Valid; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.math.BigDecimal; import java.util.*;
@RestController @RequestMapping("/api/orders")
public class OrderController {
 private final OrderRepository orders;private final CartRepository carts;private final CartItemRepository items;private final UserRepository users;private final ProductRepository products;
 public OrderController(OrderRepository o,CartRepository c,CartItemRepository i,UserRepository u,ProductRepository p){orders=o;carts=c;items=i;users=u;products=p;}
 @GetMapping @PreAuthorize("hasRole('CUSTOMER')") public List<Order> mine(Authentication a){return orders.findByCustomerIdOrderByCreatedAtDesc(users.findByEmail(a.getName()).orElseThrow().getId());}
 @GetMapping("/all") @PreAuthorize("hasRole('ADMIN')") public List<Order> all(){return orders.findAllByOrderByCreatedAtDesc();}
 @PostMapping @Transactional @PreAuthorize("hasRole('CUSTOMER')") public Order checkout(Authentication a){
  User u=users.findByEmail(a.getName()).orElseThrow();Cart c=carts.findByUserId(u.getId()).orElseThrow(()->new IllegalStateException("Cart is empty."));List<CartItem> cis=items.findByCartId(c.getId());if(cis.isEmpty())throw new IllegalStateException("Cart is empty.");
  Order o=new Order();o.setCustomer(u);BigDecimal total=BigDecimal.ZERO;
  for(CartItem ci:cis){Product p=ci.getProduct();if(p.getStock()<ci.getQuantity())throw new IllegalStateException("Insufficient stock for "+p.getName()+".");p.setStock(p.getStock()-ci.getQuantity());var oi=new OrderItem(o,p,ci.getQuantity(),p.getPrice());o.getItems().add(oi);total=total.add(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));}
  o.setTotalAmount(total);products.saveAll(cis.stream().map(CartItem::getProduct).toList());Order saved=orders.save(o);items.deleteAll(cis);return saved;
 }
 @PatchMapping("/{id}/status") @PreAuthorize("hasRole('ADMIN')") public Order updateStatus(@PathVariable Long id,@Valid @RequestBody OrderStatusRequest req){Order o=orders.findById(id).orElseThrow();o.setStatus(req.status());return orders.save(o);}
}
