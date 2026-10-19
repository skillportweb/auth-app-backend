
package com.substring.auth.auth_app_backend.ratings.controllers;

import java.util.List;
import java.util.UUID;

import com.substring.auth.auth_app_backend.auth.entities.User;
import com.substring.auth.auth_app_backend.auth.repositories.UserRepository;
import com.substring.auth.auth_app_backend.ratings.dtos.RatingRequest;
import com.substring.auth.auth_app_backend.ratings.dtos.RatingResponse;
import com.substring.auth.auth_app_backend.ratings.services.RatingService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class RatingController {

    private final RatingService ratingService;
    private final UserRepository userRepository;

    public RatingController(
            RatingService ratingService,
            UserRepository userRepository) {
        this.ratingService = ratingService;
        this.userRepository = userRepository;
    }

    // Customer: Submit a product rating
    @PostMapping("/api/v1/products/{productId}/ratings")
    public ResponseEntity<RatingResponse> submitRating(
            @PathVariable UUID productId,
            @Valid @RequestBody RatingRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        RatingResponse response = ratingService.submitRating(
                user.getId(),
                productId,
                request.getRating(),
                request.getReview());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Customer: Get published reviews for a product
    @GetMapping("/api/v1/products/{productId}/ratings")
    public ResponseEntity<List<RatingResponse>> getProductRatings(
            @PathVariable UUID productId) {

        return ResponseEntity.ok(
                ratingService.getPublishedRatingsByProduct(productId));
    }

    // Admin: Get all ratings
    @GetMapping("/api/v1/admin/ratings")
    public ResponseEntity<List<RatingResponse>> getAllRatings() {
        return ResponseEntity.ok(ratingService.getAllRatings());
    }

    // Admin: Get rating by ID
    @GetMapping("/api/v1/admin/ratings/{id}")
    public ResponseEntity<RatingResponse> getRatingById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(ratingService.getRatingById(id));
    }

    // Admin: Update review status
    @PatchMapping("/api/v1/admin/ratings/{id}/status")
    public ResponseEntity<RatingResponse> updateRatingStatus(
            @PathVariable UUID id,
            @RequestBody StatusUpdateRequest request) {

        RatingResponse response = ratingService.updateStatus(
                id, request.getStatus());

        return ResponseEntity.ok(response);
    }

    // Admin: Delete review
    @DeleteMapping("/api/v1/admin/ratings/{id}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable UUID id) {

        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "User must be authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));
    }

    public static class StatusUpdateRequest {

        private String status;

        public StatusUpdateRequest() {
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
