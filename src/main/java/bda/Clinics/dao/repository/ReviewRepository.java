package bda.Clinics.dao.repository;

import bda.Clinics.dao.model.entity.Review;
import bda.Clinics.dao.model.enums.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findByStatus(ReviewStatus reviewStatus);
    List<Review> findByParentReview(Review parentReview);

}
