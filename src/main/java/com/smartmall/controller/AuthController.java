package com.smartmall.controller;
import com.smartmall.dto.AuthDtos.*; import com.smartmall.entity.*; import com.smartmall.repository.UserRepository; import com.smartmall.security.JwtService;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.web.bind.annotation.*; import java.util.Map;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
 public AuthController(UserRepository u,PasswordEncoder e,JwtService j){users=u;encoder=e;jwt=j;}
 @PostMapping("/register") public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest r){
  String email=r.email().trim().toLowerCase(); if(users.findByEmail(email).isPresent())return ResponseEntity.status(409).body(Map.of("message","Email is already registered."));
  String requested=r.accountType()==null||r.accountType().isBlank()?"CUSTOMER":r.accountType().trim().toUpperCase();
  if(!requested.equals("CUSTOMER")&&!requested.equals("STORE_OWNER"))return ResponseEntity.badRequest().body(Map.of("message","Choose either a customer or shop owner account."));
  Enums.Role role=Enums.Role.valueOf(requested);User u=new User(r.name().trim(),email,encoder.encode(r.password()),role);users.save(u);return ResponseEntity.ok(auth(u));
 }
 @PostMapping("/login") public ResponseEntity<?> login(@Valid @RequestBody LoginRequest r){
  User u=users.findByEmail(r.email().trim().toLowerCase()).orElse(null); if(u==null||!encoder.matches(r.password(),u.getPassword()))return ResponseEntity.status(401).body(Map.of("message","Invalid email or password."));
  return ResponseEntity.ok(auth(u));
 }
 private AuthResponse auth(User u){return new AuthResponse(jwt.generate(u.getEmail()),u.getName(),u.getEmail(),u.getRole().name());}
}
