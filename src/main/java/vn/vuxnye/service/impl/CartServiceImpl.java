package vn.vuxnye.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.vuxnye.dto.request.CartItemRequest;
import vn.vuxnye.dto.response.CartItemResponse;
import vn.vuxnye.dto.response.CartResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.CartEntity;
import vn.vuxnye.model.CartItemEntity;
import vn.vuxnye.model.ProductEntity;
import vn.vuxnye.model.UserEntity;
import vn.vuxnye.repository.CartItemRepository;
import vn.vuxnye.repository.CartRepository;
import vn.vuxnye.repository.ProductRepository;
import vn.vuxnye.repository.UserRepository;
import vn.vuxnye.service.CartService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "CART-SERVICE")
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getMyCart(String username) {
        CartEntity cart = getOrCreateCart(username);
        return convertToResponse(cart);
    }

    @Override
    public CartResponse addToCart(String username, CartItemRequest request) {
        // 1. Lấy giỏ hàng
        CartEntity cart = getOrCreateCart(username);

        // 2. Kiểm tra sản phẩm tồn tại và còn hàng
        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Sản phẩm này hiện đã hết hàng hoặc không đủ số lượng (Stock: " + product.getStock() + ")");
        }

        // 3. Kiểm tra xem sản phẩm đã có trong giỏ chưa
        Optional<CartItemEntity> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Nếu có rồi -> Tăng số lượng
            CartItemEntity item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();

            // Check tồn kho lại với số lượng mới
            if (newQuantity > product.getStock()) {
                throw new RuntimeException("Tổng số lượng vượt quá tồn kho cho phép");
            }
            item.setQuantity(newQuantity);
        } else {
            // Nếu chưa có -> Tạo mới
            CartItemEntity newItem = new CartItemEntity();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
        }

        // 4. Lưu và trả về
        CartEntity savedCart = cartRepository.save(cart);
        return convertToResponse(savedCart);
    }

    @Override
    public CartResponse updateItemQuantity(String username, Long cartItemId, Integer quantity) {
        // 1. Lấy giỏ hàng
        CartEntity cart = getOrCreateCart(username);

        // 2. Tìm item trong giỏ
        CartItemEntity item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // 3. Xử lý xóa nếu quantity <= 0
        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item); // Xóa cứng khỏi DB
        } else {
            // 4. Check tồn kho
            if (quantity > item.getProduct().getStock()) {
                throw new RuntimeException("Bạn chỉ có thể mua tối đa " + item.getProduct().getStock() + " sản phẩm này.");
            }
            item.setQuantity(quantity);
        }

        CartEntity savedCart = cartRepository.save(cart);
        return convertToResponse(savedCart);
    }

    @Override
    public CartResponse removeItem(String username, Long cartItemId) {
        CartEntity cart = getOrCreateCart(username);

        // Tìm và xóa
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));

        if (!removed) {
            throw new ResourceNotFoundException("Item not found in your cart");
        }

        // Cần gọi save để Hibernate đồng bộ list
        CartEntity savedCart = cartRepository.save(cart);
        return convertToResponse(savedCart);
    }

    @Override
    public void clearCart(String username) {
        CartEntity cart = getOrCreateCart(username);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    // --- Helper Methods ---

    private CartEntity getOrCreateCart(String username) {
        return cartRepository.findByUsername(username)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    CartEntity newCart = new CartEntity();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    private CartResponse convertToResponse(CartEntity cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(CartItemResponse::fromEntity)
                .collect(Collectors.toList());

        // Tính tổng tiền
        BigDecimal totalPrice = itemResponses.stream()
                .map(CartItemResponse::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tổng số lượng item
        int totalItems = itemResponses.size();

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalPrice(totalPrice)
                .totalItems(totalItems)
                .build();
    }
}