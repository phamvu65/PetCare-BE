package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.common.ResponseAPI;
import vn.vuxnye.dto.request.CouponRequest;
import vn.vuxnye.dto.response.CouponResponse;
import vn.vuxnye.service.CouponService;

import java.util.List;

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
    public ResponseAPI getAllCoupons() {
        List<CouponResponse> coupons = couponService.findAll();

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Get all coupons success")
                .data(coupons)
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Create coupon")
    public ResponseAPI createCoupon(@Valid @RequestBody CouponRequest request) {
        CouponResponse response = couponService.create(request);

        return ResponseAPI.builder()
                .status(HttpStatus.CREATED)
                .message("Create coupon success")
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Update coupon")
    public ResponseAPI updateCoupon(@PathVariable Long id, @Valid @RequestBody CouponRequest request) {
        CouponResponse response = couponService.update(id, request);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Update coupon success")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Delete coupon")
    public ResponseAPI deleteCoupon(@PathVariable Long id) {
        couponService.delete(id);

        return ResponseAPI.builder()
                .status(HttpStatus.OK) // Đổi từ NO_CONTENT sang OK để giữ lại Body JSON
                .message("Delete coupon success")
                .data(null)
                .build();
    }

    // --- USER APIs ---

    @GetMapping("/public/valid")
    @Operation(summary = "Get valid coupons", description = "List coupons available for use")
    public ResponseAPI getValidCoupons() {
        List<CouponResponse> coupons = couponService.getValidCoupons();

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Get valid coupons success")
                .data(coupons)
                .build();
    }

    @GetMapping("/check/{code}")
    @Operation(summary = "Check coupon details by code")
    public ResponseAPI checkCoupon(@PathVariable String code) {
        CouponResponse coupon = couponService.getByCode(code);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Coupon found")
                .data(coupon)
                .build();
    }
}