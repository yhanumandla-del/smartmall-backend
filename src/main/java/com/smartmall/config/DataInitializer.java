package com.smartmall.config;

import com.smartmall.entity.*;
import com.smartmall.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seed(UserRepository users, CategoryRepository cats, StoreRepository stores,
                           ProductRepository products, OfferRepository offers, PasswordEncoder encoder) {
        return args -> {
            users.findByEmail("admin@smartmall.com")
                    .orElseGet(() -> users.save(new User("Mall Admin", "admin@smartmall.com", encoder.encode("admin123"), Enums.Role.ADMIN)));
            User owner = users.findByEmail("owner@smartmall.com")
                    .orElseGet(() -> users.save(new User("Store Owner", "owner@smartmall.com", encoder.encode("owner123"), Enums.Role.STORE_OWNER)));
            users.findByEmail("customer@smartmall.com")
                    .orElseGet(() -> users.save(new User("Demo Customer", "customer@smartmall.com", encoder.encode("customer123"), Enums.Role.CUSTOMER)));

            Category electronics = category(cats, "Electronics");
            Category fashion = category(cats, "Fashion");
            Category grocery = category(cats, "Groceries");
            Category footwear = category(cats, "Footwear");
            Category food = category(cats, "Food & Dining");
            Category beauty = category(cats, "Beauty");
            Category home = category(cats, "Home & Kitchen");
            Category jewellery = category(cats, "Jewellery & Accessories");
            Category books = category(cats, "Books & Stationery");
            Category toys = category(cats, "Toys & Games");
            Category sports = category(cats, "Sports & Fitness");

            // Seed a representative digital mall directory. Urban Thread belongs to the demo seller; other sample shops are mall-directory entries.
            Store tech = store(stores, null, "Nova Electronics", "Smart devices, audio and everyday tech.", "Electronics • Smart Devices", "+91 90000 11111", "Electronics");
            Store clothing = store(stores, owner, "Urban Thread", "Everyday fashion, casual wear and seasonal collections.", "Fashion & Apparel", "+91 90000 22222", "Fashion");
            Store shoes = store(stores, null, "StepUp Footwear", "Sneakers, daily footwear and formal styles for all ages.", "Footwear", "+91 90000 33333", "Footwear");
            Store market = store(stores, null, "Daily Harvest Market", "Groceries, pantry staples and household essentials.", "Groceries & Supermarket", "+91 90000 44444", "Groceries");
            Store cafe = store(stores, null, "Brew & Bite", "Coffee, snacks and relaxed food-court dining.", "Food & Dining", "+91 90000 55555", "Food & Dining");
            Store glow = store(stores, null, "Glow Beauty Studio", "Skincare, beauty essentials and personal care.", "Beauty & Personal Care", "+91 90000 66666", "Beauty");
            Store homeLiving = store(stores, null, "Nest Home", "Home décor, kitchen appliances, mixers and useful everyday products.", "Home & Kitchen • Kitchen Appliances", "+91 90000 77777", "Home & Kitchen");
            Store jewelleryShop = store(stores, null, "Lustre Jewellery", "Everyday jewellery, watches and thoughtful accessories.", "Jewellery & Accessories", "+91 90000 88881", "Jewellery & Accessories");
            Store stationeryShop = store(stores, null, "Paper & Pen", "Books, notebooks, stationery and school supplies.", "Books & Stationery", "+91 90000 88882", "Books & Stationery");
            Store toyShop = store(stores, null, "PlaySquare Toys", "Toys, puzzles, building sets and family games.", "Toys & Games", "+91 90000 88883", "Toys & Games");
            Store sportsShop = store(stores, null, "ActiveLife Sports", "Sports equipment, training gear and fitness accessories.", "Sports & Fitness", "+91 90000 88884", "Sports & Fitness");

            // Normalize old demo labels before upserting sample records.
            renameProduct(products, "Daily Essentials Pack", "Daily Market Basket");
            renameProduct(products, "Home Essentials Storage Organizer", "Home Storage Organizer");

            // Add products by name so starting the app more than once does not create duplicates.
            product(products, "AeroFit Wireless Headphones", "Low-latency Bluetooth headphones with active noise reduction.", "2499", 35, tech, electronics);
            product(products, "Pulse Smartwatch", "AMOLED display, activity tracking and seven-day battery.", "3299", 20, tech, electronics);
            product(products, "Urban Cotton Tee", "Soft cotton regular-fit T-shirt for everyday wear.", "699", 55, clothing, fashion);
            product(products, "Classic Denim Jacket", "A versatile denim jacket for casual outfits.", "1899", 18, clothing, fashion);
            product(products, "Everyday Runner Sneakers", "Lightweight cushioned sneakers for daily use.", "1599", 26, shoes, footwear);
            product(products, "Daily Market Basket", "A curated basket of grocery and pantry products for quick shopping.", "1299", 30, market, grocery);
            product(products, "Cold Brew Coffee Kit", "A simple kit for preparing smooth cold brew at home.", "899", 14, cafe, food);
            product(products, "Hydrating Face Wash", "Gentle daily cleanser for a fresh, clean feel.", "349", 40, glow, beauty);
            product(products, "Minimal Desk Organizer", "Compact organizer for a tidy work desk.", "599", 22, homeLiving, home);
            product(products, "Preethi Zodiac Mixer Grinder", "A versatile mixer grinder with multiple jars for everyday cooking and food preparation.", "6999", 12, homeLiving, home);
            product(products, "Philips HL7756 Mixer Grinder", "A 750W mixer grinder designed for chutneys, spice mixes and daily kitchen prep.", "3599", 15, homeLiving, home);
            product(products, "Bajaj Rex Mixer Grinder", "A practical mixer grinder with multiple jars for daily blending and grinding.", "2499", 20, homeLiving, home);
            product(products, "Havells Capture Mixer Grinder", "An everyday kitchen mixer grinder for blending, grinding and meal preparation.", "2799", 14, homeLiving, home);
            product(products, "Bosch TrueMixx Pro Mixer Grinder", "A powerful mixer grinder with versatile attachments for home cooking.", "6999", 8, homeLiving, home);
            product(products, "Prestige Induction Cooktop", "A compact induction cooktop for convenient everyday cooking.", "2499", 16, homeLiving, home);
            product(products, "Pigeon Nonstick Cookware Set", "A practical cookware set for everyday home meals.", "1799", 18, homeLiving, home);
            product(products, "Home Storage Organizer", "A versatile storage organizer to keep everyday household items in order.", "499", 24, homeLiving, home);
            product(products, "Everyday Silver Pendant", "A simple silver-tone pendant for everyday styling.", "1299", 18, jewelleryShop, jewellery);
            product(products, "Classic Dial Wristwatch", "A clean everyday wristwatch with a comfortable strap.", "1999", 14, jewelleryShop, jewellery);
            product(products, "Academic Notebook Set", "A set of ruled notebooks for study, planning and notes.", "249", 50, stationeryShop, books);
            product(products, "Everyday Ballpoint Pens", "A pack of smooth-writing pens for school or office use.", "99", 80, stationeryShop, books);
            product(products, "Build & Play Blocks", "Colourful building blocks for creative play.", "799", 22, toyShop, toys);
            product(products, "Family Puzzle Game", "A family-friendly puzzle game for game night.", "499", 16, toyShop, toys);
            product(products, "Training Football", "A durable football for practice and recreational games.", "699", 20, sportsShop, sports);
            product(products, "Yoga Mat", "A comfortable exercise mat for stretching and home workouts.", "599", 25, sportsShop, sports);

            renameOffer(offers, "Everyday Essentials", "Market Savings");
            renameOffer(offers, "Home Essentials Mixer Week", "Kitchen Appliance Week");
            offer(offers, stores, "Weekend Tech Fest", "Extra savings on selected electronics this week.", "15", LocalDate.now(), LocalDate.now().plusDays(7), tech);
            offer(offers, stores, "Style Refresh", "Seasonal picks from Urban Thread.", "10", LocalDate.now(), LocalDate.now().plusDays(14), clothing);
            offer(offers, stores, "Market Savings", "Save on selected grocery and pantry products.", "8", LocalDate.now(), LocalDate.now().plusDays(10), market);
            offer(offers, stores, "Kitchen Appliance Week", "Discover deals on selected mixer grinders and kitchen appliances.", "10", LocalDate.now(), LocalDate.now().plusDays(14), homeLiving);
        };
    }

    private Category category(CategoryRepository repo, String name) {
        var all = repo.findAll();
        Category match = all.stream().filter(c -> name.equalsIgnoreCase(c.getName())).findFirst().orElse(null);
        if (match != null) return match;
        // Rename the old catch-all home category to the more general mall department label.
        if ("Home & Kitchen".equalsIgnoreCase(name)) {
            Category legacy = all.stream().filter(c -> "Home Essentials".equalsIgnoreCase(c.getName()) || "Home & Living".equalsIgnoreCase(c.getName())).findFirst().orElse(null);
            if (legacy != null) {
                legacy.setName(name);
                return repo.save(legacy);
            }
        }
        return repo.save(new Category(name));
    }

    private Store store(StoreRepository repo, User owner, String name, String description, String location,
                        String phone, String category) {
        Store s = repo.findAll().stream().filter(x -> name.equalsIgnoreCase(x.getName())).findFirst().orElse(null);
        if (s == null) {
            s = new Store(name, description, location, phone, null, owner);
            s.setCategory(category);
            return repo.save(s);
        }
        // Upgrade older demo rows from floor labels and old catch-all labels to mall departments.
        boolean oldHomeCategory = ("Home & Living".equalsIgnoreCase(s.getCategory()) || "Home Essentials".equalsIgnoreCase(s.getCategory())) && "Home & Kitchen".equalsIgnoreCase(category);
        if (s.getCategory() == null || s.getCategory().isBlank() || oldHomeCategory) s.setCategory(category);
        s.setOwner(owner);
        s.setOpeningHours(null);
        String oldLocation = s.getLocation() == null ? "" : s.getLocation().toLowerCase();
        if (s.getLocation() == null || s.getLocation().isBlank() || isLegacyFloorLabel(s.getLocation())
                || oldLocation.startsWith("home essentials") || oldLocation.startsWith("home & living")
                || oldLocation.startsWith("all essentials")) {
            s.setLocation(defaultSection(category));
        }
        return repo.save(s);
    }

    private boolean isLegacyFloorLabel(String value) {
        String text = value == null ? "" : value.toLowerCase();
        return text.contains("floor") || text.contains("basement") || text.matches(".*\\b(g|f)-\\d+\\b.*");
    }

    private String defaultSection(String category) {
        return switch (category) {
            case "Electronics" -> "Electronics • Smart Devices";
            case "Fashion" -> "Fashion & Clothing";
            case "Footwear" -> "Footwear";
            case "Groceries", "Groceries & Supermarket" -> "Groceries & Supermarket";
            case "Food & Dining" -> "Food & Dining";
            case "Beauty" -> "Beauty & Personal Care";
            case "Home Essentials", "Home & Living", "Home & Kitchen" -> "Home & Kitchen • Kitchen Appliances";
            default -> category;
        };
    }

    private void product(ProductRepository repo, String name, String description, String price, int stock,
                         Store store, Category category) {
        Product existing = repo.findAll().stream().filter(p -> name.equalsIgnoreCase(p.getName())).findFirst().orElse(null);
        if (existing == null) {
            repo.save(new Product(name, description, new BigDecimal(price), stock, "", store, category));
        } else {
            // Move earlier demo products into their appropriate shop after upgrading an old database.
            existing.setStore(store);
            existing.setCategory(category);
            repo.save(existing);
        }
    }

    private void renameProduct(ProductRepository repo, String oldName, String newName) {
        repo.findAll().stream().filter(p -> oldName.equalsIgnoreCase(p.getName()))
                .findFirst().ifPresent(p -> { p.setName(newName); repo.save(p); });
    }

    private void offer(OfferRepository repo, StoreRepository stores, String title, String description,
                       String discount, LocalDate start, LocalDate end, Store store) {
        boolean exists = repo.findAll().stream().anyMatch(o -> title.equalsIgnoreCase(o.getTitle()));
        if (!exists) {
            Offer o = new Offer();
            o.setTitle(title);
            o.setDescription(description);
            o.setDiscountPercent(new BigDecimal(discount));
            o.setStartDate(start);
            o.setEndDate(end);
            o.setStore(stores.findById(store.getId()).orElse(store));
            repo.save(o);
        }
    }

    private void renameOffer(OfferRepository repo, String oldTitle, String newTitle) {
        repo.findAll().stream().filter(o -> oldTitle.equalsIgnoreCase(o.getTitle()))
                .findFirst().ifPresent(o -> {
                    o.setTitle(newTitle);
                    if ("Market Savings".equals(newTitle)) o.setDescription("Save on selected grocery and pantry products.");
                    if ("Kitchen Appliance Week".equals(newTitle)) o.setDescription("Discover deals on selected mixer grinders and kitchen appliances.");
                    repo.save(o);
                });
    }
}
