package vn.vuxnye.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import vn.vuxnye.common.OrderStatus;
import vn.vuxnye.dto.request.OrderRequest;
import vn.vuxnye.dto.response.OrderPageResponse;
import vn.vuxnye.dto.response.OrderResponse;
import vn.vuxnye.dto.response.OrderStatisticResponse;
import vn.vuxnye.dto.response.ProductStatsResponse;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    // 1. Tạo đơn hàng (User)
    OrderResponse createOrder(UserDetails userDetails, OrderRequest request);

    // 2. Xem lịch sử đơn hàng (User)
    List<OrderResponse> getMyOrders(UserDetails userDetails);

    // 3. Xem chi tiết đơn hàng (User - có check quyền sở hữu)
    OrderResponse getOrderById(Long id, UserDetails userDetails);

    // 4. [MỚI] Xem chi tiết đơn hàng (Admin - không check owner)
    OrderResponse getOrderById(Long id);

    // 5. Cập nhật trạng thái (Admin/Staff)
    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus, UserDetails userDetails);

    // 6. Hủy đơn hàng (User)
    void cancelOrderUser(Long id, UserDetails userDetails);

    // 7. [CẬP NHẬT] Lấy danh sách đơn hàng (Admin - có lọc theo user và ngày)
    // 🟢 ĐÃ SỬA: Thêm tham số Long userId vào đầu
    OrderPageResponse getAllOrders(Long userId, OrderStatus status, LocalDate fromDate, LocalDate toDate, int page, int size);

    // 8. [MỚI] Lấy thống kê cho Dashboard
    OrderStatisticResponse getStatistics(LocalDate fromDate, LocalDate toDate);

    Page<ProductStatsResponse> getProductSalesStats(LocalDate fromDate, LocalDate toDate, int page, int size, String sortField, String sortDir);
}