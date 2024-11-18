package bda.Clinics.dao.model.dto.request;

import bda.Clinics.dao.model.entity.Clinic;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    String serviceDescription;

    List<RequestClinicDto> clinics; // List of clinics with schedules


}
