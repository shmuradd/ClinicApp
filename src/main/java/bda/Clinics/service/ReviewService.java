package bda.Clinics.service;

import bda.Clinics.dao.model.entity.Doctor;
import bda.Clinics.dao.model.entity.Review;
import bda.Clinics.dao.model.dto.request.RequestReviewDto;
import bda.Clinics.dao.model.dto.response.ResponseReviewDto;
import bda.Clinics.dao.model.enums.ReviewStatus;

import java.util.List;

public interface ReviewService {
    void saveReview(Long doctorId, RequestReviewDto requestReviewDto);
    void addReviewToDoctorByFullName(String fullName,String clinicName, RequestReviewDto reviewDto, String speciality);

    List<ResponseReviewDto> getReviewsByDoctorId(Long doctorId);

    List<Review> getPendingReviews();
    void updateReviewStatus(Long reviewId, ReviewStatus newStatus);
    public void addReplyToReview(Long parentReviewId, RequestReviewDto replyDto);
    public List<ResponseReviewDto> getRepliesToReview(Long parentReviewId);
    public List<ResponseReviewDto> getAllReviews();
    public Review toggleReviewStatus(Long reviewId);
    public void addReviewToDoctor(Doctor doctor, RequestReviewDto requestReviewDto);
    public void addReviewWithClinic(String fullName, String speciality, String clinicName,
                                    RequestReviewDto requestReviewDto);

    public ResponseReviewDto getReviewById(Long reviewId);

}