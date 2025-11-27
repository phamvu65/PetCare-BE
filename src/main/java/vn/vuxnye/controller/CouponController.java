package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.dto.request.CouponRequest;
import vn.vuxnye.dto.response.CouponResponse;
import vn.vuxnye.service.CouponService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupons")
@Tag(name = "Coupon Controller")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // --- ADMIN APIs ---

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Get all coupons")
    public Map<String, Object> getAllCoupons() {
        List<CouponResponse> coupons = couponService.findAll();
        return createResponse(HttpStatus.OK, "Get all coupons success", coupons);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Create coupon")
    public Map<String, Object> createCoupon(@Valid @RequestBody CouponRequest request) {
        CouponResponse response = couponService.create(request);
        return createResponse(HttpStatus.CREATED, "Create coupon success", response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Update coupon")
    public Map<String, Object> updateCoupon(@PathVariable Long id, @Valid @RequestBody CouponRequest request) {
        CouponResponse response = couponService.update(id, request);
        return createResponse(HttpStatus.OK, "Update coupon success", response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Delete coupon")
    public Map<String, Object> deleteCoupon(@PathVariable Long id) {
        couponService.delete(id);
        return createResponse(HttpStatus.NO_CONTENT, "Delete coupon success", null);
    }

    // --- USER APIs ---

    @GetMapping("/public/valid")
    @Operation(summary = "Get valid coupons", description = "List coupons available for use")
    public Map<String, Object> getValidCoupons() {
        List<CouponResponse> coupons = couponService.getValidCoupons();
        return createResponse(HttpStatus.OK, "Get valid coupons success", coupons);
    }

    @GetMapping("/check/{code}")
    @Operation(summary = "Check coupon details by code")
    public Map<String, Object> checkCoupon(@PathVariable String code) {
        CouponResponse coupon = couponService.getByCode(code);
        return createResponse(HttpStatus.OK, "Coupon found", coupon);
    }

    // Helper
    private Map<String, Object> createResponse(HttpStatus status, String message, Object data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", status.value());
        result.put("message", message);
        result.put("data", data);
        return result;
    }
}