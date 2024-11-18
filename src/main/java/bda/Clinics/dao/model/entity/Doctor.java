package bda.Clinics.dao.model.entity;

import bda.Clinics.service.ClinicService;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Getter
@NamedEntityGraph(name = "doctor.clinics",
        attributeNodes = @NamedAttributeNode("clinics") )
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long doctorId;
    String fullName;
    String speciality;
    String photoUrl;
    String qualifications;
    Double experience;
    String service;
    @Lob
    @Column(name = "service_description", columnDefinition = "TEXT") // Optional column definition for specific DB types
    private String serviceDescription;    Boolean isActive;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinTable(
            name = "doctor_clinic",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "clinic_id"))
    Set<Clinic> clinics=new HashSet<>();


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctorId")
    @JsonBackReference
    Set<Review> reviews=new HashSet<>();


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctorId")
    @JsonBackReference
    Set<Schedule> schedules=new HashSet<>();

    public boolean isActive() {
        return this.isActive;
    }
    // Method to add a clinic to the doctor
    public void addClinics(Clinic clinic) {
        this.clinics.add(clinic);
    }

}