package com.ecomerse.webecom.service;

import com.ecomerse.webecom.dto.Mappers;
import com.ecomerse.webecom.dto.ReviewRequest;
import com.ecomerse.webecom.dto.ReviewResponse;
import com.ecomerse.webecom.entity.OrderStatus;
import com.ecomerse.webecom.entity.Product;
import com.ecomerse.webecom.entity.Review;
import com.ecomerse.webecom.entity.User;
import com.ecomerse.webecom.exception.BadRequestException;
import com.ecomerse.webecom.exception.ResourceNotFoundException;
import com.ecomerse.webecom.repository.OrderRepository;
import com.ecomerse.webecom.repository.ProductRepository;
import com.ecomerse.webecom.repository.ReviewRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         ProductRepository productRepository,
                         OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getForProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream().map(Mappers::toReview).toList();
    }

    /** Only customers who bought the product (and did not cancel) can review it. One review per customer. */
    public ReviewResponse addOrUpdate(User user, Long productId, ReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        boolean purchased = orderRepository.existsByUserIdAndItemsProductIdAndStatusNot(
                user.getId(), productId, OrderStatus.CANCELLED);
        if (!purchased) {
            throw new BadRequestException("Only customers who purchased this product can review it");
        }
        Review review = reviewRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseGet(Review::new);
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.rating());
        review.setComment(request.comment());
        Review saved = reviewRepository.saveAndFlush(review);

        refreshProductRating(product);
        return Mappers.toReview(saved);
    }

    /** Recalculates the product's average rating and review count from its reviews. */
    public void refreshProductRating(Product product) {
        Double average = reviewRepository.averageRating(product.getId());
        double rounded = average == null ? 0.0 : Math.round(average * 10.0) / 10.0;
        product.setAverageRating(rounded);
        product.setReviewCount((int) reviewRepository.countByProductId(product.getId()));
        productRepository.save(product);
    }
}
