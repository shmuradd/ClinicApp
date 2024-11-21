package bda.Clinics.rest;

import bda.Clinics.dao.model.Schedule;
import bda.Clinics.dao.model.dto.request.RequestScheduleDto;
import bda.Clinics.dao.model.dto.response.ResponseScheduleDto;
import bda.Clinics.service.ClinicService;
import bda.Clinics.service.DoctorService;
import bda.Clinics.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://64.226.99.16:3000"}) // Both frontend URLs

public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ClinicService clinicService;
    private final DoctorService doctorService;


    @GetMapping
    public ResponseEntity<List<ResponseScheduleDto>> getAllSchedules() {
        List<ResponseScheduleDto> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseScheduleDto> getScheduleById(@PathVariable Long id) {
        ResponseScheduleDto schedule = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(schedule);
    }

    @PostMapping
    public ResponseEntity<ResponseScheduleDto> createSchedule(@RequestBody RequestScheduleDto requestScheduleDto) {
        ResponseScheduleDto createdSchedule = scheduleService.createSchedule(mapToSchedule(requestScheduleDto));
        return ResponseEntity.ok(createdSchedule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseScheduleDto> updateSchedule(@PathVariable Long id, @RequestBody RequestScheduleDto requestScheduleDto) {
        ResponseScheduleDto updatedSchedule = scheduleService.updateSchedule(id, mapToSchedule(requestScheduleDto));
        return ResponseEntity.ok(updatedSchedule);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{clinicId}/schedules")
    public ResponseEntity<Void> addScheduleToClinic(
            @PathVariable Long clinicId,
            @RequestBody RequestScheduleDto requestScheduleDto) {

        Schedule schedule = mapToSchedule(requestScheduleDto);

        clinicService.addScheduleToClinic(clinicId, schedule);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{doctorId}/schedules")
    public ResponseEntity<Void> addScheduleToDoctor(
            @PathVariable Long doctorId,
            @RequestBody RequestScheduleDto requestScheduleDto) {

        Schedule schedule = mapToSchedule(requestScheduleDto);

        doctorService.addScheduleToDoctor(doctorId, schedule);
        return ResponseEntity.ok().build();
    }
    private Schedule mapToSchedule(RequestScheduleDto dto) {
        return Schedule.builder()
                .weekDay(dto.getWeekDay())
                .workingHoursFrom(LocalTime.parse(dto.getWorkingHoursFrom())) // "hh:mm" format
                .workingHoursTo(LocalTime.parse(dto.getWorkingHoursTo()))     // "hh:mm" format
                .build();
    }

}
