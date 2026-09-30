package com.ecomerse.webecom.service;

import com.ecomerse.webecom.dto.CategoryRequest;
import com.ecomerse.webecom.dto.CategoryResponse;
import com.ecomerse.webecom.dto.Mappers;
import com.ecomerse.webecom.entity.Category;
import com.ecomerse.webecom.exception.BadRequestException;
import com.ecomerse.webecom.exception.ResourceNotFoundException;
import com.ecomerse.webecom.repository.CategoryRepository;
import com.ecomerse.webecom.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name().trim())) {
            throw new BadRequestException("A category with this name already exists");
        }
        Category category = new Category();
        category.setName(request.name().trim());
        category.setDescription(request.description());
        return Mappers.toCategory(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(Mappers::toCategory).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return Mappers.toCategory(findOrThrow(id));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findOrThrow(id);
        category.setName(request.name().trim());
        category.setDescription(request.description());
        return Mappers.toCategory(categoryRepository.save(category));
    }

    public void delete(Long id) {
        Category category = findOrThrow(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new BadRequestException("Cannot delete a category that still has products");
        }
        categoryRepository.delete(category);
    }

    private Category findOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
