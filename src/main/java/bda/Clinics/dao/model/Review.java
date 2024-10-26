package bda.Clinics.dao.model;

import bda.Clinics.dao.model.enums.ReviewStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.Objects;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long reviewId;
    String fullName;
    int rating;
    String comment;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    ReviewStatus status = ReviewStatus.PENDING;
    @CreationTimestamp
    Date reviewDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return rating == review.rating && Objects.equals(fullName, review.fullName) && Objects.equals(comment, review.comment) && status == review.status && Objects.equals(reviewDate, review.reviewDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, rating, comment, status, reviewDate);
    }
}
