package vn.vuxnye.service;

import org.springframework.security.core.userdetails.UserDetails;
import vn.vuxnye.dto.request.PaymentRequest;
import vn.vuxnye.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request, UserDetails userDetails);

    void handlePaymentSuccess(Long orderId, String transactionCode);

    List<PaymentResponse> getPaymentHistory(Long orderId, UserDetails userDetails);

    PaymentResponse getSuccessPayment(Long orderId, UserDetails userDetails);
}