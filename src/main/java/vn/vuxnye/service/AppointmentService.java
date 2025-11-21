package vn.vuxnye.service;

import org.springframework.security.core.userdetails.UserDetails;
import vn.vuxnye.dto.request.AppointmentRequest;
import vn.vuxnye.dto.response.AppointmentPageResponse;
import vn.vuxnye.dto.response.AppointmentResponse;
import java.util.List;

public interface AppointmentService {

    // Admin/Staff: Lấy tất cả lịch hẹn (phân trang)
    AppointmentPageResponse findAll(int page, int size);

    // User: Lấy lịch hẹn của TÔI
    List<AppointmentResponse> getMyAppointments(UserDetails userDetails);

    // User/Admin: Lấy chi tiết 1 lịch hẹn
    AppointmentResponse findById(Long id);

    // User: Đặt lịch hẹn mới
    AppointmentResponse create(AppointmentRequest request, UserDetails userDetails);

    // Admin/Staff: Cập nhật (gán nhân viên, đổi trạng thái)
    AppointmentResponse update(Long id, AppointmentRequest request);

    // User: Hủy lịch hẹn của mình
    void cancel(Long id, UserDetails userDetails);
}