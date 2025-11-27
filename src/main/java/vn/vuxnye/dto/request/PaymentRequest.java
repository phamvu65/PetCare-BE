package vn.vuxnye.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.common.PaymentMethod;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequest {

    @NotNull(message = "Order ID must not be null")
    private Long orderId;

    // Số tiền khách muốn trả (thường bằng totalAmount của đơn hàng)
    @NotNull
    @Min(0)
    private BigDecimal amount;

    // Phương thức (COD, VNPAY, MOMO...)
    @NotNull
    private PaymentMethod method;

    private String note;
}