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
import vn.vuxnye.common.ResponseAPI;
import vn.vuxnye.dto.request.CartItemRequest;
import vn.vuxnye.dto.response.CartResponse;
import vn.vuxnye.service.CartService;


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
    public ResponseAPI getMyCart(@AuthenticationPrincipal UserDetails userDetails) {
        CartResponse cart = cartService.getMyCart(userDetails.getUsername());

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Get cart success")
                .data(cart)
                .build();
    }

    /**
     * Thêm sản phẩm vào giỏ
     */
    @PostMapping("/add")
    @Operation(summary = "Add to cart", description = "Add a product to cart")
    public ResponseAPI addToCart(
            @Valid @RequestBody CartItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        CartResponse cart = cartService.addToCart(userDetails.getUsername(), request);

        return ResponseAPI.builder()
                .status(HttpStatus.OK) // Có thể để OK hoặc CREATED tùy logic nghiệp vụ của bạn
                .message("Item added to cart")
                .data(cart)
                .build();
    }

    /**
     * Cập nhật số lượng
     */
    @PutMapping("/upd/{itemId}")
    @Operation(summary = "Update item quantity", description = "Update quantity of a cart item")
    public ResponseAPI updateQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity,
            @AuthenticationPrincipal UserDetails userDetails) {

        CartResponse cart = cartService.updateItemQuantity(userDetails.getUsername(), itemId, quantity);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Quantity updated")
                .data(cart)
                .build();
    }

    /**
     * Xóa sản phẩm khỏi giỏ
     */
    @DeleteMapping("/del/{itemId}")
    @Operation(summary = "Remove item", description = "Remove a product from cart")
    public ResponseAPI removeItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {

        CartResponse cart = cartService.removeItem(userDetails.getUsername(), itemId);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Item removed from cart")
                .data(cart)
                .build();
    }

    /**
     * Xóa sạch giỏ hàng
     */
    @DeleteMapping("/clear")
    @Operation(summary = "Clear cart", description = "Remove all items from cart")
    public ResponseAPI clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Cart cleared")
                .data(null)
                .build();
    }


}