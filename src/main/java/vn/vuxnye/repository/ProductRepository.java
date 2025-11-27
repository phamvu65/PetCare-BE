package vn.vuxnye.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vuxnye.model.ProductEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    /**
     * TÌM KIẾM & LỌC SẢN PHẨM (MAIN QUERY)
     * - keyword: tìm theo tên hoặc mô tả (có thể null)
     * - categoryIds: danh sách ID danh mục cần lọc (có thể null hoặc rỗng)
     * - Sử dụng LEFT JOIN FETCH p.category để lấy luôn thông tin danh mục, tránh lỗi N+1 query.
     */
    @Query("SELECT p FROM ProductEntity p " +
            "LEFT JOIN FETCH p.category c " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "       LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "       LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND ((:categoryIds) IS NULL OR c.id IN (:categoryIds))")
    Page<ProductEntity> searchProducts(@Param("keyword") String keyword,
                                       @Param("categoryIds") List<Long> categoryIds,
                                       Pageable pageable);

    /**
     * CHI TIẾT SẢN PHẨM
     * - Lấy 1 sản phẩm theo ID.
     * - FETCH cả Category và Images để hiển thị đầy đủ chi tiết mà không cần query thêm.
     */
    @Query("SELECT p FROM ProductEntity p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.images " +
            "WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithDetails(@Param("id") Long id);

    // Lưu ý: Tôi đã đổi List<Integer> thành List<Long> để khớp với kiểu dữ liệu chuẩn
    // của ID trong JpaRepository<ProductEntity, Long>.
    // Nếu Entity của bạn dùng Integer cho ID thì bạn hãy đổi lại thành Integer nhé.
}