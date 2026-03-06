package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.common.ResponseAPI;
import vn.vuxnye.dto.request.ReviewRequest;
import vn.vuxnye.dto.response.ReviewResponse;
import vn.vuxnye.service.ReviewService;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
@Tag(name = "Review Controller")
@RequiredArgsConstructor
@Slf4j(topic = "REVIEW-CONTROLLER")
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Tạo đánh giá mới
     */
    @PostMapping("/comment")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'STAFF')")
    @Operation(summary = "Submit a review", description = "User creates a review for a purchased product")
    public ResponseAPI createReview(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        ReviewResponse response = reviewService.createReview(userDetails, request);

        return ResponseAPI.builder()
                .status(HttpStatus.CREATED)
                .message("Review submitted successfully")
                .data(response)
                .build();
    }

    /**
     * Xem danh sách đánh giá của một sản phẩm (Public)
     * API: GET /api/v1/reviews/product/{productId}
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get product reviews", description = "Get list of reviews for a specific product")
    public ResponseAPI getProductReviews(
            @PathVariable @Min(1) Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ReviewResponse> reviews = reviewService.getReviewsByProduct(productId, page, size);

        // Đóng gói cả danh sách nội dung và phân trang vào chung một Map
        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("items", reviews.getContent());
        responseData.put("pagination", Map.of(
                "page", reviews.getNumber() + 1,
                "size", reviews.getSize(),
                "totalElements", reviews.getTotalElements(),
                "totalPages", reviews.getTotalPages()
        ));

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Get reviews success")
                .data(responseData)
                .build();
    }
}