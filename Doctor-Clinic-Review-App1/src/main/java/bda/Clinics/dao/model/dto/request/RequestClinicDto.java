package bda.Clinics.dao.model.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestClinicDto {
     Long clinicId;
     String clinicName;
     String location;
     String contactDetails;
     String city;
     Double distance;

     public Double getDistance() {
          return distance != null ? distance : Double.MAX_VALUE;
     }

}

