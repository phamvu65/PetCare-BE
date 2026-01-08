package vn.vuxnye.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vuxnye.common.OrderStatus;
import vn.vuxnye.dto.response.ProductStatsResponse;
import vn.vuxnye.model.ProductEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    /**
     * 1. TÌM KIẾM & LỌC SẢN PHẨM (CHO TRANG DANH SÁCH SẢN PHẨM)
     * - Sử dụng Native Query để tối ưu hiệu suất tìm kiếm nhiều điều kiện.
     */
    @Query(value = "SELECT p.* FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "       p.name LIKE :keyword OR " +
            "       p.description LIKE :keyword OR " +
            "       c.name LIKE :keyword) " +
            "AND (:categoryIds IS NULL OR c.id IN (:categoryIds)) " +
            "AND p.is_deleted = :isDeleted",
            countQuery = "SELECT count(*) FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "WHERE (:keyword IS NULL OR :keyword = '' OR " +
                    "       p.name LIKE :keyword OR " +
                    "       p.description LIKE :keyword OR " +
                    "       c.name LIKE :keyword) " +
                    "AND (:categoryIds IS NULL OR c.id IN (:categoryIds)) " +
                    "AND p.is_deleted = :isDeleted",
            nativeQuery = true)
    Page<ProductEntity> searchProducts(@Param("keyword") String keyword,
                                       @Param("categoryIds") List<Long> categoryIds,
                                       @Param("isDeleted") boolean isDeleted,
                                       Pageable pageable);

    /**
     * 2. LẤY CHI TIẾT SẢN PHẨM (KÈM CATEGORY VÀ IMAGES)
     */
    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithDetails(@Param("id") Long id);

    /**
     * 3. CÁC HÀM TÌM KIẾM CƠ BẢN KHÁC
     */
    List<ProductEntity> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM ProductEntity p LEFT JOIN p.category c WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ProductEntity> searchEverything(@Param("keyword") String keyword);

    /**
     * 🟢 4. HÀM MỚI: THỐNG KÊ DOANH SỐ CHO TẤT CẢ SẢN PHẨM (CHO TRANG PRODUCT SALES)
     * - Logic: Lấy từ bảng Product -> LEFT JOIN sang OrderDetail.
     * - Mục đích: Giữ lại cả những sản phẩm chưa bán được (để hiển thị số lượng 0).
     */
    @Query("SELECT new vn.vuxnye.dto.response.ProductStatsResponse(" +
            "p.id, " +
            "p.name, " +
            "c.name, " +
            "p.price, " +
            // Tính tổng số lượng: Nếu có đơn hàng khớp status & ngày thì cộng, ngược lại cộng 0
            "COALESCE(SUM(CASE WHEN o.status = :status " +
            "   AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
            "   AND (:endDate IS NULL OR o.createdAt <= :endDate) " +
            "   THEN od.qty ELSE 0 END), 0), " +
            // Tính tổng doanh thu
            "COALESCE(SUM(CASE WHEN o.status = :status " +
            "   AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
            "   AND (:endDate IS NULL OR o.createdAt <= :endDate) " +
            "   THEN (od.qty * od.unitPrice) ELSE 0 END), 0) ," +
            "p.stock " +
            ") " +
            "FROM ProductEntity p " +
            "LEFT JOIN p.category c " +
            "LEFT JOIN OrderDetailEntity od ON p.id = od.product.id " +
            "LEFT JOIN od.order o " +
            "WHERE p.isDeleted = false " + // Chỉ lấy sản phẩm chưa bị xóa
            "GROUP BY p.id, p.name, c.name, p.price, p.stock")
    Page<ProductStatsResponse> getAllProductSalesStats(
            @Param("status") OrderStatus status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable);

    /**
     * 5. TÌM SẢN PHẨM SẮP HẾT HÀNG (CHO DASHBOARD)
     */
    @Query("SELECT p FROM ProductEntity p WHERE p.stock <= :limit AND p.isDeleted = false ORDER BY p.stock ASC")
    List<ProductEntity> findLowStockProducts(@Param("limit") int limit);
}