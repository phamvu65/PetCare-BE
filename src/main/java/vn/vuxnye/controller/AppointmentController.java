package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.common.AppointmentStatus;
import vn.vuxnye.dto.request.AppointmentRequest;
import vn.vuxnye.dto.response.AppointmentPageResponse;
import vn.vuxnye.dto.response.AppointmentResponse;
import vn.vuxnye.dto.response.EmployeeStatsResponse; // 🟢 Import DTO Nhân viên
import vn.vuxnye.dto.response.ServiceStatsResponse;  // 🟢 Import DTO Dịch vụ
import vn.vuxnye.service.AppointmentService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
@Tag(name = "Appointment Controller")
@RequiredArgsConstructor
@Slf4j(topic = "APPOINTMENT-CONTROLLER")
@Validated
@PreAuthorize("isAuthenticated()")
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Admin/Staff: Xem tất cả lịch hẹn (Có bộ lọc)
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Admin/Staff: Get appointments with filter")
    public Map<String, Object> getAllAppointments(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long staffId) {

        AppointmentPageResponse response = appointmentService.findAll(page, size, status, customerId, staffId);

        return createResponse(HttpStatus.OK, "Get appointments success", response);
    }

    /**
     * User: Xem lịch hẹn của tôi
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "User: Get my appointments")
    public Map<String, Object> getMyAppointments(@AuthenticationPrincipal UserDetails userDetails) {

        List<AppointmentResponse> list = appointmentService.getMyAppointments(userDetails);
        return createResponse(HttpStatus.OK, "Get my appointments success", list);
    }

    /**
     * Xem chi tiết 1 lịch hẹn
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get appointment detail")
    public Map<String, Object> getAppointmentById(@PathVariable @Min(1) Long id) {
        AppointmentResponse response = appointmentService.findById(id);
        return createResponse(HttpStatus.OK, "Get detail success", response);
    }

    /**
     * User: Đặt lịch
     */
    @PostMapping("/book")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'STAFF')")
    @Operation(summary = "User: Book appointment")
    public Map<String, Object> bookAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        AppointmentResponse response = appointmentService.create(request, userDetails);
        return createResponse(HttpStatus.CREATED, "Booking success", response);
    }

    /**
     * Admin/Staff: Cập nhật lịch hẹn (Gán staff, đổi trạng thái)
     * 🟢 ĐÃ SỬA: Thêm UserDetails để tự động gán Staff
     */
    @PutMapping("/upd/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Admin/Staff: Update appointment")
    public Map<String, Object> updateAppointment(
            @PathVariable @Min(1) Long id,
            @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) { // <--- Thêm cái này

        // Truyền userDetails xuống Service
        AppointmentResponse response = appointmentService.update(id, request, userDetails);
        return createResponse(HttpStatus.OK, "Update success", response);
    }

    /**
     * User: Hủy lịch hẹn của mình
     */
    @PatchMapping("/cancel/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'STAFF')")
    @Operation(summary = "User: Cancel my appointment")
    public Map<String, Object> cancelAppointment(
            @PathVariable @Min(1) Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        appointmentService.cancel(id, userDetails);
        return createResponse(HttpStatus.OK, "Cancellation success", null);
    }

    // =========================================================================
    // 🟢 CỤM API THỐNG KÊ (ANALYTICS)
    // =========================================================================

    /**
     * 1. Thống kê DỊCH VỤ (Service Stats)
     */
    @GetMapping("/stats/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Admin/Staff: Get service statistics")
    public Map<String, Object> getServiceStatsPage(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "totalRevenue") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<ServiceStatsResponse> stats = appointmentService.getServiceStatsPage(fromDate, toDate, page, size, sortBy, sortDir);
        return createResponse(HttpStatus.OK, "Get service stats success", stats);
    }

    /**
     * 2. Thống kê NHÂN VIÊN (Employee Stats) - MỚI THÊM
     */
    @GetMapping("/stats/staff")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Admin/Staff: Get employee performance statistics")
    public Map<String, Object> getEmployeeStatsPage(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "totalRevenue") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<EmployeeStatsResponse> stats = appointmentService.getEmployeeStatsPage(fromDate, toDate, page, size, sortBy, sortDir);
        return createResponse(HttpStatus.OK, "Get employee stats success", stats);
    }

    // --- Helper Method ---
    private Map<String, Object> createResponse(HttpStatus status, String message, Object data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", status.value());
        result.put("message", message);
        result.put("data", data);
        return result;
    }
}