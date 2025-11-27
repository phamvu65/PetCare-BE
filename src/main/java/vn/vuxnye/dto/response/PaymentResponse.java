package vn.vuxnye.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentResponse {
    private String status;       // SUCCESS, PENDING, FAILED
    private String message;
    private String paymentUrl;   // URL để redirect sang trang thanh toán (nếu là Online)
    private Long orderId;
    private Long paymentId;
}