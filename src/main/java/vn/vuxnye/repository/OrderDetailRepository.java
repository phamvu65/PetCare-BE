package vn.vuxnye.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vuxnye.model.OrderDetailEntity;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
    // Tìm các chi tiết đơn hàng chứa sản phẩm ID này
    List<OrderDetailEntity> findByProductId(Long productId);
}