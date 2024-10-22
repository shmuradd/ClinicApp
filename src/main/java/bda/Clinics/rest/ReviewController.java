package bda.Clinics.rest;

import bda.Clinics.dao.model.dto.request.RequestReviewDto;
import bda.Clinics.dao.model.dto.response.ResponseReviewDto;
import bda.Clinics.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://64.226.99.16:3000"}) // Both frontend URLs

public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public void addReview(@Param("id") Long doctorId,
                          @RequestBody RequestReviewDto requestReviewDto) {
        reviewService.saveReview(doctorId, requestReviewDto);
    }

    @PostMapping("/reviews")
    public void addReviewWithClinic(@RequestParam String fullName,
                                    @RequestParam String clinicName,
                                    @RequestBody RequestReviewDto requestReviewDto,
                                    @RequestParam String speciality) {
        reviewService.addReviewToDoctorByFullName(fullName, clinicName, requestReviewDto, speciality);
    }

    @GetMapping("/{doctorId}/reviews")
    public ResponseEntity<List<ResponseReviewDto>> getReviewsByDoctorId(@PathVariable Long doctorId) {
        List<ResponseReviewDto> reviews = reviewService.getReviewsByDoctorId(doctorId);
        return ResponseEntity.ok(reviews);
    }

}
