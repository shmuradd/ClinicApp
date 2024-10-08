package bda.Clinics.dao.repository;

import bda.Clinics.dao.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,Long> {
}
