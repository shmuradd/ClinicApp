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
    if (requestDoctorDto == null) {
        throw new IllegalArgumentException("Request data cannot be null");
    }

    Specification<Doctor> spec = Specification
            .where(DoctorSpecification.hasFullName(requestDoctorDto.getFullName()))
            .and(DoctorSpecification.hasSpeciality(requestDoctorDto.getSpeciality()))
            .and(DoctorSpecification.hasClinic(requestDoctorDto.getClinicName()));
//            .and(DoctorSpecification.hasMoreThanXReviews(requestDoctorDto.getReviewCount()))
//            .and(DoctorSpecification.hasMoreThanXRating(requestDoctorDto.getRatingCount()));

    List<ResponseDoctorDto> responseDoctorDtoList = doctorRepository.findAll(spec)
            .stream()
            .map(doctor -> modelMapper.map(doctor, ResponseDoctorDto.class))
            .collect(Collectors.toList());

    if (responseDoctorDtoList.isEmpty()) {
        return Collections.emptyList();
    }

    return locationOperation.doctorSearchForLocationSpec(responseDoctorDtoList, requestDoctorDto);
}


    @Override
    @Transactional
    public List<ResponseDoctorDto> findAll() {
        return doctorRepository.findAll().stream().map(doctor -> modelMapper.map(doctor, ResponseDoctorDto.class)).collect(Collectors.toList());
    }

}


