package com.smartmall.controller;
import com.smartmall.entity.Store; import com.smartmall.repository.StoreRepository; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/stores")
public class StoreController {
 private final StoreRepository stores; public StoreController(StoreRepository s){stores=s;}
 @GetMapping public List<Store> all(){return stores.findAll();}
 @GetMapping("/{id}") public Store get(@PathVariable Long id){return stores.findById(id).orElseThrow(()->new IllegalArgumentException("Store not found."));}
 @PostMapping @PreAuthorize("hasRole('ADMIN')") public Store create(@RequestBody Store s){return stores.save(s);}
 @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public Store update(@PathVariable Long id,@RequestBody Store s){Store cur=get(id);cur.setName(s.getName());cur.setDescription(s.getDescription());cur.setLocation(s.getLocation());cur.setPhone(s.getPhone());cur.setOpeningHours(s.getOpeningHours());cur.setCategory(s.getCategory());return stores.save(cur);}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public void delete(@PathVariable Long id){stores.deleteById(id);}
}
