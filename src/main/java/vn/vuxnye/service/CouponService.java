package vn.vuxnye.service;

import vn.vuxnye.dto.request.CouponRequest;
import vn.vuxnye.dto.response.CouponResponse;
import java.util.List;

public interface CouponService {
    // Admin CRUD
    List<CouponResponse> findAll();
    CouponResponse create(CouponRequest request);
    CouponResponse update(Long id, CouponRequest request);
    void delete(Long id);

    // User
    List<CouponResponse> getValidCoupons();
    CouponResponse getByCode(String code); // Để check mã
}