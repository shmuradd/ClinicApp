package bda.Clinics.service.impl;

import bda.Clinics.dao.model.Clinic;
import bda.Clinics.dao.model.Doctor;
import bda.Clinics.dao.model.Review;
import bda.Clinics.dao.model.dto.request.RequestReviewDto;
import bda.Clinics.dao.model.dto.response.ResponseReviewDto;
import bda.Clinics.dao.model.enums.ReviewStatus;
import bda.Clinics.dao.repository.ClinicRepository;
import bda.Clinics.dao.repository.DoctorRepository;
import bda.Clinics.dao.repository.ReviewRepository;
import bda.Clinics.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);


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
        review.setStatus(ReviewStatus.PENDING);
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
                    .isActive(false)
                    .build();
        }
        Optional<Clinic> optionalClinic = clinicRepository.findByClinicName(clinicName);
        Clinic clinic;
        if (optionalClinic.isPresent()) {
            clinic = optionalClinic.get();
        } else {
            clinic = Clinic.builder()
                    .clinicName(clinicName)
                    .isActive(false)
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

        review.setStatus(ReviewStatus.PENDING);
        reviewRepository.save(review);

        log.info("Review saved in PENDING status for Doctor: " + fullName);
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
    @Override
    public List<Review> getPendingReviews() {
        return reviewRepository.findByStatus(ReviewStatus.PENDING);
    }

    @Override
    public void updateReviewStatus(Long reviewId, ReviewStatus newStatus) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + reviewId));

        review.setStatus(newStatus);
        reviewRepository.save(review);
    }

}
