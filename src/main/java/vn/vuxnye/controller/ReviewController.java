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
    public Map<String, Object> createReview(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        ReviewResponse response = reviewService.createReview(userDetails, request);
        return createResponse(HttpStatus.CREATED, "Review submitted successfully", response);
    }

    /**
     * Xem danh sách đánh giá của một sản phẩm (Public)
     * API: GET /api/v1/reviews/product/{productId}
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get product reviews", description = "Get list of reviews for a specific product")
    public Map<String, Object> getProductReviews(
            @PathVariable @Min(1) Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ReviewResponse> reviews = reviewService.getReviewsByProduct(productId, page, size);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Get reviews success");
        result.put("data", reviews.getContent());
        result.put("pagination", Map.of(
                "page", reviews.getNumber() + 1,
                "size", reviews.getSize(),
                "totalElements", reviews.getTotalElements(),
                "totalPages", reviews.getTotalPages()
        ));
        return result;
    }

    private Map<String, Object> createResponse(HttpStatus status, String message, Object data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", status.value());
        result.put("message", message);
        result.put("data", data);
        return result;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(RuntimeException ex) {
        return createResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }
}