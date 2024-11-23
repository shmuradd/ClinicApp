package bda.Clinics.dao.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Objects;
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
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "clinic_id", referencedColumnName = "clinicId")
    Set<Schedule> schedules=new HashSet<>();;

    @ManyToMany(mappedBy = "clinics", fetch = FetchType.LAZY)
    @JsonBackReference
    Set<Doctor> doctors = new HashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clinic clinic = (Clinic) o;
        return Objects.equals(clinicName, clinic.clinicName) && Objects.equals(location, clinic.location) && Objects.equals(contactDetails, clinic.contactDetails) && Objects.equals(city, clinic.city) && Objects.equals(isActive, clinic.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clinicName, location, contactDetails, city, isActive);
    }

    // Optional utility method to add a doctor
    public void addDoctor(Doctor doctor) {
        this.doctors.add(doctor);
    }

    public void removeDoctor(Doctor doctorToRemove) {
        if (doctorToRemove != null && this.doctors != null) {
            // Remove the clinic from the doctor's clinic list
            this.doctors.remove(doctorToRemove);

            // Break the bidirectional relationship by setting the doctor reference to null
            //clinicToRemove.setDoctor(null);
        }
    }

}
