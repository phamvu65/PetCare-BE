package vn.vuxnye.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vuxnye.model.OrderCouponEntity;

@Repository
public interface OrderCouponRepository extends JpaRepository<OrderCouponEntity, Long> {
    // Hiện tại chưa cần query gì phức tạp, JpaRepository có sẵn hàm save() là đủ.
}