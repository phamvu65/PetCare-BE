package vn.vuxnye.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.vuxnye.common.*;
import vn.vuxnye.dto.request.OrderItemRequest;
import vn.vuxnye.dto.request.OrderRequest;
import vn.vuxnye.dto.response.*; // Import * để lấy các DTO mới
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.*;
import vn.vuxnye.repository.*;
import vn.vuxnye.service.CartService;
import vn.vuxnye.service.OrderService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final OrderCouponRepository orderCouponRepository;
    private final CartService cartService;
    private final AppointmentRepository appointmentRepository;
    private final OrderDetailRepository orderDetailRepository;

    // --- 1. ADMIN: QUẢN LÝ ĐƠN HÀNG ---
    @Override
    @Transactional(readOnly = true)
    public OrderPageResponse getAllOrders(Long userId, OrderStatus status, LocalDate fromDate, LocalDate toDate, int page, int size) {
        log.info("Admin fetching orders. User: {}, Status: {}, Date: {} - {}", userId, status, fromDate, toDate);

        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size, Sort.by("createdAt").descending());
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

        Instant start = (fromDate != null) ? fromDate.atStartOfDay(zoneId).toInstant() : null;
        Instant end = (toDate != null) ? toDate.plusDays(1).atStartOfDay(zoneId).toInstant() : null;

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

    // --- 2. THỐNG KÊ DASHBOARD (TOP 5 + DOANH THU) ---
    @Override
    @Transactional(readOnly = true)
    public OrderStatisticResponse getStatistics(LocalDate fromDate, LocalDate toDate) {
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

        Instant startInstant = (fromDate != null) ? fromDate.atStartOfDay(zoneId).toInstant() : null;
        Instant endInstant = (toDate != null) ? toDate.plusDays(1).atStartOfDay(zoneId).toInstant() : null;

        LocalDateTime startLocal = (fromDate != null) ? fromDate.atStartOfDay() : null;
        LocalDateTime endLocal = (toDate != null) ? toDate.plusDays(1).atStartOfDay() : null;

        // A. Thống kê Đơn hàng
        BigDecimal orderRevenue = orderRepository.countRevenue(OrderStatus.COMPLETED, startInstant, endInstant);
        if (orderRevenue == null) orderRevenue = BigDecimal.ZERO;

        Long newOrders = orderRepository.countByStatusAndDate(OrderStatus.PENDING, startInstant, endInstant);
        Long shippingOrders = orderRepository.countByStatusAndDate(OrderStatus.SHIPPING, startInstant, endInstant);
        Long cancelledOrders = orderRepository.countByStatusAndDate(OrderStatus.CANCELLED, startInstant, endInstant);
        Long successOrders = orderRepository.countByStatusAndDate(OrderStatus.COMPLETED, startInstant, endInstant);
        Long totalOrders = orderRepository.countTotalByDate(startInstant, endInstant);

        // B. Thống kê Dịch vụ
        BigDecimal serviceRevenue = appointmentRepository.countRevenue(AppointmentStatus.DONE, startLocal, endLocal);
        if (serviceRevenue == null) serviceRevenue = BigDecimal.ZERO;

        Long totalAppointments = appointmentRepository.countTotalByDate(startLocal, endLocal);
        Long completedAppointments = appointmentRepository.countByStatusAndDate(AppointmentStatus.DONE, startLocal, endLocal);
        Long cancelledAppointments = appointmentRepository.countByStatusAndDate(AppointmentStatus.CANCELLED, startLocal, endLocal);

        // C. Biểu đồ
        Map<String, OrderStatisticResponse.DailyRevenue> dailyMap = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");

        List<OrderEntity> completedOrders = orderRepository.findCompletedOrdersBetween(OrderStatus.COMPLETED, startInstant, endInstant);
        for (OrderEntity order : completedOrders) {
            if (order.getCreatedAt() != null) {
                String dateKey = order.getCreatedAt().atZone(zoneId).format(formatter);
                dailyMap.putIfAbsent(dateKey, new OrderStatisticResponse.DailyRevenue(dateKey, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
                OrderStatisticResponse.DailyRevenue entry = dailyMap.get(dateKey);
                entry.setOrderRevenue(entry.getOrderRevenue().add(order.getTotalAmount()));
                entry.setTotal(entry.getTotal().add(order.getTotalAmount()));
            }
        }

        List<AppointmentEntity> completedApps = appointmentRepository.findCompletedAppointmentsBetween(AppointmentStatus.DONE, startLocal, endLocal);
        for (AppointmentEntity appt : completedApps) {
            if (appt.getScheduledAt() != null) {
                String dateKey = appt.getScheduledAt().format(formatter);
                dailyMap.putIfAbsent(dateKey, new OrderStatisticResponse.DailyRevenue(dateKey, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
                OrderStatisticResponse.DailyRevenue entry = dailyMap.get(dateKey);

                BigDecimal price = (appt.getService() != null) ? appt.getService().getPrice() : BigDecimal.ZERO;
                entry.setServiceRevenue(entry.getServiceRevenue().add(price));
                entry.setTotal(entry.getTotal().add(price));
            }
        }

        List<OrderStatisticResponse.DailyRevenue> chartData = new ArrayList<>(dailyMap.values());
        chartData.sort(Comparator.comparing(OrderStatisticResponse.DailyRevenue::getDate));

        // D. Top sản phẩm & Tồn kho thấp (Cho Dashboard)
        List<ProductStatsResponse> topSelling = orderDetailRepository.findTopSellingProducts(
                OrderStatus.COMPLETED, startInstant, endInstant, PageRequest.of(0, 5)
        );

        List<ProductEntity> lowStockEntities = productRepository.findLowStockProducts(10);
        List<OrderStatisticResponse.LowStockDto> lowStockDtos = lowStockEntities.stream()
                .map(p -> OrderStatisticResponse.LowStockDto.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .stock(p.getStock())
                        .price(p.getPrice())
                        .build())
                .collect(Collectors.toList());


        List<ServiceStatsResponse> topServices = appointmentRepository.findTopServices(
                AppointmentStatus.DONE, startLocal, endLocal, PageRequest.of(0, 5)
        );

        List<EmployeeStatsResponse> topEmployees = appointmentRepository.findTopEmployees(
                AppointmentStatus.DONE, startLocal, endLocal, PageRequest.of(0, 5)
        );

        return OrderStatisticResponse.builder()
                .totalRevenue(orderRevenue.add(serviceRevenue))
                .totalOrderRevenue(orderRevenue)
                .newOrders(newOrders)
                .shippingOrders(shippingOrders)
                .cancelledOrders(cancelledOrders)
                .totalOrders(totalOrders)
                .successOrders(successOrders)
                .totalServiceRevenue(serviceRevenue)
                .totalAppointments(totalAppointments)
                .completedAppointments(completedAppointments)
                .cancelledAppointments(cancelledAppointments)
                .chartData(chartData)
                .topSellingProducts(topSelling)
                .lowStockProducts(lowStockDtos)
                // --- Set 2 trường mới vào ---
                .topServices(topServices)
                .topEmployees(topEmployees)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductStatsResponse> getProductSalesStats(LocalDate fromDate, LocalDate toDate, int page, int size, String sortField, String sortDir) {
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        Instant start = (fromDate != null) ? fromDate.atStartOfDay(zoneId).toInstant() : null;
        Instant end = (toDate != null) ? toDate.plusDays(1).atStartOfDay(zoneId).toInstant() : null;

        Sort sort;
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        if ("revenue".equalsIgnoreCase(sortField)) {
            sort = JpaSort.unsafe(direction, "SUM(CASE WHEN o.status = vn.vuxnye.common.OrderStatus.COMPLETED THEN (od.qty * od.unitPrice) ELSE 0 END)");
        } else if ("totalSold".equalsIgnoreCase(sortField)) {
            sort = JpaSort.unsafe(direction, "SUM(CASE WHEN o.status = vn.vuxnye.common.OrderStatus.COMPLETED THEN od.qty ELSE 0 END)");
        } else if ("name".equalsIgnoreCase(sortField)) {
            sort = Sort.by(direction, "p.name");
        } else if ("stock".equalsIgnoreCase(sortField)) {
            sort = Sort.by(direction, "p.stock");
        } else {
            sort = Sort.by(direction, "p.id");
        }

        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size, sort);

        return productRepository.getAllProductSalesStats(
                OrderStatus.COMPLETED, start, end, pageable
        );
    }

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
        CouponEntity appliedCoupon = null;

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            appliedCoupon = validateCoupon(request.getCouponCode(), provisionalTotal);
            if (appliedCoupon.getType() == CouponType.percent) {
                BigDecimal percent = appliedCoupon.getValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                discountAmount = provisionalTotal.multiply(percent);
            } else {
                discountAmount = appliedCoupon.getValue();
            }
            if (discountAmount.compareTo(provisionalTotal) > 0) {
                discountAmount = provisionalTotal;
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

        if (appliedCoupon != null) {
            OrderCouponEntity orderCoupon = new OrderCouponEntity();
            orderCoupon.setOrder(savedOrder);
            orderCoupon.setCoupon(appliedCoupon);
            orderCoupon.setDiscountValue(discountAmount);
            orderCouponRepository.save(orderCoupon);
        }

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
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartsAt()) || now.isAfter(coupon.getEndsAt())) {
            throw new RuntimeException("Mã giảm giá chưa diễn ra hoặc đã hết hạn");
        }
        if (orderValue.compareTo(coupon.getMinOrderValue()) < 0) throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu");
        long usedCount = couponRepository.countUsage(coupon.getId());
        if (usedCount >= coupon.getUsageLimit()) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng!");
        }
        return coupon;
    }
}