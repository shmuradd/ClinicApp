package bda.Clinics.service;

import bda.Clinics.dao.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<Review> getAllReviews();
    Optional<Review> getReviewById(Long reviewId);
    List<Review> getReviewsByDoctorId(Long doctorId);
    Review saveReview(Review review);
    void deleteReview(Long reviewId);
    Review updateReview(Long reviewId, Review review);
}
