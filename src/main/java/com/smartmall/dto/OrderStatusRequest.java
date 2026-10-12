package com.smartmall.dto;
import com.smartmall.entity.Enums;
import jakarta.validation.constraints.NotNull;
public record OrderStatusRequest(@NotNull Enums.OrderStatus status){}
