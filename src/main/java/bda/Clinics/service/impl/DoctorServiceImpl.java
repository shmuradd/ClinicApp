package bda.Clinics.service.impl;

import bda.Clinics.dao.model.Clinic;
import bda.Clinics.dao.model.Doctor;
import bda.Clinics.dao.model.Review;
import bda.Clinics.dao.model.dto.request.RequestClinicDto;
import bda.Clinics.dao.model.dto.response.ResponseReviewDto;
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

    @Override
    @Transactional
    public ResponseDoctorDto getDoctorById(Long doctorId) {
        // Fetch the doctor by ID from the database
        // This assumes you have a repository to handle the data fetching.
        Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);
        return doctorOptional.map(this::convertToDto).orElse(null);
    }

    private ResponseDoctorDto convertToDto(Doctor doctor) {
        // Map the attributes from Doctor entity to ResponseDoctorDto
        ResponseDoctorDto responseDto = ResponseDoctorDto.builder()
                .fullName(doctor.getFullName())
                .speciality(doctor.getSpeciality())
                .qualifications(doctor.getQualifications())
                .experience(doctor.getExperience())
                .service(doctor.getService())
                .serviceDescription(doctor.getServiceDescription())
                .photoUrl(doctor.getPhotoUrl())
                .clinics(convertClinicsToDto(doctor.getClinics()))
                .reviews(convertReviewsToDto(doctor.getReviews()))
                .build();

        return responseDto;
    }
    private Set<RequestClinicDto> convertClinicsToDto(Set<Clinic> clinics) {
        return clinics.stream()
                .map(clinic -> RequestClinicDto.builder() // Assuming you have a RequestClinicDto with necessary fields
                        .clinicId(clinic.getClinicId()) // Replace with actual field name
                        .clinicName(clinic.getClinicName()) // Replace with actual field name
                        .location(clinic.getLocation()) // Replace with actual field name
                        .build())
                .collect(Collectors.toSet());
    }
    private Set<ResponseReviewDto> convertReviewsToDto(Set<Review> reviews) {
        return reviews.stream()
                .map(review -> ResponseReviewDto.builder() // Assuming you have a ResponseReviewDto with necessary fields
                        .rating(review.getRating()) // Replace with actual field name
                        .comments(review.getComments()) // Replace with actual field name
                        .reviewDate(review.getReviewDate()) // Replace with actual field name
                        .build())
                .collect(Collectors.toSet());
    }
}


