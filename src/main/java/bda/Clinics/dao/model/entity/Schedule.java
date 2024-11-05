package bda.Clinics.dao.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long scheduleId;
    String weekDay;
    LocalTime workingHoursFrom;
    LocalTime workingHoursTo;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return Objects.equals(weekDay, schedule.weekDay) && Objects.equals(workingHoursFrom, schedule.workingHoursFrom) && Objects.equals(workingHoursTo, schedule.workingHoursTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekDay, workingHoursFrom, workingHoursTo);
    }
}
