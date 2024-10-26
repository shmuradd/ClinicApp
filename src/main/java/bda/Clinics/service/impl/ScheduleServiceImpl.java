package bda.Clinics.service.impl;

import bda.Clinics.dao.model.Schedule;
import bda.Clinics.dao.repository.ScheduleRepository;
import bda.Clinics.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    @Override
    public Schedule getScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + scheduleId));
    }

    @Override
    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public Schedule updateSchedule(Long id, Schedule updatedSchedule) {
        Schedule existingSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + id));

        existingSchedule.setWeekDay(updatedSchedule.getWeekDay());
        existingSchedule.setWorkingHoursFrom(updatedSchedule.getWorkingHoursFrom());
        existingSchedule.setWorkingHoursTo(updatedSchedule.getWorkingHoursTo());

        return scheduleRepository.save(existingSchedule);
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
}
