package bda.Clinics.service.impl;

import bda.Clinics.dao.model.Doctor;
import bda.Clinics.util.location.LocationOperation;
import bda.Clinics.dao.model.dto.request.RequestDoctorDto;
import bda.Clinics.dao.model.dto.response.ResponseDoctorDto;
import bda.Clinics.dao.repository.DoctorRepository;
import bda.Clinics.service.DoctorService;
import bda.Clinics.util.DoctorSpecification;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    @Qualifier("put")
    private final ModelMapper modelMapper;
    private final LocationOperation locationOperation;

    public DoctorServiceImpl(DoctorRepository doctorRepository,
                             @Qualifier("put") ModelMapper modelMapper,
                             LocationOperation locationOperation) {
        this.doctorRepository = doctorRepository;
        this.modelMapper = modelMapper;
        this.locationOperation = locationOperation;
    }
    @Override
    @Transactional
    public List<ResponseDoctorDto> getDoctorsBySpecialty(RequestDoctorDto requestDoctorDto) {
        double radiusInKm = 10.0;  // 10 km radius

        // Check if location is empty and set default Baku coordinates
        if (requestDoctorDto.getLocation() == null || requestDoctorDto.getLocation().isEmpty()) {
            requestDoctorDto.setLocation("Baku, Azerbaijan");
        }

        List<ResponseDoctorDto> doctorDtoList = doctorRepository.findAll(Specification
                        .allOf(DoctorSpecification.hasFullName(requestDoctorDto.getFullName()))
                        .or(DoctorSpecification.hasSpeciality(requestDoctorDto.getSpeciality()))
                        .or(DoctorSpecification.hasClinic(requestDoctorDto.getClinicName())))
                .stream()
                .map(doctor -> modelMapper.map(doctor, ResponseDoctorDto.class))
                .collect(Collectors.toList());

        if (!doctorDtoList.isEmpty()) {
            // Filter doctors by location and distance within 10 km
            return locationOperation.doctorSearchForLocationSpecWithinRadius(doctorDtoList, requestDoctorDto, radiusInKm);
        } else {
            List<Doctor> doctors = doctorRepository.findAll();
            List<ResponseDoctorDto> map = doctors.stream().map(doctor -> modelMapper.map(doctor, ResponseDoctorDto.class)).collect(Collectors.toList());
            return locationOperation.doctorSearchForLocationSpecWithinRadius(map, requestDoctorDto, radiusInKm);
        }
    }



    @Override
    @Transactional
    public List<ResponseDoctorDto> findAll() {
        return doctorRepository.findAll().stream().map(doctor -> modelMapper.map(doctor, ResponseDoctorDto.class)).collect(Collectors.toList());
    }

}


