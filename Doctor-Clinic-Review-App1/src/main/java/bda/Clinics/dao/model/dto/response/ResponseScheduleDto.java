package bda.Clinics.dao.model.dto.response;

import bda.Clinics.dao.model.Schedule;
import bda.Clinics.dao.model.dto.request.RequestClinicDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseScheduleDto {

    Long scheduleId;
    String weekDay;
    LocalTime workingHoursFrom;
    LocalTime workingHoursTo;

}
