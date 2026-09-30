package com.ecomerse.webecom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecomerse.webecom.dto.ProductRequest;
import com.ecomerse.webecom.dto.ProductResponse;
import com.ecomerse.webecom.entity.Category;
import com.ecomerse.webecom.entity.Product;
import com.ecomerse.webecom.exception.ResourceNotFoundException;
import com.ecomerse.webecom.repository.CategoryRepository;
import com.ecomerse.webecom.repository.ProductRepository;
import com.ecomerse.webecom.service.ProductService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Category electronics() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        return category;
    }

    @Test
    void getByIdThrowsWhenProductDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getById(99L));
    }

    @Test
    void createSavesProductAndReturnsResponse() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics()));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });

        ProductRequest request = new ProductRequest("Phone", "A phone", new BigDecimal("999"), "img", 10, 1L,
                "Acme", "1 year", List.of("Fast"), List.of("Pricey"), true);
        ProductResponse response = productService.create(request);

        assertEquals(5L, response.id());
        assertEquals("Electronics", response.categoryName());
        assertEquals(List.of("Fast"), response.pros());
        assertEquals(List.of("Pricey"), response.cons());
    }

    @Test
    void createFailsWhenCategoryIsMissing() {
        when(categoryRepository.findById(7L)).thenReturn(Optional.empty());
        ProductRequest request = new ProductRequest("Phone", "A phone", new BigDecimal("999"), "img", 10, 7L,
                "Acme", "1 year", List.of(), List.of(), true);
        assertThrows(ResourceNotFoundException.class, () -> productService.create(request));
    }

    @Test
    void deleteHidesTheProductInsteadOfRemovingIt() {
        Product product = new Product();
        product.setId(3L);
        product.setActive(true);
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));

        productService.delete(3L);

        assertFalse(product.isActive());
        verify(productRepository).save(product);
    }
}
