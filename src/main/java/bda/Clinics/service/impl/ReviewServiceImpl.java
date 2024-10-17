package bda.Clinics.service.impl;

import bda.Clinics.dao.model.Clinic;
import bda.Clinics.dao.model.Doctor;
import bda.Clinics.dao.model.Review;
import bda.Clinics.dao.model.dto.request.RequestReviewDto;
import bda.Clinics.dao.model.dto.response.ResponseReviewDto;
import bda.Clinics.dao.repository.ClinicRepository;
import bda.Clinics.dao.repository.DoctorRepository;
import bda.Clinics.dao.repository.ReviewRepository;
import bda.Clinics.service.ReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;


    public ReviewServiceImpl(ReviewRepository reviewRepository, @Qualifier("put") ModelMapper modelMapper, DoctorRepository doctorRepository, ClinicRepository clinicRepository) {
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
        this.doctorRepository = doctorRepository;
        this.clinicRepository = clinicRepository;
    }

    @Override
    public void saveReview(Long doctorId, RequestReviewDto requestReviewDto) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Review review = modelMapper.map(requestReviewDto, Review.class);
        doctor.getReviews().add(review);
        doctorRepository.save(doctor);
    }

    public void addReviewToDoctorByFullName(String fullName,
                                            String clinicName,
                                            RequestReviewDto requestReviewDto,
                                            String speciality) {
        Optional<Doctor> optionalDoctor = doctorRepository.findByFullName(fullName);
        Doctor doctor;
        if (optionalDoctor.isPresent()) {
            doctor = optionalDoctor.get();
        } else {
            doctor = Doctor.builder()
                    .fullName(fullName)
                    .speciality(speciality)
                    .isActive(true)
                    .build();
        }
        Optional<Clinic> optionalClinic = clinicRepository.findByClinicName(clinicName);
        Clinic clinic;
        if (optionalClinic.isPresent()) {
            clinic = optionalClinic.get();
        } else {
            clinic = Clinic.builder()
                    .clinicName(clinicName)
                    .isActive(true)
                    .build();
            clinicRepository.save(clinic);
        }

        Review review = modelMapper.map(requestReviewDto, Review.class);
        if (doctor.getReviews() == null) {
            doctor.setReviews(new HashSet<>());
        }
        doctor.getReviews().add(review);
        if (doctor.getClinics() == null) {
            doctor.setClinics(new HashSet<>());
        }
        doctor.getClinics().add(clinic);
        doctorRepository.save(doctor);
    }


    @Override
    public List<ResponseReviewDto> getReviewsByDoctorId(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        List<ResponseReviewDto> collect = doctor.getReviews().
                stream().
                map(review -> modelMapper.map(review, ResponseReviewDto.class)).
                collect(Collectors.toList());
        return collect;
    }

}
