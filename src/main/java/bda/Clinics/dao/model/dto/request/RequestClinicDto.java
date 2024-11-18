package bda.Clinics.dao.model.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class RequestClinicDto {
     Long clinicId;
     String clinicName;
     String location;
     String contactDetails;
     String city;
     Double distance;
     List<RequestScheduleDto> schedules; // List of schedules for this clinic

     public Double getDistance() {
          return distance != null ? distance : Double.MAX_VALUE;
     }

}

