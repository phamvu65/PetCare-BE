package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest; // Import
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // Import
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import vn.vuxnye.config.VnPayConfig; // Import
import vn.vuxnye.dto.request.PaymentRequest;
import vn.vuxnye.dto.response.PaymentResponse;
import vn.vuxnye.service.PaymentService;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payment Controller")
@RequiredArgsConstructor
@Slf4j(topic = "PAYMENT-CONTROLLER")
public class PaymentController {

    private final PaymentService paymentService;
    private final VnPayConfig vnPayConfig;

    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Map<String, Object> createPayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        PaymentResponse response = paymentService.createPayment(request, userDetails);
        return createResponse(HttpStatus.OK, "Payment initiated", response);
    }

    /**
     * [MỚI] Xem lịch sử thanh toán của đơn hàng
     */
    @GetMapping("/history/{orderId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get payment history of an order")
    public Map<String, Object> getPaymentHistory(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<PaymentResponse> history = paymentService.getPaymentHistory(orderId, userDetails);
        return createResponse(HttpStatus.OK, "Payment history", history);
    }

    /**
     * [MỚI] Xem giao dịch thanh toán thành công
     */
    @GetMapping("/success/{orderId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get successful payment details")
    public Map<String, Object> getSuccessPayment(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        PaymentResponse successPayment = paymentService.getSuccessPayment(orderId, userDetails);
        return createResponse(HttpStatus.OK, "Success payment details", successPayment);
    }

    /**
     * API Return URL từ VNPay
     * (Cần cấu hình vnp_ReturnUrl trong VnPayConfig trỏ về đây)
     *
     */
    @GetMapping("/vnpay-return")
    @Operation(summary = "VNPay Return URL")
    public ResponseEntity<?> vnpayReturn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        // Xác thực chữ ký
        String signValue = vnPayConfig.hmacSHA512(vnPayConfig.getVnp_HashSecret(), buildHashData(fields));

        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                // --- THANH TOÁN THÀNH CÔNG ---
                String txnRef = request.getParameter("vnp_TxnRef");
                String orderIdStr = txnRef.split("_")[0]; // Lấy ID đơn hàng
                Long orderId = Long.parseLong(orderIdStr);

                // Gọi service để xử lý logic nghiệp vụ
                paymentService.handlePaymentSuccess(orderId, txnRef);

                // Trả về trang thông báo thành công (hoặc redirect về FE)
                return ResponseEntity.ok("Thanh toán thành công! Đơn hàng: " + orderId);
            } else {
                return ResponseEntity.badRequest().body("Thanh toán thất bại. Mã lỗi: " + request.getParameter("vnp_ResponseCode"));
            }
        } else {
            return ResponseEntity.badRequest().body("Chữ ký không hợp lệ (Checksum failed)");
        }
    }

    // Helper để build hash data từ params trả về (giống trong Service)
    private String buildHashData(Map<String, String> fields) {
        // Sắp xếp và nối chuỗi để hash (logic giống hệt lúc tạo URL)
        // ... (Bạn có thể tách logic này vào VnPayConfig để tái sử dụng)
        // Ở đây mình viết gọn để demo:
        List<String> fieldNames = new java.util.ArrayList<>(fields.keySet());
        java.util.Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        java.util.Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(java.net.URLEncoder.encode(fieldValue, java.nio.charset.StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    sb.append("&");
                }
            }
        }
        return sb.toString();
    }

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