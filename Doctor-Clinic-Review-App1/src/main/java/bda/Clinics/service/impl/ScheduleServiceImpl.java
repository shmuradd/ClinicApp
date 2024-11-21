package bda.Clinics.service.impl;

import bda.Clinics.dao.model.Schedule;
import bda.Clinics.dao.model.dto.response.ResponseScheduleDto;
import bda.Clinics.dao.repository.ScheduleRepository;
import bda.Clinics.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, @Qualifier("put") ModelMapper modelMapper) {
        this.scheduleRepository = scheduleRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public List<ResponseScheduleDto> getAllSchedules() {
        return scheduleRepository.findAll().stream().map(schedule-> modelMapper.map(schedule,ResponseScheduleDto.class)).collect(Collectors.toList());
    }

    @Override
    public ResponseScheduleDto getScheduleById(Long scheduleId) {
        Schedule schedule= scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + scheduleId));
    return modelMapper.map(schedule,ResponseScheduleDto.class);
    }

    @Override
    public ResponseScheduleDto createSchedule(Schedule schedule) {
        Schedule newSchedule =  scheduleRepository.save(schedule);
         return modelMapper.map(newSchedule,ResponseScheduleDto.class);
    }

    @Override
    public ResponseScheduleDto updateSchedule(Long id, Schedule updatedSchedule) {
        Schedule existingSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + id));

        existingSchedule.setWeekDay(updatedSchedule.getWeekDay());
        existingSchedule.setWorkingHoursFrom(updatedSchedule.getWorkingHoursFrom());
        existingSchedule.setWorkingHoursTo(updatedSchedule.getWorkingHoursTo());

        Schedule newSchedule =  scheduleRepository.save(existingSchedule);
        return modelMapper.map(newSchedule,ResponseScheduleDto.class);
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
}