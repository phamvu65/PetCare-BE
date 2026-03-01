package vn.vuxnye.service;

import vn.vuxnye.dto.request.CartItemRequest;
import vn.vuxnye.dto.response.CartResponse;

public interface CartService {

    CartResponse getMyCart(String username);

    CartResponse addToCart(String username, CartItemRequest request);

    CartResponse updateItemQuantity(String username, Long cartItemId, Integer quantity);

    CartResponse removeItem(String username, Long cartItemId);

    void clearCart(String username);
}