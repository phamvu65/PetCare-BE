package vn.vuxnye.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.common.OrderStatus;
import vn.vuxnye.model.OrderEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class OrderResponse {
    private Long id;
    private String customerName;
    private String shippingAddress;
    private String paymentMethod;
    private OrderStatus status;
    private BigDecimal totalAmount; // Tổng sau khi trừ KM
    private List<OrderDetailResponse> items;
    private Instant createdAt;

    public static OrderResponse fromEntity(OrderEntity entity, BigDecimal totalAmount) {
        return OrderResponse.builder()
                .id(entity.getId())
                .customerName(entity.getCustomer().getFirstName() + " " + entity.getCustomer().getLastName())
                .shippingAddress(entity.getShippingAddress())
                .paymentMethod(entity.getPaymentMethod().name())
                .status(entity.getStatus())
                .totalAmount(totalAmount) // Cần truyền vào vì logic tính toán phức tạp
                .createdAt(entity.getCreatedAt())
                .items(entity.getOrderDetails().stream()
                        .map(OrderDetailResponse::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}