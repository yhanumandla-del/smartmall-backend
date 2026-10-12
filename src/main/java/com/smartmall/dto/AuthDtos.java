package com.smartmall.dto;
import jakarta.validation.constraints.*;
public final class AuthDtos {
 private AuthDtos(){}
 public record RegisterRequest(@NotBlank String name,@Email @NotBlank String email,@NotBlank @Size(min=6,max=100) String password,String accountType){}
 public record LoginRequest(@NotBlank @Email String email,@NotBlank String password){}
 public record AuthResponse(String token,String name,String email,String role){}
 public record ForgotPasswordRequest(String email){}
 public record ResetPasswordRequest(String token,String password){}
}
