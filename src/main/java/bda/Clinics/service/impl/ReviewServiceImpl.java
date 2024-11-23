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
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
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

        boolean duplicateReview = doctor.getReviews().stream()
                .anyMatch(existingReview ->
                        existingReview.getFullName().equals(requestReviewDto.getFullName()) &&
                                existingReview.getComment().equals(requestReviewDto.getComment()) &&
                                existingReview.getRating() == requestReviewDto.getRating() &&
                                existingReview.getDoctor().getDoctorId().equals(doctorId) &&
                                existingReview.getReviewDate() != null &&
                                existingReview.getReviewDate().after(new Date(System.currentTimeMillis() - 5 * 60 * 1000)) // 5-minute window
                );


        if (duplicateReview) {
            throw new IllegalStateException("You have already submitted a similar review recently.");
        }

        Review review = modelMapper.map(requestReviewDto, Review.class);
        review.setStatus(ReviewStatus.PENDING);
        review.setIsActive(false);
        doctor.getReviews().add(review);
        reviewRepository.save(review);
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
        // Step 1: Check if the doctor with the given speciality exists
        Optional<Doctor> existingDoctorOpt = doctorRepository.findBySpecialityAndFullName(speciality,fullName);

        Doctor doctor;
        if (existingDoctorOpt.isPresent()) {
            doctor = existingDoctorOpt.get();
        } else {
            // Step 2: If doctor doesn't exist, create a new doctor with isActive = false
            doctor = new Doctor();
            doctor.setFullName(fullName);
            doctor.setSpeciality(speciality);
            doctor.setIsActive(false);  // Set the doctor as inactive
            doctorRepository.save(doctor);
        }

        // Step 3: Check if the clinic with the given clinicName already exists in the doctor's clinics
        Optional<Clinic> existingClinicOpt = doctor.getClinics().stream()
                .filter(clinic -> clinic.getClinicName().equals(clinicName))
                .findFirst();

        Clinic clinic;
        if (existingClinicOpt.isPresent()) {
            // If the clinic exists, use the existing clinic
            clinic = existingClinicOpt.get();
        } else {
            // Step 4: If the clinic doesn't exist, create a new clinic and associate it with the doctor
            clinic = new Clinic();
            clinic.setClinicName(clinicName);
            clinic.setLocation("https://www.google.com/maps/place/Bak%C3%BC/@40.394737,49.6901489,40414m/data=!3m2!1e3!4b1!4m6!3m5!1s0x40307d6bd6211cf9:0x343f6b5e7ae56c6b!8m2!3d40.4092617!4d49.8670924!16zL20vMDFnZjU?entry=ttu&g_ep=EgoyMDI0MTAyOS4wIKXMDSoASAFQAw%3D%3D");
            clinic.addDoctor(doctor);  // Set the doctor reference in the clinic
            clinic.setIsActive(false);  // Set the clinic as inactive
            // Add clinic to the doctor's clinics list (assuming it's a collection)
            doctor.getClinics().add(clinic);

            // Save the clinic
            clinicRepository.save(clinic);

            // Save the updated doctor with the new clinic
            doctorRepository.save(doctor);
        }

        // Step 5: Create the review and associate it with the clinic
        Review review = new Review();
        review.setRating(requestReviewDto.getRating());
        review.setComment(requestReviewDto.getComment());
        review.setFullName(requestReviewDto.getFullName());


        review.setStatus(ReviewStatus.PENDING);
        review.setDoctor(doctor);
        review.setIsActive(false);  // Set the review as inactive
        // Save the review
        reviewRepository.save(review);

        // Log the review creation (optional)
        log.info("Review added for clinic {} of doctor {} by {}.", clinicName, doctor.getFullName(), fullName);
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

        return reviews.stream().map(review -> {
            ResponseReviewDto dto = new ResponseReviewDto();

            // Map review fields
            dto.setReviewId(review.getReviewId());
            dto.setRating(review.getRating());
            dto.setComment(review.getComment());
            dto.setReviewDate(review.getReviewDate());
            dto.setFullName(review.getFullName());
            dto.setStatus(review.getStatus());
            dto.setIsActive(review.getIsActive());

            // Map doctor fields if the doctor is present
            if (review.getDoctor() != null) {
                dto.setDoctorId(review.getDoctor().getDoctorId());
                dto.setDoctorFullName(review.getDoctor().getFullName());
                dto.setDoctorSpeciality(review.getDoctor().getSpeciality());
                dto.setDoctorIsActive(review.getDoctor().getIsActive());
            }

            return dto;
        }).sorted((r1, r2) -> {
            // Custom sorting for status: PENDING first, then REJECTED, then APPROVED
            int statusComparison = getStatusOrder(r1.getStatus()) - getStatusOrder(r2.getStatus());

            // If statuses are different, sort by status
            if (statusComparison != 0) {
                return statusComparison;
            }

            // If statuses are the same, sort by reviewId in descending order (newest first)
            return r2.getReviewId().compareTo(r1.getReviewId());
        }).collect(Collectors.toList());
    }

    // Helper method to define the custom order for statuses
    private int getStatusOrder(ReviewStatus status) {
        switch (status) {
            case PENDING: return 1;
            case REJECTED: return 2;
            case APPROVED: return 3;
            default: return 4;
        }
    }


    @PostConstruct
    public void configureModelMapper() {
        modelMapper.typeMap(Review.class, ResponseReviewDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getDoctor().getDoctorId(), ResponseReviewDto::setDoctorId);
            mapper.map(src -> src.getDoctor().getFullName(), ResponseReviewDto::setDoctorFullName);
            mapper.map(src -> src.getDoctor().getSpeciality(), ResponseReviewDto::setDoctorSpeciality);
            mapper.map(src -> src.getDoctor().getIsActive(), ResponseReviewDto::setDoctorIsActive);
        });
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

    @Override
    // Fetch Top 3 Approved Reviews
    public List<ResponseReviewDto> findTop3ApprovedReviews() {
        // Fetch reviews and sort them by review date in descending order
        return reviewRepository.findAll().stream()
                .filter(review -> review.getStatus() == ReviewStatus.APPROVED) // Filter approved reviews
                .sorted((r1, r2) -> r2.getReviewDate().compareTo(r1.getReviewDate())) // Sort by reviewDate in descending order
                .limit(3) // Limit to top 3
                .map(review -> {
                    // Extract doctor details
                    String doctorFullName = review.getDoctor() != null ? review.getDoctor().getFullName() : "No doctor assigned";
                    String doctorSpeciality = review.getDoctor() != null ? review.getDoctor().getSpeciality() : "No speciality assigned";
                    Boolean doctorIsActive = review.getDoctor() != null ? review.getDoctor().getIsActive() : false;
                    Long doctorId = review.getDoctor() != null ? review.getDoctor().getDoctorId() : null;

                    // Map review to ResponseReviewDto with doctor details
                    return ResponseReviewDto.builder()
                            .reviewId(review.getReviewId())
                            .rating(review.getRating())
                            .comment(review.getComment())
                            .reviewDate(review.getReviewDate())
                            .fullName(review.getFullName())
                            .status(review.getStatus())
                            .isActive(review.getIsActive())
                            .doctorId(doctorId)
                            .doctorFullName(doctorFullName)
                            .doctorSpeciality(doctorSpeciality)
                            .doctorIsActive(doctorIsActive)
                            .build();
                })
                .collect(Collectors.toList());
    }


}

//public void addReviewWithClinic(String fullName, String speciality, String clinicName, RequestReviewDto requestReviewDto) {
//    Doctor doctor = doctorService.findDoctorByFullNameAndSpeciality(fullName, speciality)
//            .orElseGet(() -> createInactiveDoctor(fullName, speciality));
//
//    Clinic clinic = clinicRepository.findByClinicName(clinicName)
//            .orElseGet(() -> createInactiveClinic(clinicName));
//
//    if (doctor.getClinics() == null) {
//        doctor.setClinics(new HashSet<>());
//    }
//    doctor.getClinics().add(clinic);
//    // Map and save the review with pending status
//    Review review = modelMapper.map(requestReviewDto, Review.class);
//    review.setStatus(ReviewStatus.PENDING);
//    review.setDoctor(doctor);
//
//    reviewRepository.save(review);
//    doctorRepository.save(doctor);  // Save doctor with updated clinic relationship
//}