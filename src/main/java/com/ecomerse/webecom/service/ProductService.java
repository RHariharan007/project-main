package com.ecomerse.webecom.service;

import com.ecomerse.webecom.dto.Mappers;
import com.ecomerse.webecom.dto.ProductRequest;
import com.ecomerse.webecom.dto.ProductResponse;
import com.ecomerse.webecom.entity.Category;
import com.ecomerse.webecom.entity.Product;
import com.ecomerse.webecom.exception.ResourceNotFoundException;
import com.ecomerse.webecom.repository.CategoryRepository;
import com.ecomerse.webecom.repository.ProductRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse create(ProductRequest request) {
        Category category = findCategory(request.categoryId());
        Product product = new Product();
        apply(product, request, category);
        return Mappers.toProduct(productRepository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProduct(id);
        Category category = findCategory(request.categoryId());
        apply(product, request, category);
        return Mappers.toProduct(productRepository.save(product));
    }

    /** Products are hidden instead of removed so old orders and reviews keep working. */
    public void delete(Long id) {
        Product product = findProduct(id);
        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllForAdmin() {
        return productRepository.findAll(Sort.by("id")).stream().map(Mappers::toProduct).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return productRepository.findByActiveTrue().stream().map(Mappers::toProduct).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return Mappers.toProduct(findProduct(id));
    }

    /** Used by the public shop: hidden products behave as if they do not exist. */
    @Transactional(readOnly = true)
    public ProductResponse getPublicById(Long id) {
        Product product = findProduct(id);
        if (!product.isActive()) {
            throw new ResourceNotFoundException("Product not found");
        }
        return Mappers.toProduct(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> search(String keyword, Long categoryId, String sort) {
        String cleanKeyword = keyword == null ? "" : keyword.trim();
        return productRepository.filter(cleanKeyword, categoryId, toSort(sort))
                .stream().map(Mappers::toProduct).toList();
    }

    private Sort toSort(String sort) {
        if (sort == null) {
            return Sort.by("id").ascending();
        }
        return switch (sort) {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "rating" -> Sort.by("averageRating").descending();
            case "newest" -> Sort.by("createdAt").descending();
            case "name" -> Sort.by("name").ascending();
            default -> Sort.by("id").ascending();
        };
    }

    private void apply(Product product, ProductRequest request, Category category) {
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setImageUrl(request.imageUrl());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(category);
        product.setBrand(request.brand());
        product.setWarranty(request.warranty());
        product.setPros(Mappers.join(request.pros()));
        product.setCons(Mappers.join(request.cons()));
        product.setActive(request.active() == null || request.active());
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
