package com.ecomerse.webecom.repository;

import com.ecomerse.webecom.entity.Product;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrue();

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByCategoryId(Long categoryId);

    boolean existsByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.active = true "
            + "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
    List<Product> filter(@Param("keyword") String keyword,
                         @Param("categoryId") Long categoryId,
                         Sort sort);
}
