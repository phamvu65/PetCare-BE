package vn.vuxnye.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vuxnye.model.PaymentEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    // Tìm lịch sử thanh toán của 1 đơn hàng
    List<PaymentEntity> findByOrderId(Long orderId);

    // Tìm giao dịch thành công
    Optional<PaymentEntity> findFirstByOrderIdAndPaidAtIsNotNullOrderByPaidAtDesc(Long orderId);
}