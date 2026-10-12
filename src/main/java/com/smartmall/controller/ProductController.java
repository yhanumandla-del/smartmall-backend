package com.smartmall.controller;
import com.smartmall.entity.Product; import com.smartmall.repository.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/products")
public class ProductController {
 private final ProductRepository products; private final StoreRepository stores; private final CategoryRepository categories;
 public ProductController(ProductRepository p,StoreRepository s,CategoryRepository c){products=p;stores=s;categories=c;}
 @GetMapping public List<Product> all(@RequestParam(required=false) String search,@RequestParam(required=false) Long categoryId,@RequestParam(required=false) Long storeId){
  List<Product> list=(search==null||search.isBlank())?products.findAll():products.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search,search);
  return list.stream().filter(p->categoryId==null||(p.getCategory()!=null&&categoryId.equals(p.getCategory().getId()))).filter(p->storeId==null||(p.getStore()!=null&&storeId.equals(p.getStore().getId()))).toList();
 }
 @GetMapping("/{id}") public Product get(@PathVariable Long id){return products.findById(id).orElseThrow(()->new IllegalArgumentException("Product not found."));}
 @PostMapping @PreAuthorize("hasRole('ADMIN')") public Product create(@RequestBody Product p){
  if(p.getStore()==null||p.getStore().getId()==null)throw new IllegalArgumentException("Store is required.");
  p.setStore(stores.findById(p.getStore().getId()).orElseThrow(()->new IllegalArgumentException("Store not found.")));
  if(p.getCategory()!=null&&p.getCategory().getId()!=null)p.setCategory(categories.findById(p.getCategory().getId()).orElseThrow(()->new IllegalArgumentException("Category not found.")));
  if(p.getPrice()==null||p.getPrice().signum()<0)throw new IllegalArgumentException("Price must be zero or greater.");
  if(p.getStock()==null||p.getStock()<0)throw new IllegalArgumentException("Stock cannot be negative.");
  return products.save(p);
 }
 @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public Product update(@PathVariable Long id,@RequestBody Product p){
  Product cur=get(id);cur.setName(p.getName());cur.setDescription(p.getDescription());cur.setPrice(p.getPrice());cur.setStock(p.getStock());cur.setImageUrl(p.getImageUrl());
  if(p.getCategory()!=null&&p.getCategory().getId()!=null)cur.setCategory(categories.findById(p.getCategory().getId()).orElseThrow()); return products.save(cur);
 }
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public void delete(@PathVariable Long id){if(!products.existsById(id))throw new IllegalArgumentException("Product not found.");products.deleteById(id);}
}
