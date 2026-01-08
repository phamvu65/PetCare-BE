package vn.vuxnye.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.vuxnye.common.AppointmentStatus;
import vn.vuxnye.dto.request.AppointmentRequest;
import vn.vuxnye.dto.response.AppointmentPageResponse;
import vn.vuxnye.dto.response.AppointmentResponse;
import vn.vuxnye.dto.response.EmployeeStatsResponse;
import vn.vuxnye.dto.response.ServiceStatsResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.*;
import vn.vuxnye.repository.AppointmentRepository;
import vn.vuxnye.repository.PetRepository;
import vn.vuxnye.repository.ServiceRepository;
import vn.vuxnye.repository.UserRepository;
import vn.vuxnye.service.AppointmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "APPOINTMENT-IMPL")
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final ServiceRepository serviceRepository;

    @Override
    @Transactional(readOnly = true)
    public AppointmentPageResponse findAll(int page, int size, AppointmentStatus status, Long customerId, Long staffId) {
        log.info("Find appointments. Status: {}, Customer: {}, Staff: {}", status, customerId, staffId);
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size, Sort.by("scheduledAt").descending());
        Page<AppointmentEntity> pageResult = appointmentRepository.findAllByFilter(customerId, staffId, status, pageable);
        List<AppointmentResponse> list = pageResult.stream().map(AppointmentResponse::fromEntity).toList();
        AppointmentPageResponse response = new AppointmentPageResponse();
        response.setAppointments(list);
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getMyAppointments(UserDetails userDetails) {
        log.info("User {} getting their appointments", userDetails.getUsername());
        List<AppointmentEntity> appointments = appointmentRepository.findByCustomerUsername(userDetails.getUsername());
        return appointments.stream().map(AppointmentResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse findById(Long id) {
        AppointmentEntity entity = appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        return AppointmentResponse.fromEntity(entity);
    }

    @Override
    public AppointmentResponse create(AppointmentRequest request, UserDetails userDetails) {
        log.info("Creating new appointment by user: {}", userDetails.getUsername());
        UserEntity customer;
        if (isAdminOrStaff(userDetails)) {
            if (request.getCustomerId() == null) {
                throw new RuntimeException("Admin/Staff booking must provide customerId");
            }
            customer = userRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
        } else {
            customer = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }
        PetEntity pet = petRepository.findByIdAndOwnerId(request.getPetId(), customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found or does not belong to customer"));
        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        AppointmentEntity newAppt = new AppointmentEntity();
        newAppt.setCustomer(customer);
        newAppt.setPet(pet);
        newAppt.setService(service);
        newAppt.setScheduledAt(request.getScheduledAt());
        newAppt.setNote(request.getNote());
        newAppt.setStatus(AppointmentStatus.BOOKED);
        if (request.getStaffId() != null) {
            UserEntity staff = userRepository.findById(request.getStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
            newAppt.setStaff(staff);
        }
        AppointmentEntity saved = appointmentRepository.save(newAppt);
        return AppointmentResponse.fromEntity(saved);
    }

    // =========================================================================
    // HÀM UPDATE (TỰ ĐỘNG GÁN STAFF)
    // =========================================================================
    @Override
    public AppointmentResponse update(Long id, AppointmentRequest request, UserDetails userDetails) {
        log.info("Updating appointment id: {} by user: {}", id, userDetails.getUsername());

        AppointmentEntity appt = appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        UserEntity currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        // 1. Nếu request có gửi ID cụ thể (Admin chỉ định)
        if (request.getStaffId() != null) {
            UserEntity staff = userRepository.findById(request.getStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
            appt.setStaff(staff);
        }
        // 2. Nếu không gửi ID, nhưng người làm là STAFF -> Tự động nhận việc
        else if (isStaffRole(userDetails)) { // 🟢 Đã có hàm này ở cuối file
            if (appt.getStaff() == null) {
                appt.setStaff(currentUser);
            }
        }

        if (request.getStatus() != null) {
            appt.setStatus(request.getStatus());
            // Fix thêm: Nếu DONE mà chưa có staff -> Gán luôn
            if (request.getStatus() == AppointmentStatus.DONE && appt.getStaff() == null && isStaffRole(userDetails)) {
                appt.setStaff(currentUser);
            }
        }

        if (request.getScheduledAt() != null) appt.setScheduledAt(request.getScheduledAt());
        if (request.getNote() != null) appt.setNote(request.getNote());

        AppointmentEntity updated = appointmentRepository.save(appt);
        return AppointmentResponse.fromEntity(updated);
    }

    @Override
    public void cancel(Long id, UserDetails userDetails) {
        log.info("Request cancel appointment {} by {}", id, userDetails.getUsername());
        AppointmentEntity appt = appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if (!isAdminOrStaff(userDetails)) {
            if (!appt.getCustomer().getUsername().equals(userDetails.getUsername())) {
                throw new RuntimeException("You do not have permission to cancel this appointment");
            }
        }
        if (appt.getStatus() != AppointmentStatus.BOOKED) {
            throw new RuntimeException("Cannot cancel appointment in status: " + appt.getStatus());
        }
        appt.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appt);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceStatsResponse> getServiceStatsPage(LocalDate fromDate, LocalDate toDate, int page, int size, String sortField, String sortDir) {
        LocalDateTime start = (fromDate != null) ? fromDate.atStartOfDay() : null;
        LocalDateTime end = (toDate != null) ? toDate.plusDays(1).atStartOfDay() : null;
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort;
        if ("totalRevenue".equalsIgnoreCase(sortField)) sort = JpaSort.unsafe(direction, "SUM(s.price)");
        else if ("usageCount".equalsIgnoreCase(sortField)) sort = JpaSort.unsafe(direction, "COUNT(a)");
        else if ("name".equalsIgnoreCase(sortField)) sort = Sort.by(direction, "s.name");
        else sort = JpaSort.unsafe(direction, "SUM(s.price)");
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size, sort);
        return appointmentRepository.getServiceStatsPage(AppointmentStatus.DONE, start, end, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeStatsResponse> getEmployeeStatsPage(LocalDate fromDate, LocalDate toDate, int page, int size, String sortField, String sortDir) {
        LocalDateTime start = (fromDate != null) ? fromDate.atStartOfDay() : null;
        LocalDateTime end = (toDate != null) ? toDate.plusDays(1).atStartOfDay() : null;
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort;
        if ("totalRevenue".equalsIgnoreCase(sortField)) sort = JpaSort.unsafe(direction, "SUM(s.price)");
        else if ("usageCount".equalsIgnoreCase(sortField) || "completedAppointments".equalsIgnoreCase(sortField)) sort = JpaSort.unsafe(direction, "COUNT(a)");
        else if ("name".equalsIgnoreCase(sortField)) sort = Sort.by(direction, "u.firstName");
        else sort = JpaSort.unsafe(direction, "SUM(s.price)");
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size, sort);
        return appointmentRepository.getEmployeeStatsPage(AppointmentStatus.DONE, start, end, pageable);
    }

    // --- HELPER METHODS ---

    private boolean isAdminOrStaff(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") ||
                        role.getAuthority().equals("ROLE_STAFF"));
    }

    // 🟢 ĐÂY LÀ HÀM BẠN ĐANG THIẾU
    private boolean isStaffRole(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_STAFF"));
    }
}