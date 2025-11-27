package vn.vuxnye.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.common.CouponType; // Enum PERCENT, FIXED

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CouponRequest {

    @NotBlank(message = "Code must not be blank")
    private String code;

    @NotNull
    private CouponType type; // PERCENT or FIXED

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal value; // Số tiền giảm hoặc % giảm

    @DecimalMin(value = "0.0")
    private BigDecimal minOrderValue; // Giá trị đơn tối thiểu

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startsAt;

    @Future(message = "End date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endsAt;

    @NotNull
    private Integer usageLimit; // Số lượng mã

    private Boolean active = true;
}