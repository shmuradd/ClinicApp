package bda.Clinics.service;

import bda.Clinics.dao.model.Review;
import bda.Clinics.dao.model.dto.request.RequestReviewDto;
import bda.Clinics.dao.model.dto.response.ResponseReviewDto;
import bda.Clinics.dao.model.enums.ReviewStatus;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    void saveReview(Long doctorId, RequestReviewDto requestReviewDto);
    void addReviewToDoctorByFullName(String fullName,String clinicName, RequestReviewDto reviewDto, String speciality);

    List<ResponseReviewDto> getReviewsByDoctorId(Long doctorId);

    List<Review> getPendingReviews();
    void updateReviewStatus(Long reviewId, ReviewStatus newStatus);
    public void addReplyToReview(Long parentReviewId, RequestReviewDto replyDto);
}