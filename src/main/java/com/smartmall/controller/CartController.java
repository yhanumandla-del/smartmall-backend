package com.smartmall.controller;

import com.smartmall.dto.CartItemResponse;
import com.smartmall.dto.CartRequest;
import com.smartmall.entity.*;
import com.smartmall.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {
    private final CartRepository carts;
    private final CartItemRepository items;
    private final ProductRepository products;
    private final UserRepository users;

    public CartController(CartRepository carts, CartItemRepository items,
                          ProductRepository products, UserRepository users) {
        this.carts = carts; this.items = items; this.products = products; this.users = users;
    }

    private User currentUser(Authentication auth) {
        return users.findByEmail(auth.getName())
            .orElseThrow(() -> new IllegalStateException("Logged-in user was not found."));
    }

    private Cart cart(Authentication auth) {
        User user = currentUser(auth);
        return carts.findByUserId(user.getId()).orElseGet(() -> carts.save(new Cart(user)));
    }

    private void own(CartItem item, Authentication auth) {
        if (!item.getCart().getUser().getEmail().equalsIgnoreCase(auth.getName())) {
            throw new IllegalStateException("Unauthorized cart item.");
        }
    }

    @GetMapping
    @Transactional
    public List<CartItemResponse> get(Authentication auth) {
        return items.findByCartId(cart(auth).getId()).stream()
            .map(CartItemResponse::from)
            .toList();
    }

    @PostMapping("/items/{productId}")
    @Transactional
    public CartItemResponse add(Authentication auth, @PathVariable Long productId,
                                @Valid @RequestBody CartRequest request) {
        Cart cart = cart(auth);
        Product product = products.findById(productId)
            .orElseThrow(() -> new IllegalStateException("Product not found."));

        CartItem item = items.findByCartIdAndProductId(cart.getId(), productId)
            .orElseGet(() -> new CartItem(cart, product, 0));

        int newQuantity = item.getQuantity() + request.quantity();
        if (newQuantity > product.getStock()) {
            throw new IllegalStateException("Only " + product.getStock() + " unit(s) are available.");
        }

        item.setQuantity(newQuantity);
        return CartItemResponse.from(items.save(item));
    }

    @PutMapping("/items/{id}")
    @Transactional
    public CartItemResponse update(Authentication auth, @PathVariable Long id,
                                   @Valid @RequestBody CartRequest request) {
        CartItem item = items.findById(id)
            .orElseThrow(() -> new IllegalStateException("Cart item not found."));
        own(item, auth);

        if (request.quantity() > item.getProduct().getStock()) {
            throw new IllegalStateException("Only " + item.getProduct().getStock() + " unit(s) are available.");
        }

        item.setQuantity(request.quantity());
        return CartItemResponse.from(items.save(item));
    }

    @DeleteMapping("/items/{id}")
    @Transactional
    public void remove(Authentication auth, @PathVariable Long id) {
        CartItem item = items.findById(id)
            .orElseThrow(() -> new IllegalStateException("Cart item not found."));
        own(item, auth);
        items.delete(item);
    }
}
