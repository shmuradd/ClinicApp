package bda.Clinics.rest;

import bda.Clinics.dao.model.dto.request.RequestDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;
import bda.Clinics.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
@Slf4j
public class DoctorController {
    private final DoctorService doctorService;
    @GetMapping
    public ResponseEntity<List<ResponseDoctorDto>> findAll(@RequestBody RequestDoctorDto requestDoctorDto){
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialty(requestDoctorDto));
    }
    @GetMapping("/all")
    public List<ResponseDoctorDto> find(){
        return doctorService.findAll();
    }

}
