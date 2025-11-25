package vn.vuxnye.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.model.OrderDetailEntity;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class OrderDetailResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage; // (Optional) Ảnh sản phẩm để hiển thị cho đẹp
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal; // Tổng tiền dòng này (qty * price)

    public static OrderDetailResponse fromEntity(OrderDetailEntity entity) {
        // Logic lấy ảnh đại diện (nếu sản phẩm có ảnh)
        String img = null;
        if (entity.getProduct() != null && !entity.getProduct().getImages().isEmpty()) {
            img = entity.getProduct().getImages().get(0).getImageUrl();
        }

        return OrderDetailResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .productName(entity.getName()) // Lấy tên từ snapshot trong order_details (an toàn hơn)
                .productImage(img)
                .quantity(entity.getQty())
                .unitPrice(entity.getUnitPrice())
                // Tự tính subTotal: qty * price
                .subTotal(entity.getUnitPrice().multiply(BigDecimal.valueOf(entity.getQty())))
                .build();
    }
}