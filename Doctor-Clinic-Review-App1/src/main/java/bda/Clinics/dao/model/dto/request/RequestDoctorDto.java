package bda.Clinics.dao.model.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDoctorDto {
    String fullName;
    String speciality;
    String clinicName;
    String location;
    Double reviewCount;
    Double ratingCount;
}
