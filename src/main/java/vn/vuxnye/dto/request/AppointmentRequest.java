package vn.vuxnye.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.common.AppointmentStatus;
import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentRequest {
    //  Dùng cho Admin/Staff khi đặt lịch hộ khách
    // Khách hàng tự đặt thì không cần gửi trường này (để null)
    private Long customerId;

    @NotNull(message = "Pet ID must not be null")
    private Long petId;

    @NotNull(message = "Service ID must not be null")
    private Long serviceId;

    // Staff có thể null lúc đặt, admin sẽ gán sau
    private Long staffId;

    @NotNull(message = "Scheduled time must not be null")
    @Future(message = "Scheduled time must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledAt;

    private String note;

    // Dùng cho Admin khi cập nhật trạng thái
    private AppointmentStatus status;
}