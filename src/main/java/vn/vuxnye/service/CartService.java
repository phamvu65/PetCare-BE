package vn.vuxnye.service;

import vn.vuxnye.dto.request.CartItemRequest;
import vn.vuxnye.dto.response.CartResponse;

public interface CartService {

    // Xem giỏ hàng
    CartResponse getMyCart(String username);

    // Thêm vào giỏ
    CartResponse addToCart(String username, CartItemRequest request);

    // Cập nhật số lượng
    CartResponse updateItemQuantity(String username, Long cartItemId, Integer quantity);

    // Xóa khỏi giỏ
    CartResponse removeItem(String username, Long cartItemId);

    // Xóa toàn bộ giỏ
    void clearCart(String username);
}