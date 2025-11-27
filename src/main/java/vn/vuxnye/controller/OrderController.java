package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.common.OrderStatus;
import vn.vuxnye.dto.request.OrderRequest;
import vn.vuxnye.dto.response.OrderPageResponse;
import vn.vuxnye.dto.response.OrderResponse;
import vn.vuxnye.service.OrderService;

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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Admin: Get all orders", description = "Retrieve all orders with pagination and status filter")
    public Map<String, Object> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        OrderPageResponse response = orderService.getAllOrders(status, page, size);
        return createResponse(HttpStatus.OK, "Get all orders success", response);
    }


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

    /**
     * Admin/Staff: Cập nhật trạng thái đơn hàng
     * (Duyệt đơn, Giao hàng, Hủy đơn, Hoàn thành)
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Chỉ nhân viên mới được đổi trạng thái
    @Operation(summary = "Admin/Staff: Update order status")
    public Map<String, Object> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status, // Nhận status từ query param (hoặc body)
            @AuthenticationPrincipal UserDetails userDetails) {

        OrderResponse response = orderService.updateOrderStatus(id, status, userDetails);
        return createResponse(HttpStatus.OK, "Update status success", response);
    }

    /**
     * User: Hủy đơn hàng (Chỉ khi còn PENDING)
     */
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