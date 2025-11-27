package vn.vuxnye.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.model.ProductReviewEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String userName;
    private String userAvatar;
    private String userRole; // Để FE hiển thị tag "QTV" hoặc "Khách hàng"
    private Byte rating;
    private String comment;
    private Instant createdAt;
    private List<ReviewResponse> replies;

    public static ReviewResponse fromEntity(ProductReviewEntity entity) {
        ReviewResponseBuilder builder = ReviewResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getName())
                .userName(entity.getUser().getLastName() + " " + entity.getUser().getFirstName())
                .userAvatar(entity.getUser().getAvatarUrl())
                // Giả sử lấy role đầu tiên để hiển thị
                .userRole(entity.getUser().getRoles().stream().findFirst().get().getName())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt());

        // Đệ quy để lấy danh sách trả lời
        if (entity.getReplies() != null && !entity.getReplies().isEmpty()) {
            builder.replies(entity.getReplies().stream()
                    .map(ReviewResponse::fromEntity)
                    .collect(Collectors.toList()));
        } else {
            builder.replies(new ArrayList<>());
        }

        return builder.build();
    }
}