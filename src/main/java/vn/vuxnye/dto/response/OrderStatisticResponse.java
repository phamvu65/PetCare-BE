package vn.vuxnye.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStatisticResponse {

    // Tổng doanh thu (thường tính từ các đơn đã hoàn thành - COMPLETED)
    private BigDecimal revenue;

    // Số lượng đơn hàng mới chờ xử lý (PENDING)
    private Long newOrders;

    // Số lượng đơn hàng đang giao (SHIPPING)
    private Long shippingOrders;

    // Số lượng đơn hàng đã bị hủy (CANCELLED)
    private Long cancelledOrders;

    // Tổng số đơn hàng trong khoảng thời gian lọc
    private Long totalOrders;

    private List<DailyRevenue> chartData;
    @Getter
    @Setter
    @AllArgsConstructor
    public static class DailyRevenue {
        private String date;      // Ngày (dd/MM)
        private BigDecimal total; // Doanh thu ngày đó
    }
}