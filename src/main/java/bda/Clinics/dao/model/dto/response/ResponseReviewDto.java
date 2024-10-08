package bda.Clinics.dao.model.dto.response;

import bda.Clinics.dao.model.Doctor;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseReviewDto {
    int rating;
    String comments;
    Date reviewDate;
}
