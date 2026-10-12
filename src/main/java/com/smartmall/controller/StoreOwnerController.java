package com.smartmall.controller;

import com.smartmall.entity.*;
import com.smartmall.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/owner")
@PreAuthorize("hasRole('STORE_OWNER')")
public class StoreOwnerController {
    private final UserRepository users;
    private final StoreRepository stores;
    private final ProductRepository products;
    private final CategoryRepository categories;
    private final OfferRepository offers;
    private final OrderRepository orders;

    public StoreOwnerController(UserRepository users, StoreRepository stores, ProductRepository products,
                                CategoryRepository categories, OfferRepository offers, OrderRepository orders) {
        this.users = users; this.stores = stores; this.products = products;
        this.categories = categories; this.offers = offers; this.orders = orders;
    }

    private User currentUser(Authentication auth) {
        return users.findByEmail(auth.getName()).orElseThrow(() -> new AccessDeniedException("Seller account not found."));
    }

    private Store ownedStore(Long id, Long ownerId) {
        Store s = stores.findById(id).orElseThrow(() -> new IllegalArgumentException("Store not found."));
        if (s.getOwner() == null || !ownerId.equals(s.getOwner().getId()))
            throw new AccessDeniedException("You can only manage stores assigned to your seller account.");
        return s;
    }

    private Product ownedProduct(Long id, Long ownerId) {
        Product p = products.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found."));
        if (p.getStore() == null || p.getStore().getOwner() == null || !ownerId.equals(p.getStore().getOwner().getId()))
            throw new AccessDeniedException("You can only manage products from your own stores.");
        return p;
    }

    private void validateStoreFields(Store s) {
        if (s.getName() == null || s.getName().isBlank()) throw new IllegalArgumentException("Store name is required.");
        if (s.getCategory() == null || s.getCategory().isBlank()) throw new IllegalArgumentException("Choose a department.");
        if (s.getDescription() == null || s.getDescription().isBlank()) throw new IllegalArgumentException("Store description is required.");
        s.setOpeningHours(null); // SmartMall is online; no mall-wide physical opening schedule is used.
    }

    @GetMapping("/stores")
    public List<Store> myStores(Authentication auth) { return stores.findByOwnerId(currentUser(auth).getId()); }

    @PostMapping("/stores")
    public Store createStore(@RequestBody Store incoming, Authentication auth) {
        User owner = currentUser(auth);
        validateStoreFields(incoming);
        incoming.setId(null); incoming.setOwner(owner);
        return stores.save(incoming);
    }

    @PutMapping("/stores/{id}")
    public Store updateStore(@PathVariable Long id, @RequestBody Store incoming, Authentication auth) {
        User owner = currentUser(auth); Store s = ownedStore(id, owner.getId());
        validateStoreFields(incoming);
        s.setName(incoming.getName().trim()); s.setCategory(incoming.getCategory());
        s.setDescription(incoming.getDescription()); s.setLocation(incoming.getLocation()); s.setPhone(incoming.getPhone());
        s.setOpeningHours(null);
        return stores.save(s);
    }

    @GetMapping("/products")
    public List<Product> myProducts(Authentication auth) { return products.findByStoreOwnerId(currentUser(auth).getId()); }

