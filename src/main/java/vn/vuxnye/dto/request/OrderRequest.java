package vn.vuxnye.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.common.PaymentMethod;

import java.util.List;

@Getter
@Setter
public class OrderRequest {

    @NotNull(message = "Address ID cannot be null")
    private Long addressId;

    @NotNull(message = "Payment method cannot be null")
    private PaymentMethod paymentMethod; // COD, CARD, E_WALLET...

    private String couponCode; // (Optional) Mã giảm giá

    private String note;

    // (Optional) Dùng cho chức năng "Mua ngay" (Buy Now) 1 sản phẩm
    // Nếu list này null hoặc rỗng -> Hệ thống sẽ lấy từ GIỎ HÀNG
    private List<OrderItemRequest> items;
}