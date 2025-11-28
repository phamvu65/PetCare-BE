package vn.vuxnye.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.model.AppointmentEntity;
import vn.vuxnye.common.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AppointmentResponse {
    private Long id;

    // Thông tin khách hàng
    private Long customerId;
    private String customerName;
    private String customerPhone;

    // Thông tin thú cưng
    private Long petId;
    private String petName;

    // Thông tin dịch vụ
    private Long serviceId;
    private String serviceName;
    private BigDecimal servicePrice;

    // Thông tin nhân viên (có thể null)
    private Long staffId;
    private String staffName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledAt;

    private AppointmentStatus status;
    private String note;

    public static AppointmentResponse fromEntity(AppointmentEntity entity) {
        return AppointmentResponse.builder()
                .id(entity.getId())
                .customerId(entity.getCustomer().getId())
                // Khách hàng: First + Last
                .customerName(entity.getCustomer().getFirstName() + " " + entity.getCustomer().getLastName())
                .customerPhone(entity.getCustomer().getPhone())

                .petId(entity.getPet().getId())
                .petName(entity.getPet().getName())

                .serviceId(entity.getService().getId())
                .serviceName(entity.getService().getName())
                .servicePrice(entity.getService().getPrice())

                // --- SỬA ĐOẠN NÀY ---
                .staffId(entity.getStaff() != null ? entity.getStaff().getId() : null)
                .staffName(entity.getStaff() != null
                        ? entity.getStaff().getFirstName() + " " + entity.getStaff().getLastName()
                        : "Unassigned")
                // --------------------

                .scheduledAt(entity.getScheduledAt())
                .status(entity.getStatus())
                .note(entity.getNote())
                .build();
    }
}