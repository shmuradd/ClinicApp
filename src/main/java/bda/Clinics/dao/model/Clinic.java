package bda.Clinics.dao.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long clinicId;

    String clinicName;
    String location;
    String contactDetails;
    String city;

    Boolean isActive;
    @OneToMany(mappedBy ="clinic",cascade = CascadeType.ALL)
    Set<Schedule> schedules;
}
