package bda.Clinics.rest;

import bda.Clinics.dao.model.entity.Speciality;
import bda.Clinics.service.SpecialityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/specialities")
public class SpecialityController {

    @Autowired
    private SpecialityService specialityService;

    // Add a new speciality
    @PostMapping("/add")
    public ResponseEntity<Speciality> addSpeciality(@RequestParam String name) {
        Speciality speciality = specialityService.addSpeciality(name);
        return ResponseEntity.ok(speciality);
    }

    // Delete a speciality
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteSpeciality(@PathVariable Long id) {
        specialityService.deleteSpeciality(id);
        return ResponseEntity.ok("Speciality deleted successfully");
    }

    // List all specialities
    @GetMapping("/list")
    public ResponseEntity<List<Speciality>> getAllSpecialities() {
        return ResponseEntity.ok(specialityService.getAllSpecialities());
    }

    // Edit speciality
    @PutMapping("/{id}/edit")
    public ResponseEntity<Speciality> editSpeciality(@PathVariable Long id, @RequestParam String newName) {
        Speciality updatedSpeciality = specialityService.editSpeciality(id, newName);
        return ResponseEntity.ok(updatedSpeciality);
    }

    // Toggle isActive
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<Speciality> toggleSpecialityActive(@PathVariable Long id) {
        Speciality updatedSpeciality = specialityService.toggleActive(id);
        return ResponseEntity.ok(updatedSpeciality);
    }

    // Get active specialities
    @GetMapping("/active")
    public ResponseEntity<List<Speciality>> getActiveSpecialities() {
        List<Speciality> activeSpecialities = specialityService.getActiveSpecialities();
        return ResponseEntity.ok(activeSpecialities);
    }
}
