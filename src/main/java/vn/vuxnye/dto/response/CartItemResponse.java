package vn.vuxnye.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.model.CartItemEntity;
import vn.vuxnye.model.ProductImageEntity;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CartItemResponse {
    private Long id; // Cart Item ID
    private Long productId;
    private String productName;
    private String productImage; // Ảnh đại diện sản phẩm
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subTotal; // Thành tiền (price * quantity)

    public static CartItemResponse fromEntity(CartItemEntity entity) {
        // Lấy ảnh đầu tiên làm đại diện
        String imgUrl = entity.getProduct().getImages().isEmpty() ? null
                : entity.getProduct().getImages().get(0).getImageUrl();

        BigDecimal price = entity.getProduct().getPrice();
        BigDecimal subTotal = price.multiply(BigDecimal.valueOf(entity.getQuantity()));

        return CartItemResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getName())
                .productImage(imgUrl)
                .price(price)
                .quantity(entity.getQuantity())
                .subTotal(subTotal)
                .build();
    }
}