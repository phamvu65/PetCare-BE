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

    OrderResponse createOrder(UserDetails userDetails, OrderRequest request);

    List<OrderResponse> getMyOrders(UserDetails userDetails);


    OrderResponse getOrderById(Long id);

    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus, UserDetails userDetails);

    void cancelOrderUser(Long id, UserDetails userDetails);


    OrderPageResponse getAllOrders(Long userId, OrderStatus status, LocalDate fromDate, LocalDate toDate, int page, int size);

    OrderStatisticResponse getStatistics(LocalDate fromDate, LocalDate toDate);

    Page<ProductStatsResponse> getProductSalesStats(LocalDate fromDate, LocalDate toDate, int page, int size, String sortField, String sortDir);
}