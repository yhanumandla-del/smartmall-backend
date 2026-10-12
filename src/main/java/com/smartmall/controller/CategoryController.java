package com.smartmall.controller;
import com.smartmall.entity.Category; import com.smartmall.repository.CategoryRepository; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/categories")
public class CategoryController {
 private final CategoryRepository repo; public CategoryController(CategoryRepository r){repo=r;}
 @GetMapping public List<Category> all(){return repo.findAll();}
}
