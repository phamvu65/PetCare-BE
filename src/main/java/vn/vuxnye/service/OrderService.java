package vn.vuxnye.service;

import org.springframework.security.core.userdetails.UserDetails;
import vn.vuxnye.common.OrderStatus;
import vn.vuxnye.dto.request.OrderRequest;
import vn.vuxnye.dto.response.OrderPageResponse;
import vn.vuxnye.dto.response.OrderResponse;
import vn.vuxnye.dto.response.OrderStatisticResponse; // Nhớ import DTO mới này

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

    // 7. [CẬP NHẬT] Lấy danh sách đơn hàng (Admin - có lọc theo ngày)
    OrderPageResponse getAllOrders(OrderStatus status, LocalDate fromDate, LocalDate toDate, int page, int size);

    // 8. [MỚI] Lấy thống kê cho Dashboard
    OrderStatisticResponse getStatistics(LocalDate fromDate, LocalDate toDate);
}