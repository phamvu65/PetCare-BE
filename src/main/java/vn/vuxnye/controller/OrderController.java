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
import vn.vuxnye.dto.request.OrderRequest;
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