    @PostMapping("/products")
    public Product createProduct(@RequestBody Product incoming, Authentication auth) {
        User owner = currentUser(auth);
        if (incoming.getName() == null || incoming.getName().isBlank()) throw new IllegalArgumentException("Product name is required.");
        if (incoming.getDescription() == null || incoming.getDescription().isBlank()) throw new IllegalArgumentException("Product description is required.");
        validatePriceAndStock(incoming);
        Long storeId = incoming.getStore() == null ? null : incoming.getStore().getId();
        Store store;
        if (storeId != null) store = ownedStore(storeId, owner.getId());
        else store = stores.findByOwnerId(owner.getId()).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Create a store profile before adding products."));
        incoming.setId(null); incoming.setName(incoming.getName().trim()); incoming.setStore(store);
        incoming.setCategory(resolveCategory(incoming.getCategory()));
        return products.save(incoming);
    }

    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product incoming, Authentication auth) {
        User owner = currentUser(auth); Product p = ownedProduct(id, owner.getId());
        if (incoming.getName() == null || incoming.getName().isBlank()) throw new IllegalArgumentException("Product name is required.");
        if (incoming.getDescription() == null || incoming.getDescription().isBlank()) throw new IllegalArgumentException("Product description is required.");
        validatePriceAndStock(incoming);
        p.setName(incoming.getName().trim()); p.setDescription(incoming.getDescription()); p.setPrice(incoming.getPrice()); p.setStock(incoming.getStock());
        if (incoming.getImageUrl() != null) p.setImageUrl(incoming.getImageUrl());
        if (incoming.getCategory() != null && incoming.getCategory().getId() != null) p.setCategory(resolveCategory(incoming.getCategory()));
        return products.save(p);
    }

    @DeleteMapping("/products/{id}")
    public Map<String, String> deleteProduct(@PathVariable Long id, Authentication auth) {
        Product p = ownedProduct(id, currentUser(auth).getId()); products.delete(p);
        return Map.of("message", "Product deleted.");
    }

    private void validatePriceAndStock(Product p) {
        if (p.getPrice() == null || p.getPrice().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Price must be zero or greater.");
        if (p.getStock() == null || p.getStock() < 0) throw new IllegalArgumentException("Stock cannot be negative.");
    }

    private Category resolveCategory(Category c) {
        if (c == null || c.getId() == null) return null;
        return categories.findById(c.getId()).orElseThrow(() -> new IllegalArgumentException("Category not found."));
    }

    @GetMapping("/offers")
    public List<Offer> myOffers(Authentication auth) { return offers.findByStoreOwnerId(currentUser(auth).getId()); }

    @PostMapping("/offers")
    public Offer createOffer(@RequestBody Offer incoming, Authentication auth) {
        User owner = currentUser(auth); validateOffer(incoming);
        if (incoming.getStore() == null || incoming.getStore().getId() == null) throw new IllegalArgumentException("Choose one of your stores.");
        Store store = ownedStore(incoming.getStore().getId(), owner.getId());
        incoming.setId(null); incoming.setStore(store);
        return offers.save(incoming);
    }

    @PutMapping("/offers/{id}")
    public Offer updateOffer(@PathVariable Long id, @RequestBody Offer incoming, Authentication auth) {
        User owner = currentUser(auth);
        Offer o = offers.findById(id).orElseThrow(() -> new IllegalArgumentException("Offer not found."));
        if (o.getStore() == null || o.getStore().getOwner() == null || !owner.getId().equals(o.getStore().getOwner().getId()))
            throw new AccessDeniedException("You can only update offers for your own stores.");
        validateOffer(incoming);
        o.setTitle(incoming.getTitle().trim()); o.setDescription(incoming.getDescription()); o.setDiscountPercent(incoming.getDiscountPercent());
        o.setStartDate(incoming.getStartDate()); o.setEndDate(incoming.getEndDate());
        if (incoming.getStore() != null && incoming.getStore().getId() != null) o.setStore(ownedStore(incoming.getStore().getId(), owner.getId()));
        return offers.save(o);
    }

    @DeleteMapping("/offers/{id}")
    public Map<String, String> deleteOffer(@PathVariable Long id, Authentication auth) {
        User owner = currentUser(auth); Offer o = offers.findById(id).orElseThrow(() -> new IllegalArgumentException("Offer not found."));
        if (o.getStore() == null || o.getStore().getOwner() == null || !owner.getId().equals(o.getStore().getOwner().getId()))
            throw new AccessDeniedException("You can only delete offers for your own stores.");
        offers.delete(o); return Map.of("message", "Offer deleted.");
    }

    private void validateOffer(Offer o) {
        if (o.getTitle() == null || o.getTitle().isBlank()) throw new IllegalArgumentException("Offer title is required.");
        if (o.getDescription() == null || o.getDescription().isBlank()) throw new IllegalArgumentException("Offer details are required.");
        if (o.getDiscountPercent() == null || o.getDiscountPercent().compareTo(BigDecimal.ZERO) < 0 || o.getDiscountPercent().compareTo(new BigDecimal("100")) > 0)
            throw new IllegalArgumentException("Discount must be between 0 and 100 percent.");
        if (o.getStartDate() == null || o.getEndDate() == null || o.getEndDate().isBefore(o.getStartDate()))
            throw new IllegalArgumentException("Provide a valid offer start and end date.");
    }

    @GetMapping("/orders")
    @Transactional
    public List<OwnerOrderView> myOrderLines(Authentication auth) {
        Long ownerId = currentUser(auth).getId();
        return orders.findAllByOrderByCreatedAtDesc().stream().map(order -> {
            List<OwnerOrderLine> lines = order.getItems().stream().filter(item -> {
                Store s = item.getProduct() == null ? null : item.getProduct().getStore();
                return s != null && s.getOwner() != null && ownerId.equals(s.getOwner().getId());
            }).map(item -> {
                Product p = item.getProduct(); BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                return new OwnerOrderLine(p.getName(), item.getQuantity(), item.getUnitPrice(), lineTotal, p.getStore().getName());
            }).toList();
            if (lines.isEmpty()) return null;
            BigDecimal sellerTotal = lines.stream().map(OwnerOrderLine::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            return new OwnerOrderView(order.getId(), order.getCustomer() == null ? "Customer" : order.getCustomer().getName(),
                    order.getStatus(), order.getCreatedAt(), sellerTotal, lines);
        }).filter(Objects::nonNull).toList();
    }

    public record OwnerOrderView(Long id, String customerName, Enums.OrderStatus status, LocalDateTime createdAt,
                                 BigDecimal sellerTotal, List<OwnerOrderLine> items) {}
    public record OwnerOrderLine(String productName, Integer quantity, BigDecimal unitPrice, BigDecimal lineTotal, String storeName) {}
}
