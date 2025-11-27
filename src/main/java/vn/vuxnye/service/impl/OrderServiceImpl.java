package vn.vuxnye.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.vuxnye.common.CouponType;
import vn.vuxnye.common.ItemType;
import vn.vuxnye.common.OrderChannel;
import vn.vuxnye.common.OrderStatus;
import vn.vuxnye.dto.request.OrderItemRequest;
import vn.vuxnye.dto.request.OrderRequest;
import vn.vuxnye.dto.response.OrderPageResponse;
import vn.vuxnye.dto.response.OrderResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.*;
import vn.vuxnye.repository.*;
import vn.vuxnye.service.CartService;
import vn.vuxnye.service.OrderService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "ORDER-IMPL")
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CouponRepository couponRepository;
    private final CartService cartService; // Để clear cart sau khi đặt hàng thành công

    @Override
    @Transactional(readOnly = true)
    public OrderPageResponse getAllOrders(OrderStatus status, int page, int size) {
        log.info("Admin fetching all orders. Status filter: {}", status);

        // Sắp xếp mặc định: Mới nhất lên đầu
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size, Sort.by("createdAt").descending());

        Page<OrderEntity> orderPage;

        if (status != null) {
            // Nếu có lọc theo trạng thái (Ví dụ: chỉ xem đơn PENDING)
            // (Lưu ý: Bạn cần đảm bảo Repository có hàm findAllByStatus hoặc dùng Specification)
            orderPage = orderRepository.findAllByStatus(status, pageable);
        } else {
            // Lấy tất cả
            orderPage = orderRepository.findAll(pageable);
        }

        // Chuyển đổi Entity -> DTO
        List<OrderResponse> orderResponses = orderPage.stream()
                .map(order -> OrderResponse.fromEntity(order, order.getTotalAmount()))
                .collect(Collectors.toList());

        OrderPageResponse response = new OrderPageResponse();
        response.setOrders(orderResponses);
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(orderPage.getTotalElements());
        response.setTotalPages(orderPage.getTotalPages());

        return response;
    }


    @Override
    public OrderResponse createOrder(UserDetails userDetails, OrderRequest request) {
        log.info("Creating order for user: {}", userDetails.getUsername());

        // 1. Lấy User từ token
        UserEntity user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Lấy Địa chỉ giao hàng (Phải thuộc về User)
        AddressEntity address = addressRepository.findByIdAndUserId(request.getAddressId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found or does not belong to user"));

        // 3. Xác định danh sách sản phẩm (Mua ngay hoặc Mua từ giỏ)
        List<OrderDetailEntity> orderDetails = new ArrayList<>();
        BigDecimal provisionalTotal = BigDecimal.ZERO;

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            // (b) Mua Ngay (Buy Now) - Từ list gửi lên
            for (OrderItemRequest itemReq : request.getItems()) {
                OrderDetailEntity detail = createOrderDetail(itemReq.getProductId(), itemReq.getQuantity());
                orderDetails.add(detail);

                // Tự tính tiền (qty * unitPrice) thay vì gọi getLineTotal() (vì chưa save DB nên @Formula chưa chạy)
                BigDecimal linePrice = detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQty()));
                provisionalTotal = provisionalTotal.add(linePrice);
            }
        } else {
            // (a) Mua từ Giỏ hàng (Checkout Cart)
            CartEntity cart = cartRepository.findByUsername(user.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Cart is empty"));

            if (cart.getItems().isEmpty()) {
                throw new RuntimeException("Giỏ hàng trống");
            }

            for (CartItemEntity cartItem : cart.getItems()) {
                OrderDetailEntity detail = createOrderDetail(cartItem.getProduct().getId(), cartItem.getQuantity());
                orderDetails.add(detail);

                BigDecimal linePrice = detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQty()));
                provisionalTotal = provisionalTotal.add(linePrice);
            }
        }

        // 4. Khởi tạo Order Entity
        OrderEntity order = new OrderEntity();
        order.setCustomer(user);
        order.setChannel(OrderChannel.WEB);
        order.setStatus(OrderStatus.PENDING); // Mặc định là Chờ xử lý
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNote(request.getNote());
        // Lưu snapshot địa chỉ (dạng text) để tránh user sửa địa chỉ gốc sau này làm sai lệch đơn hàng
        order.setShippingAddress(address.getAddressDetail() + ", " + address.getWard() + ", " + address.getCity());

        // 5. Xử lý Mã Giảm Giá (Coupon)
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            CouponEntity coupon = validateCoupon(request.getCouponCode(), provisionalTotal);

            // Tính tiền giảm
            if (coupon.getType() == CouponType.percent) { // (Lưu ý: Đảm bảo Enum trong code bạn là chữ thường hay hoa)
                // Chia cho 100, làm tròn
                BigDecimal percent = coupon.getValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                discountAmount = provisionalTotal.multiply(percent);
            } else {
                discountAmount = coupon.getValue();
            }

            // Tạo liên kết OrderCoupon (để lưu lịch sử dùng mã)
            OrderCouponEntity orderCoupon = new OrderCouponEntity();
            orderCoupon.setOrder(order);
            orderCoupon.setCoupon(coupon);
            orderCoupon.setDiscountValue(discountAmount);

            // Gán vào set (nếu entity có quan hệ cascade)
            // order.setOrderCoupons(Set.of(orderCoupon));
            // Hoặc lưu thủ công sau khi save order (tùy cấu hình cascade của bạn)
        }

        // 6. Tính tổng tiền cuối cùng
        BigDecimal finalTotal = provisionalTotal.subtract(discountAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        // 7. LƯU TOTAL AMOUNT VÀO ENTITY (Quan trọng)
        order.setTotalAmount(finalTotal);

        // 8. Lưu Order xuống DB
        OrderEntity savedOrder = orderRepository.save(order);

        // 9. Lưu OrderDetails và Trừ Tồn Kho
        for (OrderDetailEntity detail : orderDetails) {
            detail.setOrder(savedOrder); // Gán khóa ngoại

            // Trừ tồn kho
            ProductEntity product = detail.getProduct();
            product.setStock(product.getStock() - detail.getQty());
            productRepository.save(product);
        }
        // Cập nhật danh sách chi tiết vào order để Hibernate xử lý (nếu dùng CascadeType.ALL)
        savedOrder.setOrderDetails(new java.util.HashSet<>(orderDetails));
        orderRepository.save(savedOrder);

        // 10. Xóa giỏ hàng (nếu là mua từ giỏ)
        if (request.getItems() == null || request.getItems().isEmpty()) {
            cartService.clearCart(user.getUsername());
        }

        // 11. Trả về Response
        return OrderResponse.fromEntity(savedOrder, savedOrder.getTotalAmount());
    }

    @Override
    public List<OrderResponse> getMyOrders(UserDetails userDetails) {
        log.info("User {} is fetching order history", userDetails.getUsername());

        // Lấy danh sách đơn hàng từ DB (Sắp xếp mới nhất trước)
        List<OrderEntity> orders = orderRepository.findByCustomerUsernameOrderByCreatedAtDesc(userDetails.getUsername());

        // Chuyển đổi sang DTO
        // Vì đã lưu totalAmount trong DB, ta lấy trực tiếp ra dùng
        return orders.stream()
                .map(order -> OrderResponse.fromEntity(order, order.getTotalAmount()))
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id, UserDetails userDetails) {
        log.info("User {} getting order detail {}", userDetails.getUsername(), id);

        // 1. Tìm đơn hàng
        OrderEntity order = orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // 2. KIỂM TRA QUYỀN SỞ HỮU (Bảo mật)
        // User chỉ được xem đơn hàng của chính mình
        if (!order.getCustomer().getUsername().equals(userDetails.getUsername())) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
            // (Ném lỗi Not Found để kẻ tấn công không biết đơn hàng này có tồn tại hay không)
        }

        return OrderResponse.fromEntity(order, order.getTotalAmount());
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus, UserDetails userDetails) {
        log.info("Updating status of order {} to {} by {}", orderId, newStatus, userDetails.getUsername());

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // 1. Kiểm tra luồng chuyển đổi (State Machine)
        validateStatusTransition(order.getStatus(), newStatus);

        // 2. Xử lý Hủy đơn (Hoàn tồn kho)
        if (newStatus == OrderStatus.CANCELLED) {
            for (OrderDetailEntity detail : order.getOrderDetails()) {
                ProductEntity product = detail.getProduct();
                product.setStock(product.getStock() + detail.getQty());
                productRepository.save(product);
            }
            log.info("Restocked products for cancelled order {}", orderId);
        }

        // 3. Cập nhật
        order.setStatus(newStatus);
        OrderEntity savedOrder = orderRepository.save(order);

        return OrderResponse.fromEntity(savedOrder, savedOrder.getTotalAmount());
    }

    /**
     * Logic kiểm soát chuyển đổi trạng thái chặt chẽ (Đã thêm SHIPPING)
     * PENDING -> (PAID) -> SHIPPING -> DELIVERED -> COMPLETED
     */
    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == next) return;

        if (current == OrderStatus.CANCELLED || current == OrderStatus.COMPLETED || current == OrderStatus.REFUNDED) {
            throw new RuntimeException("Không thể thay đổi trạng thái đơn hàng đã kết thúc (" + current + ")");
        }

        switch (next) {
            case PAID:
                // Từ PENDING (Online), hoặc SHIPPING/DELIVERED (COD trả tiền sau)
                if (current != OrderStatus.PENDING && current != OrderStatus.SHIPPING && current != OrderStatus.DELIVERED) {
                    throw new RuntimeException("Không thể chuyển sang PAID từ " + current);
                }
                break;

            case SHIPPING:
                // [MỚI] Admin giao cho Shipper
                // Chỉ được từ PENDING hoặc PAID
                if (current != OrderStatus.PENDING && current != OrderStatus.PAID) {
                    throw new RuntimeException("Chỉ có thể giao hàng (SHIPPING) khi đơn hàng đang PENDING hoặc PAID.");
                }
                break;

            case DELIVERED:
                // Shipper báo giao xong
                // Phải từ SHIPPING chuyển sang
                if (current != OrderStatus.SHIPPING) {
                    throw new RuntimeException("Đơn hàng phải đang giao (SHIPPING) mới có thể chuyển thành Đã giao (DELIVERED).");
                }
                break;

            case COMPLETED:
                // Khách xác nhận
                // Phải đã giao (DELIVERED) hoặc đã thanh toán (PAID - trường hợp không ship)
                if (current != OrderStatus.DELIVERED && current != OrderStatus.PAID) {
                    throw new RuntimeException("Chỉ có thể hoàn tất đơn hàng khi đã giao hàng (DELIVERED).");
                }
                break;

            case CANCELLED:
                // Chỉ hủy khi chưa giao
                if (current == OrderStatus.SHIPPING || current == OrderStatus.DELIVERED) {
                    throw new RuntimeException("Không thể hủy đơn hàng đang giao hoặc đã giao.");
                }
                break;

            case REFUNDED:
                if (current == OrderStatus.PENDING) {
                    throw new RuntimeException("Đơn hàng chưa thanh toán, không thể hoàn tiền.");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void cancelOrderUser(Long orderId, UserDetails userDetails) {
        log.info("User {} requesting cancel order {}", userDetails.getUsername(), orderId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // 1. Kiểm tra quyền sở hữu (Bảo mật)
        if (!order.getCustomer().getUsername().equals(userDetails.getUsername())) {
            // Ném lỗi Not Found để tránh lộ thông tin đơn hàng của người khác
            throw new ResourceNotFoundException("Order not found");
        }

        // 2. Kiểm tra trạng thái
        // Khách hàng CHỈ được hủy khi đơn hàng còn PENDING (chưa xử lý/chưa giao)
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Bạn chỉ có thể hủy đơn hàng khi đang chờ xử lý (PENDING). Trạng thái hiện tại: " + order.getStatus());
        }

        // 3. Hoàn lại tồn kho (Restock)
        for (OrderDetailEntity detail : order.getOrderDetails()) {
            ProductEntity product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQty());
            productRepository.save(product);
        }
        log.info("Restocked products for user-cancelled order {}", orderId);

        // 4. Cập nhật trạng thái
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // --- Helper Methods ---

    private OrderDetailEntity createOrderDetail(Long productId, Integer quantity) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        // Kiểm tra tồn kho
        if (product.getStock() < quantity) {
            throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ số lượng tồn kho (Còn: " + product.getStock() + ")");
        }

        OrderDetailEntity detail = new OrderDetailEntity();
        detail.setProduct(product);
        // Lưu ý: Enum ItemType phải khớp với định nghĩa của bạn (chữ thường/hoa)
        detail.setItemType(ItemType.product);
        detail.setName(product.getName()); // Snapshot tên sản phẩm
        detail.setQty(quantity);
        detail.setUnitPrice(product.getPrice()); // Snapshot giá
        detail.setDiscount(BigDecimal.ZERO); // Tạm thời chưa có giảm giá từng món

        return detail;
    }

    private CouponEntity validateCoupon(String code, BigDecimal orderValue) {
        CouponEntity coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

        if (!coupon.getActive()) {
            throw new RuntimeException("Mã giảm giá đã hết hạn hoặc bị khóa");
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartsAt() != null && now.isBefore(coupon.getStartsAt())) {
            throw new RuntimeException("Mã giảm giá chưa đến thời gian áp dụng");
        }
        if (coupon.getEndsAt() != null && now.isAfter(coupon.getEndsAt())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn");
        }
        if (coupon.getUsageLimit() != null && coupon.getUsageLimit() <= 0) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
        }
        if (orderValue.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu (" + coupon.getMinOrderValue() + ") để áp dụng mã này");
        }

        return coupon;
    }
}