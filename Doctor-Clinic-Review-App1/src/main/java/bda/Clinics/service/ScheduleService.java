package bda.Clinics.service;

import bda.Clinics.dao.model.Schedule;
import bda.Clinics.dao.model.dto.response.ResponseScheduleDto;

import java.util.List;

public interface ScheduleService {
    List<ResponseScheduleDto> getAllSchedules();
    ResponseScheduleDto getScheduleById(Long scheduleId);
    ResponseScheduleDto createSchedule(Schedule schedule);
    ResponseScheduleDto updateSchedule(Long scheduleId, Schedule schedule);
    void deleteSchedule(Long scheduleId);
}