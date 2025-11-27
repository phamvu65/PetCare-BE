package vn.vuxnye.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.model.CouponEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CouponResponse {
    private Long id;
    private String code;
    private String type;
    private BigDecimal value;
    private BigDecimal minOrderValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startsAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endsAt;

    private Integer usageLimit;
    private Boolean active;

    public static CouponResponse fromEntity(CouponEntity entity) {
        return CouponResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .type(entity.getType().name())
                .value(entity.getValue())
                .minOrderValue(entity.getMinOrderValue())
                .startsAt(entity.getStartsAt())
                .endsAt(entity.getEndsAt())
                .usageLimit(entity.getUsageLimit())
                .active(entity.getActive())
                .build();
    }
}