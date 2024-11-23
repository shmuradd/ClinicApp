package bda.Clinics.dao.repository;

import bda.Clinics.dao.model.entity.Clinic;
import bda.Clinics.dao.model.entity.Doctor;
import bda.Clinics.dao.model.entity.Schedule;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    Optional<Schedule> findByDoctorAndClinicAndWeekDayAndWorkingHoursFromAndWorkingHoursTo(Doctor doctor, Clinic clinic, String weekDay, LocalTime parse, LocalTime parse1);

    Optional<Schedule> findByClinicAndDoctorAndWeekDay(Clinic clinic, Doctor doctor, String weekDay);

    void deleteByClinic(Clinic clinicToRemove);

    void deleteByClinicAndDoctor(Clinic clinicToRemove, Doctor doctor);

    void deleteByDoctorAndClinic(Doctor doctor, Clinic clinic);

    List<Schedule> findByDoctorAndClinic(Doctor doctor, Clinic clinic);

    // Method to find all schedules by clinicId
    List<Schedule> findByClinic_ClinicId(Long clinicId);

    // Method to delete schedules by clinicId
    void deleteByClinic_ClinicId(Long clinicId);
    // Custom query to delete schedules by doctorId and clinicId
    @Modifying
    @Transactional
    @Query("DELETE FROM Schedule s WHERE s.clinic.clinicId = :clinicId AND s.doctor.doctorId = :doctorId")
    void deleteByClinicAndDoctor(@Param("clinicId") Long clinicId, @Param("doctorId") Long doctorId);
}
