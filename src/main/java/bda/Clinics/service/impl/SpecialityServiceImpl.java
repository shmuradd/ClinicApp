package bda.Clinics.service.impl;

import bda.Clinics.dao.model.entity.Speciality;
import bda.Clinics.dao.repository.SpecialityRepository;
import bda.Clinics.service.SpecialityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialityServiceImpl implements SpecialityService {

    @Autowired
    private SpecialityRepository specialityRepository;

    // Add a new speciality
    @Override
    public Speciality addSpeciality(String name) {
        if (specialityRepository.existsByName(name)) {
            throw new RuntimeException("Speciality already exists: " + name);
        }
        return specialityRepository.save(new Speciality(null, name, true));
    }

    // Delete a speciality
    @Override
    public void deleteSpeciality(Long id) {
        if (!specialityRepository.existsById(id)) {
            throw new RuntimeException("Speciality not found with ID: " + id);
        }
        specialityRepository.deleteById(id);
    }

    // Get all
    @Override
    public List<Speciality> getAllSpecialities() {
        return specialityRepository.findAll();
    }

    // Edit speciality name
    @Override
    public Speciality editSpeciality(Long id, String newName) {
        Speciality speciality = specialityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Speciality not found with ID: " + id));
        speciality.setName(newName);
        return specialityRepository.save(speciality);
    }

    // Toggle isActive field
    @Override
    public Speciality toggleActive(Long id) {
        Speciality speciality = specialityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Speciality not found with ID: " + id));
        speciality.setIsActive(!speciality.getIsActive());
        return specialityRepository.save(speciality);
    }

    @Override
    // Get active specialities
    public List<Speciality> getActiveSpecialities() {
        return specialityRepository.findByIsActiveTrue();
    }
}

