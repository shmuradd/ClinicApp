package bda.Clinics.dao.model;

import bda.Clinics.dao.model.enums.ReviewStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

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
    Boolean conditionAgreed;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    ReviewStatus status = ReviewStatus.PENDING;
    @CreationTimestamp
    Date reviewDate;


}
