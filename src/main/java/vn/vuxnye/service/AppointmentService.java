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

    AppointmentPageResponse findAll(int page, int size, AppointmentStatus status, Long customerId, Long staffId);

    List<AppointmentResponse> getMyAppointments(UserDetails userDetails);

    AppointmentResponse findById(Long id);

    AppointmentResponse create(AppointmentRequest request, UserDetails userDetails);


    AppointmentResponse update(Long id, AppointmentRequest request, UserDetails userDetails);

    // User: Hủy lịch hẹn của mình
    void cancel(Long id, UserDetails userDetails);


    // 1. Thống kê Dịch vụ (Có phân trang & Sort)
    Page<ServiceStatsResponse> getServiceStatsPage(LocalDate fromDate, LocalDate toDate, int page, int size, String sortField, String sortDir);

    // 2. Thống kê Nhân viên (Có phân trang & Sort) - MỚI THÊM
    Page<EmployeeStatsResponse> getEmployeeStatsPage(LocalDate fromDate, LocalDate toDate, int page, int size, String sortField, String sortDir);
}