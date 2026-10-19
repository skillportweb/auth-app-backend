package com.substring.auth.auth_app_backend.ratings.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.substring.auth.auth_app_backend.ratings.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    List<Rating> findByProduct_Id(UUID productId);

    List<Rating> findByStatusIgnoreCase(String status);

    List<Rating> findByProduct_IdAndStatusIgnoreCase(
            UUID productId,
            String status
    );

    Optional<Rating> findByUser_IdAndProduct_Id(
            UUID userId,
            UUID productId
    );

    long countByStatusIgnoreCase(String status);

    long countByRating(Integer rating);

    long countByReportedTrue();
}
