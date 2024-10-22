package bda.Clinics.rest;

import bda.Clinics.dao.model.Doctor;
import bda.Clinics.dao.model.Review;
import bda.Clinics.dao.model.dto.request.RequestDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;
import bda.Clinics.dao.repository.DoctorRepository;
import bda.Clinics.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://64.226.99.16:3000"}) // Both frontend URLs

public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorRepository doctorRepository;


    @GetMapping
    public ResponseEntity<List<ResponseDoctorDto>> findAll(
            @RequestBody RequestDoctorDto requestDoctorDto,
            @RequestParam(value = "sortBy", required = false) String sortBy) {

        // Retrieve the list of doctors
        List<ResponseDoctorDto> doctors = doctorService.getDoctorsBySpecialty(requestDoctorDto);

        // Sort by review count if requested
        if ("reviewCount".equalsIgnoreCase(sortBy)) {
            doctors.sort(Comparator.comparingInt((ResponseDoctorDto doctor) -> doctor.getReviews().size()).reversed());
        }
        // Sort by average rating if requested
        else if ("averageRating".equalsIgnoreCase(sortBy)) {
            doctors.sort(Comparator.comparingDouble((ResponseDoctorDto doctor) ->
                    doctor.getReviews().stream()
                            .mapToInt(review -> review.getRating())  // Replace method reference with lambda
                            .average()
                            .orElse(0.0)
            ).reversed());
        }

        // Return the sorted list
        return ResponseEntity.ok(doctors);
    }





    @GetMapping("/all")
    public List<ResponseDoctorDto> find(){
        return doctorService.findAll();
    }
    @GetMapping("/{doctorId}")
    public ResponseEntity<ResponseDoctorDto> getById(@PathVariable Long doctorId) {
        ResponseDoctorDto response = doctorService.getById(doctorId);
        return ResponseEntity.ok(response);

    }

}
