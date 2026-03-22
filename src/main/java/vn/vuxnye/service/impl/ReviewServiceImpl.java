package vn.vuxnye.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.vuxnye.dto.request.ReviewRequest;
import vn.vuxnye.dto.response.ReviewResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.ProductEntity;
import vn.vuxnye.model.ProductReviewEntity;
import vn.vuxnye.model.UserEntity;
import vn.vuxnye.repository.OrderRepository;
import vn.vuxnye.repository.ProductRepository;
import vn.vuxnye.repository.ProductReviewRepository;
import vn.vuxnye.repository.UserRepository;
import vn.vuxnye.service.ReviewService;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j(topic = "REVIEW-SERVICE")
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private static final List<String> BAD_WORDS = Arrays.asList("lừa đảo", "xấu", "tệ hại", "ngu");

    @Override
    public ReviewResponse createReview(UserDetails userDetails, ReviewRequest request) {
        log.info("User {} posting a review/reply", userDetails.getUsername());

        UserEntity user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Kiểm tra từ cấm
        if (containsBadWords(request.getComment())) {
            throw new RuntimeException("Nội dung vi phạm quy tắc cộng đồng.");
        }

        ProductReviewEntity review = new ProductReviewEntity();
        review.setUser(user);
        review.setProduct(product);
        review.setComment(request.getComment());

        // --- PHÂN NHÁNH LOGIC ---
        if (request.getParentId() != null) {

            ProductReviewEntity parentReview = reviewRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Review gốc không tồn tại"));

            review.setParent(parentReview);
            review.setRating(null);

        } else {

            // 1. Validate Rating
            if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
                throw new RuntimeException("Vui lòng chọn số sao (1-5) cho đánh giá gốc.");
            }

            // 2. Kiểm tra đã mua hàng chưa (Chỉ áp dụng cho đánh giá gốc)
            boolean hasPurchased = orderRepository.existsByCustomerAndProductAndStatusCompleted(user.getId(), product.getId());
            if (!hasPurchased && !isAdmin(userDetails)) { // Admin có thể đánh giá test mà không cần mua
                throw new RuntimeException("Bạn phải mua sản phẩm này trước khi đánh giá.");
            }

            // 3. Kiểm tra đã đánh giá chưa (Mỗi người 1 đánh giá gốc/sp)
            if (reviewRepository.existsByUserIdAndProductIdOriginal(user.getId(), product.getId())) {
                throw new RuntimeException("Bạn đã gửi đánh giá cho sản phẩm này rồi.");
            }

            review.setParent(null);
            review.setRating(request.getRating());
        }

        ProductReviewEntity savedReview = reviewRepository.save(review);

        // Chỉ cập nhật điểm trung bình nếu là đánh giá gốc
        if (savedReview.getParent() == null) {
            updateProductRating(product);
        }

        return ReviewResponse.fromEntity(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByProduct(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size);
        Page<ProductReviewEntity> reviewPage = reviewRepository.findByProductId(productId, pageable);
        return reviewPage.map(ReviewResponse::fromEntity);
    }

    private void updateProductRating(ProductEntity product) {
    }

    private boolean containsBadWords(String comment) {
        String lowerComment = comment.toLowerCase();
        return BAD_WORDS.stream().anyMatch(lowerComment::contains);
    }

    private boolean isAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }
}