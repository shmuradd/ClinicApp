package bda.Clinics.dao.model.dto.request;

import lombok.Data;

@Data
public class RequestScheduleDto {
    private String weekDay;
    private String workingHoursFrom;
    private String workingHoursTo;
    private Long doctorId;  // ID of the doctor
    private Long clinicId;  // ID of the clinic
}
