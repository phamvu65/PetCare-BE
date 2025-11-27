package vn.vuxnye.service;

import org.springframework.security.core.userdetails.UserDetails;
import vn.vuxnye.common.OrderStatus;
import vn.vuxnye.dto.request.OrderRequest;
import vn.vuxnye.dto.response.OrderPageResponse;
import vn.vuxnye.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    // Luồng 5.3: Tạo đơn hàng (Checkout)
    OrderResponse createOrder(UserDetails userDetails, OrderRequest request);

    // Xem lịch sử đơn hàng
    List<OrderResponse> getMyOrders(UserDetails userDetails);

    // Xem chi tiết đơn hàng
    OrderResponse getOrderById(Long id, UserDetails userDetails);

    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus, UserDetails userDetails);

    void cancelOrderUser(Long id,UserDetails userDetails);

    OrderPageResponse getAllOrders(OrderStatus status, int page, int size);

}