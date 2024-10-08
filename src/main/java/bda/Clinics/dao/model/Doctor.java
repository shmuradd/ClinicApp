package bda.Clinics.dao.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long doctorId;
    String fullName;
    String speciality;
    String photoUrl;
    String qualifications;
    Double experience;
    String service;
    String serviceDescription;
    Boolean isActive;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinTable(
            name = "doctor_clinic",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "clinic_id"))
    Set<Clinic> clinics;


    @OneToMany( cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctorId")
    Set<Review> reviews;


    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    Set<Schedule> schedules;
}
