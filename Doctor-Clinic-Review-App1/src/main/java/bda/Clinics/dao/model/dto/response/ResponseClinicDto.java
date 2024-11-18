package bda.Clinics.dao.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseClinicDto {
    String clinicName;
    String location;
    String contactDetails;
    String city;
}