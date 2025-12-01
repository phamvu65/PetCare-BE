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
        // 🟢 MAP THÊM usedCount VÀO RESPONSE
        return couponRepository.findAll().stream()
                .map(entity -> {
                    CouponResponse response = CouponResponse.fromEntity(entity);
                    long used = couponRepository.countUsage(entity.getId());
                    response.setUsedCount(used);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponse create(CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã khuyến mãi đã tồn tại!");
        }

        CouponEntity coupon = CouponEntity.builder()
                .code(request.getCode().toUpperCase())
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mã khuyến mãi"));

        long usageCount = couponRepository.countUsage(id);

        // Logic bảo vệ dữ liệu lịch sử
        if (usageCount > 0) {
            if (!coupon.getCode().equals(request.getCode().toUpperCase()) ||
                    !coupon.getType().equals(request.getType()) ||
                    coupon.getValue().compareTo(request.getValue()) != 0 ||
                    coupon.getMinOrderValue().compareTo(request.getMinOrderValue()) != 0) {

                throw new RuntimeException("Mã này đã có người sử dụng! Chỉ được sửa: Số lượng, Hạn sử dụng, Trạng thái.");
            }
        } else {
            if (!coupon.getCode().equals(request.getCode()) && couponRepository.existsByCode(request.getCode())) {
                throw new RuntimeException("Mã khuyến mãi mới bị trùng!");
            }
            coupon.setCode(request.getCode().toUpperCase());
            coupon.setType(request.getType());
            coupon.setValue(request.getValue());
            coupon.setMinOrderValue(request.getMinOrderValue());
            coupon.setStartsAt(request.getStartsAt());
        }

        coupon.setEndsAt(request.getEndsAt());
        coupon.setUsageLimit(request.getUsageLimit());

        // 🟢 Cập nhật trạng thái (Dùng để Dừng/Kích hoạt lại)
        if (request.getActive() != null) {
            coupon.setActive(request.getActive());
        }

        return CouponResponse.fromEntity(couponRepository.save(coupon));
    }

    @Override
    public void delete(Long id) {
        // Hàm này vẫn giữ để xóa cứng nếu chưa ai dùng
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy mã khuyến mãi");
        }
        long usageCount = couponRepository.countUsage(id);
        if (usageCount > 0) {
            // Nếu đã dùng -> Ném lỗi để FE biết mà hướng dẫn người dùng Dừng
            throw new RuntimeException("Mã này đã có " + usageCount + " lượt dùng. Vui lòng chọn 'Dừng' thay vì Xóa.");
        }
        couponRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getValidCoupons() {
        List<CouponEntity> potentialCoupons = couponRepository.findValidCoupons(LocalDateTime.now());
        return potentialCoupons.stream()
                .filter(c -> {
                    long used = couponRepository.countUsage(c.getId());
                    return used < c.getUsageLimit();
                })
                .map(entity -> {
                    CouponResponse res = CouponResponse.fromEntity(entity);
                    res.setUsedCount(couponRepository.countUsage(entity.getId()));
                    return res;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponse getByCode(String code) {
        CouponEntity coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Mã khuyến mãi không tồn tại"));

        long currentUsage = couponRepository.countUsage(coupon.getId());

        if (!coupon.getActive() ||
                currentUsage >= coupon.getUsageLimit() ||
                LocalDateTime.now().isAfter(coupon.getEndsAt()) ||
                LocalDateTime.now().isBefore(coupon.getStartsAt())) {
            throw new RuntimeException("Mã khuyến mãi đã hết hạn hoặc hết lượt sử dụng");
        }

        CouponResponse res = CouponResponse.fromEntity(coupon);
        res.setUsedCount(currentUsage);
        return res;
    }
}