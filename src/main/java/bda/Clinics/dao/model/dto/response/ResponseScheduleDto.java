package bda.Clinics.dao.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

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
