package bda.Clinics.rest;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://64.226.99.16:3000",
        "https://topdoc.com.az/",
        "https://topdoc.com.az"}) // Both frontend URLs

public class ReviewController {
    private final ReviewService reviewService;

    private final DoctorService doctorService;
    private final ReviewRepository reviewRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final ClinicService clinicService;

    @GetMapping("/{reviewId}")
    public ResponseEntity<ResponseReviewDto> getReviewById(@PathVariable Long reviewId) {
        ResponseReviewDto reviewResponse = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(reviewResponse);
    }

    @PostMapping
    public void addReview(@RequestParam("id") Long doctorId,
                          @RequestBody RequestReviewDto requestReviewDto) {
        reviewService.saveReview(doctorId, requestReviewDto);
    }


    @PostMapping("/reviews")
    public ResponseEntity<String> addReviewWithClinic(
            @RequestParam String fullName,
            @RequestParam String speciality,
            @RequestParam String clinicName,
            @RequestBody RequestReviewDto requestReviewDto) {

        reviewService.addReviewWithClinic(fullName, speciality, clinicName, requestReviewDto);
        return ResponseEntity.ok("Review submitted and awaiting admin approval.");
    }


    @GetMapping("/{doctorId}/reviews")
    public ResponseEntity<List<ResponseReviewDto>> getReviewsByDoctorId(@PathVariable Long doctorId) {
        List<ResponseReviewDto> reviews = reviewService.getReviewsByDoctorId(doctorId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Review>> getPendingReviews() {
        List<Review> pendingReviews = reviewService.getPendingReviews();
        return ResponseEntity.ok(pendingReviews);
    }

    @PutMapping("/{reviewId}/status")
    public ResponseEntity<String> updateReviewStatus(@PathVariable Long reviewId,
                                                     @RequestParam ReviewStatus newStatus) {
        try {
            reviewService.updateReviewStatus(reviewId, newStatus);
            return ResponseEntity.ok("Review status updated successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{parentReviewId}/reply")
    public ResponseEntity<String> addReplyToReview(@PathVariable Long parentReviewId,
                                                   @RequestBody RequestReviewDto replyDto) {
        reviewService.addReplyToReview(parentReviewId, replyDto);
        return ResponseEntity.ok("Reply added successfully.");
    }

    @GetMapping("/{parentReviewId}/replies")
    public ResponseEntity<List<ResponseReviewDto>> getRepliesToReview(@PathVariable Long parentReviewId) {
        List<ResponseReviewDto> replies = reviewService.getRepliesToReview(parentReviewId);
        return ResponseEntity.ok(replies);
    }
    @GetMapping("/all")
    public ResponseEntity<List<ResponseReviewDto>> getAllReviews() {
        List<ResponseReviewDto> allReviews = reviewService.getAllReviews();
        return ResponseEntity.ok(allReviews);
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<Review> toggleReviewStatus(@PathVariable Long id) {
        Review updatedReview = reviewService.toggleReviewStatus(id);
        return ResponseEntity.ok(updatedReview);
    }

    @GetMapping("/top-approved")
    public List<ResponseReviewDto> getTop3ApprovedReviews() {
        return reviewService.findTop3ApprovedReviews();
    }

}

