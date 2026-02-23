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
import vn.vuxnye.common.ResponseAPI;
import vn.vuxnye.dto.request.AppointmentRequest;
import vn.vuxnye.dto.response.AppointmentPageResponse;
import vn.vuxnye.dto.response.AppointmentResponse;
import vn.vuxnye.dto.response.EmployeeStatsResponse;
import vn.vuxnye.dto.response.ServiceStatsResponse;
import vn.vuxnye.service.AppointmentService;


import java.time.LocalDate;
import java.util.List;

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
    public ResponseAPI getAllAppointments(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long staffId) {

        AppointmentPageResponse response = appointmentService.findAll(page, size, status, customerId, staffId);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Get appointments success")
                .data(response)
                .build();
    }

    /**
     * User: Xem lịch hẹn của tôi
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "User: Get my appointments")
    public ResponseAPI getMyAppointments(@AuthenticationPrincipal UserDetails userDetails) {
        List<AppointmentResponse> list = appointmentService.getMyAppointments(userDetails);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Get my appointments success")
                .data(list)
                .build();
    }

    /**
     * Xem chi tiết 1 lịch hẹn
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get appointment detail")
    public ResponseAPI getAppointmentById(@PathVariable @Min(1) Long id) {
        AppointmentResponse response = appointmentService.findById(id);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Get detail success")
                .data(response)
                .build();
    }

    /**
     * User: Đặt lịch
     */
    @PostMapping("/book")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'STAFF')")
    @Operation(summary = "User: Book appointment")
    public ResponseAPI bookAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        AppointmentResponse response = appointmentService.create(request, userDetails);

        return ResponseAPI.builder()
                .status(HttpStatus.CREATED)
                .message("Booking success")
                .data(response)
                .build();
    }

    /**
     * Admin/Staff: Cập nhật lịch hẹn (Gán staff, đổi trạng thái)
     */
    @PutMapping("/upd/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Admin/Staff: Update appointment")
    public ResponseAPI updateAppointment(
            @PathVariable @Min(1) Long id,
            @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        AppointmentResponse response = appointmentService.update(id, request, userDetails);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Update success")
                .data(response)
                .build();
    }

    /**
     * User: Hủy lịch hẹn của mình
     */
    @PatchMapping("/cancel/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'STAFF')")
    @Operation(summary = "User: Cancel my appointment")
    public ResponseAPI cancelAppointment(
            @PathVariable @Min(1) Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        appointmentService.cancel(id, userDetails);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Cancellation success")
                .data(null)
                .build();
    }


    /**
     * 1. Thống kê DỊCH VỤ (Service Stats)
     */
    @GetMapping("/stats/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Admin/Staff: Get service statistics")
    public ResponseAPI getServiceStatsPage(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "totalRevenue") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<ServiceStatsResponse> stats = appointmentService.getServiceStatsPage(fromDate, toDate, page, size, sortBy, sortDir);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Get service stats success")
                .data(stats) // Trả về cấu trúc phân trang mặc định của Spring
                .build();
    }

    /**
     * 2. Thống kê NHÂN VIÊN (Employee Stats)
     */
    @GetMapping("/stats/staff")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Admin/Staff: Get employee performance statistics")
    public ResponseAPI getEmployeeStatsPage(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "totalRevenue") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<EmployeeStatsResponse> stats = appointmentService.getEmployeeStatsPage(fromDate, toDate, page, size, sortBy, sortDir);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Get employee stats success")
                .data(stats)
                .build();
    }
}