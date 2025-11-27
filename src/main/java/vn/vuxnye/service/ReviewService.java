package vn.vuxnye.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import vn.vuxnye.dto.request.ReviewRequest;
import vn.vuxnye.dto.response.ReviewResponse;

public interface ReviewService {

    ReviewResponse createReview(UserDetails userDetails, ReviewRequest request);

    Page<ReviewResponse> getReviewsByProduct(Long productId, int page, int size);
}