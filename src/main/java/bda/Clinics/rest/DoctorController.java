package bda.Clinics.rest;

import bda.Clinics.dao.model.Review;
import bda.Clinics.dao.model.dto.request.RequestDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseReviewDto;
import bda.Clinics.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000") // Frontend URL

public class DoctorController {
    private final DoctorService doctorService;
    @GetMapping
    public ResponseEntity<List<ResponseDoctorDto>> findAll(@RequestBody RequestDoctorDto requestDoctorDto,
                                                           @RequestParam(value = "sortBy", required = false) String sortBy){
        // Get the list of doctors
        List<ResponseDoctorDto> doctors = doctorService.getDoctorsBySpecialty(requestDoctorDto);

        // Sort by review count if requested
        if ("reviewCount".equalsIgnoreCase(sortBy)) {
            doctors.sort(Comparator.comparingInt(doctor -> ((ResponseDoctorDto) doctor).getReviews().size()).reversed());
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
        ResponseDoctorDto doctor = doctorService.getDoctorById(doctorId);
        if (doctor != null) {
            return ResponseEntity.ok(doctor);
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if doctor not found
        }
    }




}
