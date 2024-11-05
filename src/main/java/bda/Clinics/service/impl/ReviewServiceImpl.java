package bda.Clinics.service.impl;

import bda.Clinics.dao.model.entity.Clinic;
import bda.Clinics.dao.model.entity.Doctor;
import bda.Clinics.dao.model.entity.Review;
import bda.Clinics.dao.model.dto.request.RequestReviewDto;
import bda.Clinics.dao.model.dto.response.ResponseReviewDto;
import bda.Clinics.dao.model.enums.ReviewStatus;
import bda.Clinics.dao.repository.ClinicRepository;
import bda.Clinics.dao.repository.DoctorRepository;
import bda.Clinics.dao.repository.ReviewRepository;
import bda.Clinics.service.ClinicService;
import bda.Clinics.service.DoctorService;
import bda.Clinics.service.ReviewService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final ClinicService clinicService;
    private final DoctorService doctorService;
    public ReviewServiceImpl(ReviewRepository reviewRepository, @Qualifier("put") ModelMapper modelMapper, DoctorRepository doctorRepository, ClinicRepository clinicRepository, ClinicService clinicService, DoctorService doctorService) {
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
        this.doctorRepository = doctorRepository;
        this.clinicRepository = clinicRepository;
        this.clinicService = clinicService;
        this.doctorService = doctorService;
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

    public void addReviewToDoctorByFullName(String fullName, String clinicName, RequestReviewDto requestReviewDto, String speciality) {
        Doctor doctor = doctorRepository.findByFullName(fullName)
                .orElseGet(() -> createInactiveDoctor(fullName, speciality));

        Clinic clinic = clinicRepository.findByClinicName(clinicName)
                .orElseGet(() -> createInactiveClinic(clinicName));

        if (doctor.getClinics() == null) {
            doctor.setClinics(new HashSet<>());
        }
        // Associate clinic with doctor, if not already associated
        doctor.getClinics().add(clinic);

        // Map and save the review with pending status
        Review review = modelMapper.map(requestReviewDto, Review.class);
        review.setStatus(ReviewStatus.PENDING);
        review.setDoctor(doctor);

        reviewRepository.save(review);
        doctorRepository.save(doctor);  // Save updated doctor with the new review and clinic association
    }

    @Transactional
    @Override
    public void addReviewWithClinic(String fullName, String speciality, String clinicName, RequestReviewDto requestReviewDto) {
        Doctor doctor = doctorService.findDoctorByFullNameAndSpeciality(fullName, speciality)
                .orElseGet(() -> createInactiveDoctor(fullName, speciality));

        Clinic clinic = clinicRepository.findByClinicName(clinicName)
                .orElseGet(() -> createInactiveClinic(clinicName));

        if (doctor.getClinics() == null) {
            doctor.setClinics(new HashSet<>());
        }
        doctor.getClinics().add(clinic);
        // Map and save the review with pending status
        Review review = modelMapper.map(requestReviewDto, Review.class);
        review.setStatus(ReviewStatus.PENDING);
        review.setDoctor(doctor);

        reviewRepository.save(review);
        doctorRepository.save(doctor);  // Save doctor with updated clinic relationship
    }

    @Override
    public ResponseReviewDto getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        return ResponseReviewDto.builder()
                .reviewId(review.getReviewId())
                .fullName(review.getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .status(review.getStatus())
                .reviewDate(review.getReviewDate())
                .isActive(review.getIsActive())
                .doctorId(review.getDoctor().getDoctorId())
                .doctorFullName(review.getDoctor().getFullName())
                .doctorSpeciality(review.getDoctor().getSpeciality())
                .doctorIsActive(review.getDoctor().getIsActive())
                .build();
    }

    private Doctor createInactiveDoctor(String fullName, String speciality) {
        Doctor doctor = Doctor.builder()
                .fullName(fullName)
                .speciality(speciality)
                .isActive(false)  // Defaults to inactive until admin activates
                .build();
        return doctorService.createDoctor(doctor);
    }

    private Clinic createInactiveClinic(String clinicName) {
        Clinic clinic = Clinic.builder()
                .clinicName(clinicName)
                .isActive(false)  // Defaults to inactive until admin activates
                .build();
        return clinicRepository.save(clinic);
    }

    public void addReviewToDoctor(Doctor doctor, RequestReviewDto requestReviewDto) {
        Review review = Review.builder()
                .fullName(requestReviewDto.getFullName())
                .rating(requestReviewDto.getRating())
                .comment(requestReviewDto.getComment())
                .status(ReviewStatus.PENDING)  // Set review to PENDING
                .doctor(doctor)
                .build();

        reviewRepository.save(review);  // Save review independently
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
        // Retrieve the review by its ID
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Check if the doctor associated with the review is active
        if (newStatus == ReviewStatus.APPROVED && !review.getDoctor().getIsActive()) {
            throw new IllegalStateException("Cannot publish review: Doctor is inactive.");
        }

        // Update the review status and save it
        review.setStatus(newStatus);
        reviewRepository.save(review);
    }
    @Override
    public void addReplyToReview(Long parentReviewId, RequestReviewDto replyDto) {
        Review parentReview = reviewRepository.findById(parentReviewId)
                .orElseThrow(() -> new RuntimeException("Parent review not found with ID: " + parentReviewId));

        // Map the DTO to a Review entity for the reply
        Review reply = modelMapper.map(replyDto, Review.class);
        reply.setParentReview(parentReview);
        reply.setStatus(ReviewStatus.PENDING); // Set default status

        // Save the reply
        reviewRepository.save(reply);

        log.info("Reply saved with parent review ID: " + parentReviewId);
    }

    @Override
    public List<ResponseReviewDto> getRepliesToReview(Long parentReviewId) {
        Review parentReview = reviewRepository.findById(parentReviewId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Review not found"));

        // Fetch replies using the parent review reference
        List<Review> replies = reviewRepository.findByParentReview(parentReview);

        // Convert replies to DTOs and return
        return replies.stream()
                .map(reply -> modelMapper.map(reply, ResponseReviewDto.class)) // Convert replies to DTOs
                .collect(Collectors.toList());
    }
    @Override
    public List<ResponseReviewDto> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();

        return reviews.stream()
                .map(review -> modelMapper.map(review, ResponseReviewDto.class))
                .sorted((r1, r2) -> {
                    // Assuming you have a method getStatus() in ResponseReviewDto
                    if ("PENDING".equals(r1.getStatus()) && !"PENDING".equals(r2.getStatus())) {
                        return -1; // r1 comes first
                    } else if (!"PENDING".equals(r1.getStatus()) && "PENDING".equals(r2.getStatus())) {
                        return 1; // r2 comes first
                    }
                    return 0; // keep the same order if both are pending or neither is pending
                })
                .collect(Collectors.toList());
    }

    @Override
    public Review toggleReviewStatus(Long reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setIsActive(!review.getIsActive()); // Toggle isActive status
            return reviewRepository.save(review); // Save updated review
        } else {
            throw new RuntimeException("Review not found with ID: " + reviewId);
        }
    }

}