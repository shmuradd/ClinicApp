package bda.Clinics.service;


import bda.Clinics.dao.model.entity.Speciality;

import java.util.List;

public interface SpecialityService {
    Speciality addSpeciality(String name);

    void deleteSpeciality(Long id);

    List<Speciality> getAllSpecialities();

    Speciality editSpeciality(Long id, String newName);

    Speciality toggleActive(Long id);

    List<Speciality> getActiveSpecialities();
}
