package com.ecomerse.webecom.config;

import com.ecomerse.webecom.entity.Category;
import com.ecomerse.webecom.entity.Product;
import com.ecomerse.webecom.entity.Review;
import com.ecomerse.webecom.entity.Role;
import com.ecomerse.webecom.entity.User;
import com.ecomerse.webecom.repository.CategoryRepository;
import com.ecomerse.webecom.repository.ProductRepository;
import com.ecomerse.webecom.repository.ReviewRepository;
import com.ecomerse.webecom.repository.UserRepository;
import com.ecomerse.webecom.service.ReviewService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inserts sample data the first time the app starts (only when the database is empty).
 * Demo admin login:    admin@shopverse.com / Admin@123
 * Demo customer login: priya@example.com / Customer@123
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "admin@shopverse.com";

    private static final String[] REVIEW_COMMENTS = {
        "Great quality for the price. Arrived on time and well packed.",
        "Works exactly as described. Would happily buy again.",
        "Good product overall, but delivery took a little longer than expected.",
        "Very satisfied with the build and finish.",
        "Decent value. Does the job, though the packaging could be better.",
        "Exceeded my expectations. Highly recommended."
    };
    private static final int[] REVIEW_RATINGS = {5, 4, 5, 3, 4};

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           ReviewRepository reviewRepository,
                           ReviewService reviewService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!userRepository.existsByEmail(ADMIN_EMAIL)) {
            createUser("Shop Admin", ADMIN_EMAIL, "Admin@123", Role.ADMIN);
        }
        if (categoryRepository.count() > 0) {
            return;
        }

        List<User> reviewers = new ArrayList<>();
        reviewers.add(createUser("Priya S.", "priya@example.com", "Customer@123", Role.USER));
        reviewers.add(createUser("Rahul K.", "rahul@example.com", "Customer@123", Role.USER));
        reviewers.add(createUser("Meena R.", "meena@example.com", "Customer@123", Role.USER));

        Category electronics = category("Electronics", "Phones, audio, laptops and gadgets");
        Category dresses = category("Dresses", "Everyday and occasion clothing");
        Category cosmetics = category("Cosmetics", "Skin care and makeup");
        Category jewellery = category("Jewellery", "Necklaces, bracelets and more");
        Category shoes = category("Shoes", "Sports and casual footwear");
        Category watches = category("Watches", "Analog and smart watches");
        Category household = category("Household", "Kitchen and home essentials");

        List<Product> products = new ArrayList<>();
        products.add(product("Wireless Headphones", "Over-ear Bluetooth headphones with deep bass, 30 hour battery and a built-in microphone for calls.",
                "2999", electronics, "Sonic Labs", "1 year manufacturer warranty",
                "Long battery life|Comfortable ear cushions|Clear call quality", "Bulky to carry|No noise cancelling", 40, "22d3ee"));
        products.add(product("Smart Watch", "Fitness smart watch with heart-rate tracking, sleep monitor, GPS and a bright AMOLED display.",
                "4999", electronics, "PulseTech", "1 year manufacturer warranty",
                "Accurate heart-rate tracking|Bright display|Water resistant", "Strap feels plasticky|Needs daily charging with GPS on", 25, "3b82f6"));
        products.add(product("Bluetooth Speaker", "Portable waterproof speaker with 360 degree sound and 12 hours of playtime.",
                "1799", electronics, "BoomBox Audio", "6 months manufacturer warranty",
                "Loud and clear sound|Waterproof|Compact size", "Bass is average|No aux input", 60, "e879f9"));
        products.add(product("Laptop 15.6 inch", "Thin 15.6 inch laptop with 16 GB RAM, 512 GB SSD and a full HD display for work and study.",
                "54999", electronics, "NovaBook", "2 year onsite warranty",
                "Fast SSD storage|Lightweight|Great keyboard", "Average speakers|Fans get loud under load", 12, "22d3ee"));

        products.add(product("Casual Cotton Shirt", "Breathable pure cotton shirt with a relaxed fit, ideal for everyday wear.",
                "899", dresses, "UrbanThread", "7 day exchange policy",
                "Soft breathable fabric|Colour holds after washing|Comfortable fit", "Needs ironing|Runs slightly large", 80, "3b82f6"));
        products.add(product("Floral Summer Dress", "Lightweight floral midi dress with an adjustable waist tie and pockets.",
                "1299", dresses, "Bloom Wear", "7 day exchange policy",
                "Light and airy|Has pockets|Flattering cut", "Fabric wrinkles easily|Dry clean recommended", 45, "e879f9"));
        products.add(product("Denim Jacket", "Classic mid-wash denim jacket with a slightly cropped fit.",
                "2199", dresses, "Indigo Works", "15 day exchange policy",
                "Durable denim|Goes with everything|Sturdy stitching", "Stiff when new|Limited sizes", 30, "22d3ee"));

        products.add(product("Gentle Face Wash", "Sulphate-free daily face wash with aloe vera for all skin types.",
                "249", cosmetics, "PureGlow", "No warranty. Use within 24 months of manufacture",
                "Gentle on skin|Pleasant light scent|Good value", "Does not remove heavy makeup|Small pump", 120, "e879f9"));
        products.add(product("Daily Moisturizer SPF 30", "Non-greasy moisturizer with SPF 30 and hyaluronic acid.",
                "499", cosmetics, "DermaCare", "No warranty. Use within 24 months of manufacture",
                "Absorbs quickly|Built-in sun protection|Non-sticky", "Slightly pricey|Fragrance may bother sensitive skin", 90, "3b82f6"));
        products.add(product("Matte Lipstick", "Long-wear matte lipstick in a rich berry shade.",
                "399", cosmetics, "Velvet Hue", "No warranty. Use within 18 months of manufacture",
                "Rich colour payoff|Lasts through the day|Not too drying", "Only one shade in this pack|Can feel dry after many hours", 100, "e879f9"));

        products.add(product("Gold Plated Necklace", "Elegant gold plated chain with a small pendant, hypoallergenic and skin friendly.",
                "1499", jewellery, "Aurelia", "6 month plating warranty",
                "Lovely finish|Lightweight|Skin friendly", "Plating fades with daily wear|Chain is fine, handle gently", 35, "fbbf24"));
        products.add(product("Silver Charm Bracelet", "Sterling silver bracelet with three interchangeable charms.",
                "1199", jewellery, "Lumi Silver", "1 year warranty against manufacturing defects",
                "Real sterling silver|Adjustable size|Comes in a gift box", "Clasp is small|Tarnishes if not stored well", 28, "22d3ee"));

        products.add(product("Running Shoes", "Cushioned running shoes with a breathable mesh upper and grippy outsole.",
                "2499", shoes, "StrideMax", "6 month warranty against sole defects",
                "Very comfortable|Good grip|Breathable", "Laces are short|Not for wide feet", 50, "3b82f6"));
        products.add(product("Leather Sneakers", "Minimal white leather sneakers that work with casual and smart outfits.",
                "3199", shoes, "Urban Sole", "6 month warranty against sole defects",
                "Premium look|Easy to clean|Supportive insole", "Needs breaking in|Gets scuffed easily", 22, "e879f9"));

        products.add(product("Classic Analog Watch", "Stainless steel analog watch with a sapphire-coated glass and 50 metre water resistance.",
                "3499", watches, "Chronos", "2 year international warranty",
                "Timeless design|Scratch resistant glass|Accurate movement", "Strap needs sizing at a shop|No date window", 20, "fbbf24"));
        products.add(product("Steel Chronograph Watch", "Chronograph watch with a brushed steel case and three sub-dials.",
                "6999", watches, "Chronos", "2 year international warranty",
                "Solid build|Easy to read dials|Great gift", "Heavy for small wrists|Pricey", 14, "22d3ee"));

        products.add(product("Non-Stick Cookware Set", "Five piece induction-friendly non-stick cookware set with heat-proof handles.",
                "2799", household, "HomeChef", "1 year warranty on the coating",
                "Works on induction|Easy to clean|Even heating", "Not dishwasher safe|Handles get warm", 26, "3b82f6"));
        products.add(product("LED Table Lamp", "Dimmable LED desk lamp with three colour temperatures and a USB charging port.",
                "899", household, "LumaHome", "1 year warranty",
                "Adjustable brightness|Built-in USB port|Flexible neck", "Touch buttons are sensitive|Cable is short", 55, "fbbf24"));

        List<Product> saved = productRepository.saveAll(products);
        addSampleReviews(saved, reviewers);
    }

    private User createUser(String name, String email, String rawPassword, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }

    private Category category(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    private Product product(String name, String description, String price, Category category,
                            String brand, String warranty, String pros, String cons,
                            int stock, String colour) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setCategory(category);
        product.setBrand(brand);
        product.setWarranty(warranty);
        product.setPros(pros);
        product.setCons(cons);
        product.setStockQuantity(stock);
        product.setActive(true);
        product.setImageUrl("https://placehold.co/600x600/0f172a/" + colour + "?text=" + name.replace(" ", "+"));
        return product;
    }

    private void addSampleReviews(List<Product> products, List<User> reviewers) {
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            addReview(product, reviewers.get(i % 3), REVIEW_RATINGS[i % 5], REVIEW_COMMENTS[i % 6], 12 + i);
            addReview(product, reviewers.get((i + 1) % 3), REVIEW_RATINGS[(i + 2) % 5], REVIEW_COMMENTS[(i + 3) % 6], 4 + i);
            reviewService.refreshProductRating(product);
        }
    }

    private void addReview(Product product, User user, int rating, String comment, int daysAgo) {
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now().minusDays(daysAgo));
        reviewRepository.save(review);
    }
}
