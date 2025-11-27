package vn.vuxnye.service;

import org.springframework.security.core.userdetails.UserDetails;
import vn.vuxnye.dto.request.PaymentRequest;
import vn.vuxnye.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    // Tạo yêu cầu thanh toán
    PaymentResponse createPayment(PaymentRequest request, UserDetails userDetails);

    // Xử lý Callback (Khi thanh toán thành công từ VNPay/ZaloPay)
    void handlePaymentSuccess(Long orderId, String transactionCode);

    // [MỚI] Xem lịch sử các lần thử thanh toán của đơn hàng
    List<PaymentResponse> getPaymentHistory(Long orderId, UserDetails userDetails);

    // [MỚI] Kiểm tra xem đơn hàng đã thanh toán thành công chưa
    PaymentResponse getSuccessPayment(Long orderId, UserDetails userDetails);
}