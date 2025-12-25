package vn.vuxnye.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vuxnye.common.OrderStatus;
import vn.vuxnye.dto.response.ProductStatsResponse;
import vn.vuxnye.model.OrderDetailEntity;

import org.springframework.data.domain.Pageable; // ✅ THÊM DÒNG NÀY
import java.time.Instant;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
    // Tìm các chi tiết đơn hàng chứa sản phẩm ID này
    List<OrderDetailEntity> findByProductId(Long productId);
    @Query("SELECT new vn.vuxnye.dto.response.ProductStatsResponse(" +
            "od.product.id, od.product.name, od.product.price, SUM(od.qty)) " +
            "FROM OrderDetailEntity od " +
            "JOIN od.order o " +
            "WHERE o.status = :status " +
            "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR o.createdAt <= :endDate) " +
            "GROUP BY od.product.id, od.product.name, od.product.price " +
            "ORDER BY SUM(od.qty) DESC")
    List<ProductStatsResponse> findTopSellingProducts(
            @Param("status") OrderStatus status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable); // Dùng Pageable để giới hạn lấy Top 5
}