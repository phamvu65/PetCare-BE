package vn.vuxnye.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vuxnye.model.ProductReviewEntity;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReviewEntity, Long> {

    // [SỬA] Chỉ lấy review gốc (parent is null)
    @Query("SELECT r FROM ProductReviewEntity r " +
            "JOIN FETCH r.user " +
            "LEFT JOIN FETCH r.replies rep " + // Fetch luôn reply để tránh N+1 (cẩn thận phân trang)
            "WHERE r.product.id = :productId AND r.parent IS NULL " +
            "ORDER BY r.createdAt DESC")
    Page<ProductReviewEntity> findByProductId(Long productId, Pageable pageable);

    // Kiểm tra xem User đã đánh giá gốc chưa (không tính reply)
    @Query("SELECT COUNT(r) > 0 FROM ProductReviewEntity r WHERE r.user.id = :userId AND r.product.id = :productId AND r.parent IS NULL")
    boolean existsByUserIdAndProductIdOriginal(Long userId, Long productId);
}