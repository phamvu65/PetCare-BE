package vn.vuxnye.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import vn.vuxnye.common.AppointmentStatus;
import vn.vuxnye.dto.request.AppointmentRequest;
import vn.vuxnye.dto.response.AppointmentPageResponse;
import vn.vuxnye.dto.response.AppointmentResponse;
import vn.vuxnye.dto.response.EmployeeStatsResponse; // 🟢 Import DTO Nhân viên
import vn.vuxnye.dto.response.ServiceStatsResponse;  // 🟢 Import DTO Dịch vụ

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    // Admin/Staff: Lấy tất cả lịch hẹn (phân trang + lọc theo status, customer, staff)
    AppointmentPageResponse findAll(int page, int size, AppointmentStatus status, Long customerId, Long staffId);

    // User: Lấy lịch hẹn của TÔI
    List<AppointmentResponse> getMyAppointments(UserDetails userDetails);

    // User/Admin: Lấy chi tiết 1 lịch hẹn
    AppointmentResponse findById(Long id);

    // User: Đặt lịch hẹn mới
    AppointmentResponse create(AppointmentRequest request, UserDetails userDetails);

    // Admin/Staff: Cập nhật (gán nhân viên, đổi trạng thái, đổi giờ)
    // 🟢 QUAN TRỌNG: Đã thêm tham số UserDetails để tự động gán Staff
    AppointmentResponse update(Long id, AppointmentRequest request, UserDetails userDetails);

    // User: Hủy lịch hẹn của mình
    void cancel(Long id, UserDetails userDetails);

    // =========================================================================
    // 🟢 CÁC HÀM THỐNG KÊ (ANALYTICS)
    // =========================================================================

    // 1. Thống kê Dịch vụ (Có phân trang & Sort)
    Page<ServiceStatsResponse> getServiceStatsPage(LocalDate fromDate, LocalDate toDate, int page, int size, String sortField, String sortDir);

    // 2. Thống kê Nhân viên (Có phân trang & Sort) - MỚI THÊM
    Page<EmployeeStatsResponse> getEmployeeStatsPage(LocalDate fromDate, LocalDate toDate, int page, int size, String sortField, String sortDir);
}