package bda.Clinics.service;

import bda.Clinics.dao.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientService {
    List<Patient> getAllPatients();
    Optional<Patient> getPatientById(Long patientId);
    Patient savePatient(Patient patient);
    void deletePatient(Long patientId);
    Patient updatePatient(Long patientId, Patient patient);
}
