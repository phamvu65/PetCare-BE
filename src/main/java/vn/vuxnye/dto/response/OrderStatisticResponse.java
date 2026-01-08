package vn.vuxnye.dto.response;

import lombok.*;
import vn.vuxnye.model.ProductEntity;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStatisticResponse {

    // --- TỔNG HỢP ---
    private BigDecimal totalRevenue; // Tổng doanh thu (Đơn hàng + Dịch vụ)

    // --- SỐ LIỆU ĐƠN HÀNG (SẢN PHẨM) ---
    private BigDecimal totalOrderRevenue; // Doanh thu bán hàng
    private Long newOrders;          // PENDING
    private Long shippingOrders;     // SHIPPING
    private Long cancelledOrders;    // CANCELLED
    private Long totalOrders;        // Tổng đơn
    private Long successOrders;      // COMPLETED (Thêm mới cho khớp frontend)

    // --- SỐ LIỆU DỊCH VỤ (SPA/GROOMING) - MỚI ---
    private BigDecimal totalServiceRevenue; // Doanh thu dịch vụ
    private Long totalAppointments;         // Tổng lịch hẹn
    private Long completedAppointments;     // DONE
    private Long cancelledAppointments;     // CANCELLED (Lịch hẹn hủy)

    // --- BIỂU ĐỒ ---
    private List<DailyRevenue> chartData;
    private List<ProductStatsResponse> topSellingProducts; // Top 5 bán chạy
    private List<LowStockDto> lowStockProducts;          // Sản phẩm sắp hết

    private List<ServiceStatsResponse> topServices;
    private List<EmployeeStatsResponse> topEmployees;
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyRevenue {
        private String date;            // Ngày (dd/MM)
        private BigDecimal orderRevenue;   // Doanh thu đơn hàng
        private BigDecimal serviceRevenue; // Doanh thu dịch vụ
        private BigDecimal total;       // Tổng cộng ngày đó
    }
    // 🟢 THÊM CLASS DTO CON Ở ĐÂY LUÔN CHO TIỆN
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LowStockDto {
        private Long id;
        private String name;
        private int stock;
        private BigDecimal price;
    }
}