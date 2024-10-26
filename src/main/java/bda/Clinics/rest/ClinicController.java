package bda.Clinics.rest;

import bda.Clinics.dao.model.Clinic;
import bda.Clinics.dao.model.dto.response.ResponseClinicDto;
import bda.Clinics.service.ClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
@CrossOrigin(origins = {"http://localhost:3000", "http://64.226.99.16:3000"}) // Both frontend URLs

public class ClinicController {

    @Autowired
    private ClinicService clinicService;

    @GetMapping("/names")
    public List<ResponseClinicDto> getAllClinicNames() {
        return clinicService.getAllClinics(); // This should return List<ResponseClinicDto>
    }
}
