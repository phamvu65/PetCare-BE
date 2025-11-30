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
import vn.vuxnye.dto.response.OrderStatisticResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.*;
import vn.vuxnye.repository.*;
import vn.vuxnye.service.CartService;
import vn.vuxnye.service.OrderService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private final CartService cartService;

    // --- 1. LOGIC MỚI: QUẢN LÝ & THỐNG KÊ (ADMIN) ---

    @Override
    @Transactional(readOnly = true)
    // 🟢 ĐÃ SỬA: Thêm tham số Long userId
    public OrderPageResponse getAllOrders(Long userId, OrderStatus status, LocalDate fromDate, LocalDate toDate, int page, int size) {
        log.info("Admin fetching orders. User: {}, Status: {}, Date: {} - {}", userId, status, fromDate, toDate);

        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size, Sort.by("createdAt").descending());

        // 🟢 CẤU HÌNH MÚI GIỜ VIỆT NAM
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

        // Convert LocalDate -> Instant
        Instant start = (fromDate != null) ? fromDate.atStartOfDay(zoneId).toInstant() : null;
        Instant end = (toDate != null) ? toDate.plusDays(1).atStartOfDay(zoneId).toInstant() : null;

        // 🟢 ĐÃ SỬA: Truyền userId vào repository
        Page<OrderEntity> orderPage = orderRepository.findOrdersByFilter(userId, status, start, end, pageable);

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
    @Transactional(readOnly = true)
    public OrderStatisticResponse getStatistics(LocalDate fromDate, LocalDate toDate) {
        // 🟢 CẤU HÌNH MÚI GIỜ VIỆT NAM
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

        Instant start = (fromDate != null) ? fromDate.atStartOfDay(zoneId).toInstant() : null;
        Instant end = (toDate != null) ? toDate.plusDays(1).atStartOfDay(zoneId).toInstant() : null;

        BigDecimal revenue = orderRepository.countRevenue(OrderStatus.COMPLETED, start, end);
        if (revenue == null) revenue = BigDecimal.ZERO;

        Long newOrders = orderRepository.countByStatusAndDate(OrderStatus.PENDING, start, end);
        Long shippingOrders = orderRepository.countByStatusAndDate(OrderStatus.SHIPPING, start, end);
        Long cancelledOrders = orderRepository.countByStatusAndDate(OrderStatus.CANCELLED, start, end);
        Long totalOrders = orderRepository.countTotalByDate(start, end);

        List<OrderEntity> completedOrders = orderRepository.findCompletedOrdersBetween(OrderStatus.COMPLETED, start, end);

        Map<String, BigDecimal> dailyMap = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (OrderEntity order : completedOrders) {
            if (order.getCreatedAt() != null) {
                String dateKey = order.getCreatedAt().atZone(zoneId).format(formatter);
                dailyMap.put(dateKey, dailyMap.getOrDefault(dateKey, BigDecimal.ZERO).add(order.getTotalAmount()));
            }
        }

        List<OrderStatisticResponse.DailyRevenue> chartData = new ArrayList<>();
        dailyMap.forEach((date, total) -> chartData.add(new OrderStatisticResponse.DailyRevenue(date, total)));

        return OrderStatisticResponse.builder()
                .revenue(revenue)
                .newOrders(newOrders)
                .shippingOrders(shippingOrders)
                .cancelledOrders(cancelledOrders)
                .totalOrders(totalOrders)
                .chartData(chartData)
                .build();
    }

    // --- CÁC HÀM CŨ GIỮ NGUYÊN (Create, Update, Cancel...) ---

    @Override
    public OrderResponse getOrderById(Long id) {
        OrderEntity order = orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return OrderResponse.fromEntity(order, order.getTotalAmount());
    }

    @Override
    public OrderResponse createOrder(UserDetails userDetails, OrderRequest request) {
        log.info("Creating order for user: {}", userDetails.getUsername());
        UserEntity user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        AddressEntity address = addressRepository.findByIdAndUserId(request.getAddressId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found or does not belong to user"));

        List<OrderDetailEntity> orderDetails = new ArrayList<>();
        BigDecimal provisionalTotal = BigDecimal.ZERO;

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (OrderItemRequest itemReq : request.getItems()) {
                OrderDetailEntity detail = createOrderDetail(itemReq.getProductId(), itemReq.getQuantity());
                orderDetails.add(detail);
                BigDecimal linePrice = detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQty()));
                provisionalTotal = provisionalTotal.add(linePrice);
            }
        } else {
            CartEntity cart = cartRepository.findByUsername(user.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Cart is empty"));
            if (cart.getItems().isEmpty()) { throw new RuntimeException("Giỏ hàng trống"); }
            for (CartItemEntity cartItem : cart.getItems()) {
                OrderDetailEntity detail = createOrderDetail(cartItem.getProduct().getId(), cartItem.getQuantity());
                orderDetails.add(detail);
                BigDecimal linePrice = detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQty()));
                provisionalTotal = provisionalTotal.add(linePrice);
            }
        }

        OrderEntity order = new OrderEntity();
        order.setCustomer(user);
        order.setChannel(OrderChannel.WEB);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNote(request.getNote());
        order.setShippingAddress(address.getAddressDetail() + ", " + address.getWard() + ", " + address.getCity());

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            CouponEntity coupon = validateCoupon(request.getCouponCode(), provisionalTotal);
            if (coupon.getType() == CouponType.percent) {
                BigDecimal percent = coupon.getValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                discountAmount = provisionalTotal.multiply(percent);
            } else {
                discountAmount = coupon.getValue();
            }
        }
        BigDecimal finalTotal = provisionalTotal.subtract(discountAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) { finalTotal = BigDecimal.ZERO; }
        order.setTotalAmount(finalTotal);

        OrderEntity savedOrder = orderRepository.save(order);
        for (OrderDetailEntity detail : orderDetails) {
            detail.setOrder(savedOrder);
            ProductEntity product = detail.getProduct();
            product.setStock(product.getStock() - detail.getQty());
            productRepository.save(product);
        }
        savedOrder.setOrderDetails(new java.util.HashSet<>(orderDetails));
        orderRepository.save(savedOrder);

        if (request.getItems() == null || request.getItems().isEmpty()) {
            cartService.clearCart(user.getUsername());
        }
        return OrderResponse.fromEntity(savedOrder, savedOrder.getTotalAmount());
    }

    @Override
    public List<OrderResponse> getMyOrders(UserDetails userDetails) {
        List<OrderEntity> orders = orderRepository.findByCustomerUsernameOrderByCreatedAtDesc(userDetails.getUsername());
        return orders.stream().map(order -> OrderResponse.fromEntity(order, order.getTotalAmount())).collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id, UserDetails userDetails) {
        OrderEntity order = orderRepository.findByIdWithDetails(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getCustomer().getUsername().equals(userDetails.getUsername())) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        return OrderResponse.fromEntity(order, order.getTotalAmount());
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus, UserDetails userDetails) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        validateStatusTransition(order.getStatus(), newStatus);
        if (newStatus == OrderStatus.CANCELLED) {
            for (OrderDetailEntity detail : order.getOrderDetails()) {
                ProductEntity product = detail.getProduct();
                product.setStock(product.getStock() + detail.getQty());
                productRepository.save(product);
            }
        }
        order.setStatus(newStatus);
        OrderEntity savedOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(savedOrder, savedOrder.getTotalAmount());
    }

    @Override
    public void cancelOrderUser(Long id, UserDetails userDetails) {
        OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getCustomer().getUsername().equals(userDetails.getUsername())) { throw new ResourceNotFoundException("Order not found"); }
        if (order.getStatus() != OrderStatus.PENDING) { throw new RuntimeException("Bạn chỉ có thể hủy đơn hàng khi đang chờ xử lý (PENDING)."); }
        for (OrderDetailEntity detail : order.getOrderDetails()) {
            ProductEntity product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQty());
            productRepository.save(product);
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == next) return;
        if (current == OrderStatus.CANCELLED || current == OrderStatus.COMPLETED || current == OrderStatus.REFUNDED) {
            throw new RuntimeException("Không thể thay đổi trạng thái đơn hàng đã kết thúc (" + current + ")");
        }
        switch (next) {
            case PAID: if (current != OrderStatus.PENDING && current != OrderStatus.SHIPPING && current != OrderStatus.DELIVERED) throw new RuntimeException("Lỗi trạng thái"); break;
            case SHIPPING: if (current != OrderStatus.PENDING && current != OrderStatus.PAID) throw new RuntimeException("Lỗi trạng thái"); break;
            case DELIVERED: if (current != OrderStatus.SHIPPING) throw new RuntimeException("Lỗi trạng thái"); break;
            case COMPLETED: if (current != OrderStatus.DELIVERED && current != OrderStatus.PAID) throw new RuntimeException("Lỗi trạng thái"); break;
            case CANCELLED: if (current == OrderStatus.SHIPPING || current == OrderStatus.DELIVERED) throw new RuntimeException("Lỗi trạng thái"); break;
            case REFUNDED: if (current == OrderStatus.PENDING) throw new RuntimeException("Lỗi trạng thái"); break;
            default: break;
        }
    }

    private OrderDetailEntity createOrderDetail(Long productId, Integer quantity) {
        ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        if (product.getStock() < quantity) { throw new RuntimeException("Sản phẩm hết hàng"); }
        OrderDetailEntity detail = new OrderDetailEntity();
        detail.setProduct(product);
        detail.setItemType(ItemType.product);
        detail.setName(product.getName());
        detail.setQty(quantity);
        detail.setUnitPrice(product.getPrice());
        detail.setDiscount(BigDecimal.ZERO);
        return detail;
    }

    private CouponEntity validateCoupon(String code, BigDecimal orderValue) {
        CouponEntity coupon = couponRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));
        if (!coupon.getActive()) throw new RuntimeException("Mã không hợp lệ");
        if (orderValue.compareTo(coupon.getMinOrderValue()) < 0) throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu");
        return coupon;
    }
}