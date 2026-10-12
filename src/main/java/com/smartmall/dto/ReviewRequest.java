package com.smartmall.dto;
import jakarta.validation.constraints.*;
public record ReviewRequest(@NotNull Long productId,@Min(1) @Max(5) Integer rating,@Size(max=1200) String comment){}
