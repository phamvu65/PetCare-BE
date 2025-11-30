package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.common.OrderStatus;
import vn.vuxnye.dto.request.OrderRequest;
import vn.vuxnye.dto.response.OrderPageResponse;
import vn.vuxnye.dto.response.OrderResponse;
import vn.vuxnye.dto.response.OrderStatisticResponse; // Cần tạo class này (xem ghi chú bên dưới)
import vn.vuxnye.service.OrderService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order Controller")
@RequiredArgsConstructor
@Slf4j(topic = "ORDER-CONTROLLER")
@PreAuthorize("isAuthenticated()")
public class OrderController {

    private final OrderService orderService;

    /**
     * 1. ADMIN DASHBOARD: Lấy số liệu thống kê (Doanh thu, số lượng đơn...)
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Admin: Get dashboard statistics", description = "Get total revenue and order counts by status")
    public Map<String, Object> getOrderStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        OrderStatisticResponse stats = orderService.getStatistics(fromDate, toDate);
        return createResponse(HttpStatus.OK, "Get statistics success", stats);
    }

    /**
     * 2. ADMIN/STAFF: Lấy danh sách đơn hàng (Có lọc theo status và ngày)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Admin: Get all orders", description = "Retrieve all orders with pagination, status filter, date range AND userId")
    public Map<String, Object> getAllOrders(
            @RequestParam(required = false) Long userId, // <--- THÊM DÒNG NÀY
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        // Bạn cần vào OrderService và cập nhật hàm getAllOrders để nhận thêm userId và truyền xuống Repository
        OrderPageResponse response = orderService.getAllOrders(userId, status, fromDate, toDate, page, size);
        return createResponse(HttpStatus.OK, "Get all orders success", response);
    }

    /**
     * 3. ADMIN/STAFF/USER: Xem chi tiết một đơn hàng cụ thể
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or @securityService.isOrderOwner(#id, principal)")
    // @securityService là custom bean để check quyền sở hữu nếu bạn muốn kỹ, hoặc dùng logic trong Service
    @Operation(summary = "Get Order Details", description = "Get detail of a specific order by ID")
    public Map<String, Object> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return createResponse(HttpStatus.OK, "Get order detail success", response);
    }

    // --- CÁC API CŨ GIỮ NGUYÊN ---

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Place an Order", description = "Checkout from cart OR Buy Now")
    public Map<String, Object> createOrder(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("User {} placing order", userDetails.getUsername());
        OrderResponse response = orderService.createOrder(userDetails, request);

        return createResponse(HttpStatus.CREATED, "Order placed successfully", response);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get my orders history")
    public Map<String, Object> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        return createResponse(HttpStatus.OK, "Get orders success", orderService.getMyOrders(userDetails));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Admin/Staff: Update order status")
    public Map<String, Object> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {

        OrderResponse response = orderService.updateOrderStatus(id, status, userDetails);
        return createResponse(HttpStatus.OK, "Update status success", response);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "User: Cancel order")
    public Map<String, Object> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        orderService.cancelOrderUser(id, userDetails);
        return createResponse(HttpStatus.OK, "Cancel order success", null);
    }

    // --- Helper ---
    private Map<String, Object> createResponse(HttpStatus status, String message, Object data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", status.value());
        result.put("message", message);
        result.put("data", data);
        return result;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(RuntimeException ex) {
        return createResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }
}