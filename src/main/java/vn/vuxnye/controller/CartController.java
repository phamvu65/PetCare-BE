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
import vn.vuxnye.dto.request.CartItemRequest;
import vn.vuxnye.dto.response.CartResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.service.CartService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@Tag(name = "Cart Controller")
@RequiredArgsConstructor
@Slf4j(topic = "CART-CONTROLLER")
@PreAuthorize("hasRole('CUSTOMER')") // Chỉ KHÁCH HÀNG mới có giỏ hàng
public class CartController {

    private final CartService cartService;

    /**
     * Xem giỏ hàng
     */
    @GetMapping
    @Operation(summary = "Get my cart", description = "View current user's shopping cart")
    public Map<String, Object> getMyCart(@AuthenticationPrincipal UserDetails userDetails) {
        CartResponse cart = cartService.getMyCart(userDetails.getUsername());
        return createResponse(HttpStatus.OK, "Get cart success", cart);
    }

    /**
     * Thêm sản phẩm vào giỏ
     */
    @PostMapping("/add")
    @Operation(summary = "Add to cart", description = "Add a product to cart")
    public Map<String, Object> addToCart(
            @Valid @RequestBody CartItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        CartResponse cart = cartService.addToCart(userDetails.getUsername(), request);
        return createResponse(HttpStatus.OK, "Item added to cart", cart);
    }

    /**
     * Cập nhật số lượng
     */
    @PutMapping("/upd/{itemId}")
    @Operation(summary = "Update item quantity", description = "Update quantity of a cart item")
    public Map<String, Object> updateQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity,
            @AuthenticationPrincipal UserDetails userDetails) {

        CartResponse cart = cartService.updateItemQuantity(userDetails.getUsername(), itemId, quantity);
        return createResponse(HttpStatus.OK, "Quantity updated", cart);
    }

    /**
     * Xóa sản phẩm khỏi giỏ
     */
    @DeleteMapping("/del/{itemId}")
    @Operation(summary = "Remove item", description = "Remove a product from cart")
    public Map<String, Object> removeItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {

        CartResponse cart = cartService.removeItem(userDetails.getUsername(), itemId);
        return createResponse(HttpStatus.OK, "Item removed from cart", cart);
    }

    /**
     * Xóa sạch giỏ hàng
     */
    @DeleteMapping("/clear")
    @Operation(summary = "Clear cart", description = "Remove all items from cart")
    public Map<String, Object> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return createResponse(HttpStatus.OK, "Cart cleared", null);
    }

    private Map<String, Object> createResponse(HttpStatus status, String message, Object data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", status.value());
        result.put("message", message);
        result.put("data", data);
        return result;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(ResourceNotFoundException ex) {
        return createResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(RuntimeException ex) {
        return createResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }
}