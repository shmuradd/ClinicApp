package bda.Clinics.dao.model.dto.request;

import lombok.Data;

@Data
public class RequestScheduleDto {
    private String weekDay;
    private String workingHoursFrom;
    private String workingHoursTo;
}
