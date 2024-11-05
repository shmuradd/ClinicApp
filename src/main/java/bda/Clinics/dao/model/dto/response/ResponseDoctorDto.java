package bda.Clinics.dao.model.dto.response;

import bda.Clinics.dao.model.dto.request.RequestClinicDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseDoctorDto {
    Long doctorId;
    String fullName;
    String speciality;
    String qualifications;
    Double experience;
    String service;
    String serviceDescription;
    Boolean isActive;
    Set<RequestClinicDto> clinics;
    Set<ResponseReviewDto> reviews;
    String photoUrl;
    Set<ResponseScheduleDto> schedules;
    // Calculate review count from reviews
    public int getReviewCount() {
        return reviews != null ? reviews.size() : 0;
    }

    // Calculate average rating from reviews and round to one decimal place
    public double getRating() {
        if (reviews != null && !reviews.isEmpty()) {
            double averageRating = reviews.stream()
                    .mapToDouble(ResponseReviewDto::getRating)
                    .average()
                    .orElse(0.0);
            return Math.round(averageRating * 10.0) / 10.0; // Round to one decimal place
        }
        return 0.0;
    }

}
