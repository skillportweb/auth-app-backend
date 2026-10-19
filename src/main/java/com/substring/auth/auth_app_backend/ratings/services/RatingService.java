
package com.substring.auth.auth_app_backend.ratings.services;

import java.util.List;
import java.util.UUID;

import com.substring.auth.auth_app_backend.auth.entities.User;
import com.substring.auth.auth_app_backend.auth.repositories.UserRepository;
import com.substring.auth.auth_app_backend.product.entities.Product;
import com.substring.auth.auth_app_backend.product.repositories.ProductRepository;
import com.substring.auth.auth_app_backend.ratings.dtos.RatingResponse;
import com.substring.auth.auth_app_backend.ratings.entities.Rating;
import com.substring.auth.auth_app_backend.ratings.repositories.RatingRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public RatingService(
            RatingRepository ratingRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<RatingResponse> getAllRatings() {
        return ratingRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RatingResponse getRatingById(UUID id) {
        return toResponse(findRating(id));
    }

    @Transactional
    public RatingResponse submitRating(
            UUID userId,
            UUID productId,
            Integer ratingValue,
            String reviewText) {

        if (ratingValue == null || ratingValue < 1 || ratingValue > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5");
        }

        if (reviewText != null && reviewText.length() > 2000) {
            throw new IllegalArgumentException(
                    "Review cannot exceed 2000 characters");
        }

        if (ratingRepository.findByUser_IdAndProduct_Id(
                userId, productId).isPresent()) {
            throw new IllegalArgumentException(
                    "You have already rated this product");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + productId));

        Rating rating = new Rating();
        rating.setUser(user);
        rating.setProduct(product);
        rating.setRating(ratingValue);
        rating.setReview(reviewText);
        rating.setStatus("PENDING");
        rating.setReported(false);

        return toResponse(ratingRepository.save(rating));
    }

    @Transactional(readOnly = true)
    public List<RatingResponse> getPublishedRatingsByProduct(UUID productId) {
        return ratingRepository
                .findByProduct_IdAndStatusIgnoreCase(productId, "PUBLISHED")
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByProduct(UUID productId) {
        return ratingRepository.findByProduct_Id(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RatingResponse updateStatus(UUID id, String status) {
        if (status == null ||
                !List.of("PENDING", "PUBLISHED", "HIDDEN")
                        .contains(status.trim().toUpperCase())) {
            throw new IllegalArgumentException(
                    "Status must be PENDING, PUBLISHED, or HIDDEN");
        }

        Rating rating = findRating(id);
        rating.setStatus(status.trim().toUpperCase());

        return toResponse(ratingRepository.save(rating));
    }

    @Transactional
    public void deleteRating(UUID id) {
        ratingRepository.delete(findRating(id));
    }

    @Transactional(readOnly = true)
    public long getTotalRatings() {
        return ratingRepository.count();
    }

    @Transactional(readOnly = true)
    public long getPendingRatings() {
        return ratingRepository.countByStatusIgnoreCase("PENDING");
    }

    @Transactional(readOnly = true)
    public long getPublishedRatings() {
        return ratingRepository.countByStatusIgnoreCase("PUBLISHED");
    }

    @Transactional(readOnly = true)
    public long getReportedRatings() {
        return ratingRepository.countByReportedTrue();
    }

    private Rating findRating(UUID id) {
        return ratingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Rating not found: " + id));
    }

    private RatingResponse toResponse(Rating rating) {
        RatingResponse response = new RatingResponse();

        response.setId(rating.getId());
        response.setProductId(rating.getProduct().getId());
        response.setProductName(rating.getProduct().getProductName());
        response.setUserId(rating.getUser().getId());
        response.setCustomerName(rating.getUser().getName());
        response.setCustomerEmail(rating.getUser().getEmail());
        response.setRating(rating.getRating());
        response.setReview(rating.getReview());
        response.setStatus(rating.getStatus());
        response.setReported(rating.isReported());
        response.setCreatedAt(rating.getCreatedAt());
        response.setUpdatedAt(rating.getUpdatedAt());

        return response;
    }
}
