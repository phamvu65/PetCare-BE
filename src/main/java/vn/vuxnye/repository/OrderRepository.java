package vn.vuxnye.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vuxnye.common.OrderStatus;
import vn.vuxnye.model.OrderEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByCustomerUsernameOrderByCreatedAtDesc(String username);

    // 🟢 ĐÃ CẬP NHẬT: Thêm logic lọc theo userId
    @Query("SELECT o FROM OrderEntity o WHERE " +
            "(:userId IS NULL OR o.customer.id = :userId) AND " + // 👈 Thêm dòng này để lọc theo User
            "(:status IS NULL OR o.status = :status) AND " +
            "(:startDate IS NULL OR o.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR o.createdAt <= :endDate)")
    Page<OrderEntity> findOrdersByFilter(
            @Param("userId") Long userId,          // 👈 Thêm tham số userId
            @Param("status") OrderStatus status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable);

    // --- Các hàm thống kê giữ nguyên ---

    @Query("SELECT SUM(o.totalAmount) FROM OrderEntity o " +
            "WHERE o.status = :status " +
            "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR o.createdAt <= :endDate)")
    BigDecimal countRevenue(@Param("status") OrderStatus status,
                            @Param("startDate") Instant startDate,
                            @Param("endDate") Instant endDate);

    @Query("SELECT COUNT(o) FROM OrderEntity o " +
            "WHERE o.status = :status " +
            "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR o.createdAt <= :endDate)")
    Long countByStatusAndDate(@Param("status") OrderStatus status,
                              @Param("startDate") Instant startDate,
                              @Param("endDate") Instant endDate);

    @Query("SELECT COUNT(o) FROM OrderEntity o " +
            "WHERE (:startDate IS NULL OR o.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR o.createdAt <= :endDate)")
    Long countTotalByDate(@Param("startDate") Instant startDate,
                          @Param("endDate") Instant endDate);

    // --- Các hàm khác ---

    @Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.product p " +
            "WHERE o.id = :id")
    Optional<OrderEntity> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT COUNT(o) > 0 FROM OrderEntity o " +
            "JOIN o.orderDetails od " +
            "WHERE o.customer.id = :userId " +
            "AND od.product.id = :productId " +
            "AND o.status = 'COMPLETED'")
    boolean existsByCustomerAndProductAndStatusCompleted(@Param("userId") Long userId,
                                                         @Param("productId") Long productId);

    @Query("SELECT o FROM OrderEntity o " +
            "WHERE o.status = :status " +
            "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR o.createdAt <= :endDate) " +
            "ORDER BY o.createdAt ASC")
    List<OrderEntity> findCompletedOrdersBetween(@Param("status") OrderStatus status,
                                                 @Param("startDate") Instant startDate,
                                                 @Param("endDate") Instant endDate);
}