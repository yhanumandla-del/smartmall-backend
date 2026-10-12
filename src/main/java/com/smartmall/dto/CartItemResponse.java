package com.smartmall.dto;

import com.smartmall.entity.CartItem;
import java.util.Map;

public record CartItemResponse(Long id, Integer quantity, Map<String, Object> product) {
    public static CartItemResponse from(CartItem item) {
        var p = item.getProduct();
        return new CartItemResponse(
            item.getId(),
            item.getQuantity(),
            Map.of(
                "id", p.getId(),
                "name", p.getName(),
                "price", p.getPrice(),
                "stock", p.getStock(),
                "imageUrl", p.getImageUrl() == null ? "" : p.getImageUrl(),
                "category", p.getCategory() == null ? Map.of() :
                    Map.of("id", p.getCategory().getId(), "name", p.getCategory().getName()),
                "store", p.getStore() == null ? Map.of() :
                    Map.of("id", p.getStore().getId(), "name", p.getStore().getName())
            )
        );
    }
}
