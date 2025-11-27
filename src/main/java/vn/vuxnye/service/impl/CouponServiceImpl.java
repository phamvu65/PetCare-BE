package vn.vuxnye.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.vuxnye.dto.request.CouponRequest;
import vn.vuxnye.dto.response.CouponResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.CouponEntity;
import vn.vuxnye.repository.CouponRepository;
import vn.vuxnye.service.CouponService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> findAll() {
        return couponRepository.findAll().stream()
                .map(CouponResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponse create(CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Coupon code already exists");
        }

        CouponEntity coupon = CouponEntity.builder()
                .code(request.getCode().toUpperCase()) // Mã luôn viết hoa
                .type(request.getType())
                .value(request.getValue())
                .minOrderValue(request.getMinOrderValue())
                .startsAt(request.getStartsAt())
                .endsAt(request.getEndsAt())
                .usageLimit(request.getUsageLimit())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return CouponResponse.fromEntity(couponRepository.save(coupon));
    }

    @Override
    public CouponResponse update(Long id, CouponRequest request) {
        CouponEntity coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        // Nếu đổi mã thì phải check trùng
        if (!coupon.getCode().equals(request.getCode()) && couponRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Coupon code already exists");
        }

        coupon.setCode(request.getCode().toUpperCase());
        coupon.setType(request.getType());
        coupon.setValue(request.getValue());
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setStartsAt(request.getStartsAt());
        coupon.setEndsAt(request.getEndsAt());
        coupon.setUsageLimit(request.getUsageLimit());
        if (request.getActive() != null) coupon.setActive(request.getActive());

        return CouponResponse.fromEntity(couponRepository.save(coupon));
    }

    @Override
    public void delete(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon not found");
        }
        couponRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getValidCoupons() {
        // Lấy các mã còn hạn sử dụng
        return couponRepository.findValidCoupons(LocalDateTime.now()).stream()
                .map(CouponResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponse getByCode(String code) {
        CouponEntity coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        // Logic check hạn sử dụng có thể đặt ở đây nếu cần thiết
        return CouponResponse.fromEntity(coupon);
    }
}