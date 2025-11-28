package vn.vuxnye.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.vuxnye.common.AppointmentStatus;
import vn.vuxnye.dto.request.AppointmentRequest;
import vn.vuxnye.dto.response.AppointmentPageResponse;
import vn.vuxnye.dto.response.AppointmentResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.*;
import vn.vuxnye.repository.AppointmentRepository;
import vn.vuxnye.repository.PetRepository;
import vn.vuxnye.repository.ServiceRepository;
import vn.vuxnye.repository.UserRepository;
import vn.vuxnye.service.AppointmentService;

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

    /**
     * Admin/Staff: Lấy tất cả lịch hẹn (có phân trang)
     */
    @Override
    @Transactional(readOnly = true)
    public AppointmentPageResponse findAll(int page, int size, AppointmentStatus status) {
        log.info("Admin finding appointments with status: {}", status);

        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size, Sort.by("scheduledAt").descending());
        Page<AppointmentEntity> pageResult;

        if (status != null) {
            // Nếu có status -> Gọi hàm lọc
            pageResult = appointmentRepository.findByStatus(status, pageable);
        } else {
            // Nếu không -> Lấy tất cả như cũ
            pageResult = appointmentRepository.findAll(pageable);
        }

        List<AppointmentResponse> list = pageResult.stream()
                .map(AppointmentResponse::fromEntity)
                .toList();

        AppointmentPageResponse response = new AppointmentPageResponse();
        response.setAppointments(list);
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());
        return response;
    }

    /**
     * User: Lấy danh sách lịch hẹn của chính mình
     */
    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getMyAppointments(UserDetails userDetails) {
        log.info("User {} getting their appointments", userDetails.getUsername());

        List<AppointmentEntity> appointments = appointmentRepository.findByCustomerUsername(userDetails.getUsername());

        return appointments.stream()
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết 1 lịch hẹn
     */
    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse findById(Long id) {
        AppointmentEntity entity = appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        return AppointmentResponse.fromEntity(entity);
    }

    /**
     * Tạo lịch hẹn mới (Xử lý cả User tự đặt và Admin đặt hộ)
     */
    @Override
    public AppointmentResponse create(AppointmentRequest request, UserDetails userDetails) {
        log.info("Creating new appointment by user: {}", userDetails.getUsername());

        UserEntity customer;

        // --- LOGIC QUAN TRỌNG: XÁC ĐỊNH KHÁCH HÀNG ---
        if (isAdminOrStaff(userDetails)) {
            // Trường hợp 1: Admin/Staff đặt hộ -> Phải gửi customerId trong request
            if (request.getCustomerId() == null) {
                throw new RuntimeException("Admin/Staff booking must provide customerId");
            }
            customer = userRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
        } else {
            // Trường hợp 2: Khách hàng tự đặt (lấy ID từ token)
            customer = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }

        // 2. Kiểm tra Pet (Pet phải thuộc về Customer đã xác định ở trên)
        PetEntity pet = petRepository.findByIdAndOwnerId(request.getPetId(), customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found or does not belong to customer"));

        // 3. Lấy Service
        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        // 4. Tạo Entity
        AppointmentEntity newAppt = new AppointmentEntity();
        newAppt.setCustomer(customer);
        newAppt.setPet(pet);
        newAppt.setService(service);
        newAppt.setScheduledAt(request.getScheduledAt());
        newAppt.setNote(request.getNote());
        newAppt.setStatus(AppointmentStatus.BOOKED); // Mặc định mới tạo là BOOKED

        // Nếu Admin gửi kèm Staff ID lúc đặt (gán luôn nhân viên ngay từ đầu)
        if (request.getStaffId() != null) {
            UserEntity staff = userRepository.findById(request.getStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
            newAppt.setStaff(staff);
        }

        AppointmentEntity saved = appointmentRepository.save(newAppt);
        return AppointmentResponse.fromEntity(saved);
    }

    /**
     * Cập nhật lịch hẹn (Gán staff, đổi giờ, đổi trạng thái)
     */
    @Override
    public AppointmentResponse update(Long id, AppointmentRequest request) {
        log.info("Updating appointment id: {}", id);

        AppointmentEntity appt = appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Cập nhật nhân viên phụ trách
        if (request.getStaffId() != null) {
            UserEntity staff = userRepository.findById(request.getStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
            appt.setStaff(staff);
        }

        // Cập nhật trạng thái (Confirmed, Done, Cancelled...)
        if (request.getStatus() != null) {
            appt.setStatus(request.getStatus());
        }

        // Cập nhật giờ hẹn (nếu khách đổi ý)
        if (request.getScheduledAt() != null) {
            appt.setScheduledAt(request.getScheduledAt());
        }

        // Cập nhật ghi chú
        if (request.getNote() != null) {
            appt.setNote(request.getNote());
        }

        AppointmentEntity updated = appointmentRepository.save(appt);
        return AppointmentResponse.fromEntity(updated);
    }

    /**
     * Hủy lịch hẹn
     */
    @Override
    public void cancel(Long id, UserDetails userDetails) {
        log.info("Request cancel appointment {} by {}", id, userDetails.getUsername());

        AppointmentEntity appt = appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // --- LOGIC QUYỀN HẠN KHI HỦY ---
        if (!isAdminOrStaff(userDetails)) {
            // Nếu là Customer -> Chỉ được hủy đơn CỦA MÌNH
            if (!appt.getCustomer().getUsername().equals(userDetails.getUsername())) {
                throw new RuntimeException("You do not have permission to cancel this appointment");
            }
        }
        // (Nếu là Admin/Staff -> Được phép hủy bất kỳ đơn nào, bỏ qua check quyền sở hữu)

        // Chỉ cho phép hủy nếu trạng thái là BOOKED (chưa thực hiện)
        if (appt.getStatus() != AppointmentStatus.BOOKED) {
            throw new RuntimeException("Cannot cancel appointment in status: " + appt.getStatus());
        }

        appt.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appt);
    }

    // --- Helper Method để kiểm tra role ---
    private boolean isAdminOrStaff(UserDetails userDetails) {
        // Lưu ý: Cần khớp với prefix ROLE_ trong UserServiceDetail của bạn
        return userDetails.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") ||
                        role.getAuthority().equals("ROLE_STAFF"));
    }
}