package vn.vuxnye.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class CartResponse {
    private Long id;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice; // Tổng tiền giỏ hàng
    private int totalItems;        // Tổng số lượng sản phẩm
}