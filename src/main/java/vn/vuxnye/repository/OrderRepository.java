package vn.vuxnye.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vuxnye.model.OrderEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    // Lấy danh sách đơn hàng của user
    List<OrderEntity> findByCustomerUsernameOrderByCreatedAtDesc(String username);

    // Admin tìm kiếm đơn hàng
    @Query("SELECT o FROM OrderEntity o WHERE " +
            "(:status IS NULL OR o.status = :status)")
    Page<OrderEntity> findAllByStatus(@Param("status") Enum status, Pageable pageable);

    /**
     * Lấy chi tiết đơn hàng + FETCH luôn danh sách sản phẩm (orderDetails)
     * + FETCH luôn thông tin Product của từng detail (để lấy ảnh)
     */
    @Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.orderDetails od " +  // Tải chi tiết đơn
            "LEFT JOIN FETCH od.product p " +       // Tải thông tin sản phẩm gốc
            "LEFT JOIN FETCH p.images " +           // Tải ảnh sản phẩm (nếu cần hiện ảnh)
            "WHERE o.id = :id")
    Optional<OrderEntity> findByIdWithDetails(@Param("id") Long id);

    // Kiểm tra xem User đã từng mua (và nhận hàng) sản phẩm này chưa
    // Điều kiện: Đơn hàng chứa sản phẩm đó VÀ trạng thái là COMPLETED (hoặc DELIVERED)
    @Query("SELECT COUNT(o) > 0 FROM OrderEntity o " +
            "JOIN o.orderDetails od " +
            "WHERE o.customer.id = :userId " +
            "AND od.product.id = :productId " +
            "AND o.status = 'COMPLETED'") // Chỉ cho phép đánh giá khi đơn đã hoàn tất
    boolean existsByCustomerAndProductAndStatusCompleted(Long userId, Long productId);
}