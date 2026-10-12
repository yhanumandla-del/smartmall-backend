package com.smartmall.dto;
import jakarta.validation.constraints.Min;
public record CartRequest(@Min(1) Integer quantity){}
