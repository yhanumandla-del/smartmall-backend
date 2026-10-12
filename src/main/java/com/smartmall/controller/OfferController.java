package com.smartmall.controller;
import com.smartmall.entity.Offer; import com.smartmall.repository.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/offers")
public class OfferController {
 private final OfferRepository offers; private final StoreRepository stores; public OfferController(OfferRepository o,StoreRepository s){offers=o;stores=s;}
 @GetMapping public List<Offer> all(){return offers.findAll();}
 @PostMapping @PreAuthorize("hasRole('ADMIN')") public Offer create(@RequestBody Offer o){if(o.getStore()!=null&&o.getStore().getId()!=null)o.setStore(stores.findById(o.getStore().getId()).orElseThrow());return offers.save(o);}
 @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public Offer update(@PathVariable Long id,@RequestBody Offer o){Offer cur=offers.findById(id).orElseThrow();cur.setTitle(o.getTitle());cur.setDescription(o.getDescription());cur.setDiscountPercent(o.getDiscountPercent());cur.setStartDate(o.getStartDate());cur.setEndDate(o.getEndDate());if(o.getStore()!=null&&o.getStore().getId()!=null)cur.setStore(stores.findById(o.getStore().getId()).orElseThrow(()->new IllegalArgumentException("Store not found.")));return offers.save(cur);}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public void delete(@PathVariable Long id){if(!offers.existsById(id))throw new IllegalArgumentException("Offer not found.");offers.deleteById(id);}
}
