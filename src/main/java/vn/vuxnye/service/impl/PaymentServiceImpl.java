package vn.vuxnye.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.vuxnye.common.OrderStatus;
import vn.vuxnye.config.VnPayConfig;
import vn.vuxnye.dto.request.PaymentRequest;
import vn.vuxnye.dto.response.PaymentResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.OrderEntity;
import vn.vuxnye.model.PaymentEntity;
import vn.vuxnye.common.PaymentMethod;
import vn.vuxnye.repository.OrderRepository;
import vn.vuxnye.repository.PaymentRepository;
import vn.vuxnye.service.PaymentService;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "PAYMENT-IMPL")
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final VnPayConfig vnPayConfig;

    @Override
    public PaymentResponse createPayment(PaymentRequest request, UserDetails userDetails) {
        log.info("Initiating payment for order: {}", request.getOrderId());

        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getCustomer().getUsername().equals(userDetails.getUsername())) {
            throw new ResourceNotFoundException("Order not found");
        }

        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Đơn hàng đã thanh toán hoặc đã hủy.");
        }

        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setNote(request.getNote());
        payment.setPaidAt(null);

        String paymentUrl = "";
        String message = "";

        if (request.getMethod() == PaymentMethod.COD) {
            message = "Đã xác nhận COD. Vui lòng thanh toán khi nhận hàng.";
        } else if (request.getMethod() == PaymentMethod.E_WALLET || request.getMethod() == PaymentMethod.CARD || request.getMethod() == PaymentMethod.TRANSFER) {
            paymentUrl = createVnPayUrl(order);
            message = "Vui lòng truy cập đường dẫn để thanh toán qua VNPay.";
        } else {
            message = "Vui lòng chuyển khoản.";
        }

        PaymentEntity savedPayment = paymentRepository.save(payment);

        return mapToResponse(savedPayment, message, paymentUrl);
    }

    // --- [HOÀN THIỆN] Xử lý khi thanh toán thành công ---
    @Override
    public void handlePaymentSuccess(Long orderId, String transactionCode) {
        log.info("Processing payment success callback for order: {}", orderId);

        // 1. Tìm đơn hàng
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // 2. Kiểm tra idempotency (tránh xử lý 2 lần nếu VNPay gọi callback nhiều lần)
        if (order.getStatus() == OrderStatus.PAID) {
            log.info("Order {} is already PAID, skipping update.", orderId);
            return;
        }

        // 3. Cập nhật trạng thái đơn hàng -> PAID
        // Chỉ cập nhật nếu đang PENDING (hoặc SHIPPING nếu cho phép trả sau)
        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
            log.info("Updated order {} status to PAID.", orderId);
        }

        // 4. Lưu lịch sử thanh toán (Ghi nhận giao dịch thành công)
        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setMethod(PaymentMethod.E_WALLET); // VNPay là E_WALLET/CARD
        payment.setPaidAt(LocalDateTime.now()); // Quan trọng: Đánh dấu thời gian đã trả
        payment.setNote("VNPay Success. TransRef: " + transactionCode);

        paymentRepository.save(payment);
    }

    // --- [MỚI] Lấy lịch sử thanh toán ---
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentHistory(Long orderId, UserDetails userDetails) {
        OrderEntity order = checkOrderOwnership(orderId, userDetails);

        List<PaymentEntity> payments = paymentRepository.findByOrderId(order.getId());

        return payments.stream()
                .map(p -> mapToResponse(p, p.getNote(), null))
                .collect(Collectors.toList());
    }

    // --- [MỚI] Lấy thông tin giao dịch thành công ---
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getSuccessPayment(Long orderId, UserDetails userDetails) {
        OrderEntity order = checkOrderOwnership(orderId, userDetails);

        PaymentEntity successPayment = paymentRepository.findFirstByOrderIdAndPaidAtIsNotNullOrderByPaidAtDesc(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No successful payment found for order " + orderId));

        return mapToResponse(successPayment, "Payment Successful", null);
    }

    // --- Helper Methods ---

    private OrderEntity checkOrderOwnership(Long orderId, UserDetails userDetails) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Chỉ cho phép Chủ đơn hàng hoặc Admin xem
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STAFF"));

        if (!isAdmin && !order.getCustomer().getUsername().equals(userDetails.getUsername())) {
            throw new ResourceNotFoundException("Order not found");
        }
        return order;
    }

    private PaymentResponse mapToResponse(PaymentEntity entity, String message, String url) {
        String status = (entity.getPaidAt() != null) ? "SUCCESS" : "PENDING";
        return PaymentResponse.builder()
                .paymentId(entity.getId())
                .orderId(entity.getOrder().getId())
                .status(status)
                .message(message)
                .paymentUrl(url)
                .build();
    }

    private String createVnPayUrl(OrderEntity order) {
        // (Giữ nguyên logic tạo URL VNPay của bạn)
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = "Thanh toan don hang " + order.getId();
        String vnp_TxnRef = String.valueOf(order.getId()) + "_" + System.currentTimeMillis();
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = vnPayConfig.getVnp_TmnCode();

        long amount = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getVnp_ReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = vnPayConfig.hmacSHA512(vnPayConfig.getVnp_HashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return vnPayConfig.getVnp_Url() + "?" + queryUrl;
    }
}