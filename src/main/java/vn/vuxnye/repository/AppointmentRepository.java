package vn.vuxnye.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vuxnye.common.AppointmentStatus;
import vn.vuxnye.dto.response.EmployeeStatsResponse;
import vn.vuxnye.dto.response.ServiceStatsResponse;
import vn.vuxnye.model.AppointmentEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    @Override
    @Query(value = "SELECT a FROM AppointmentEntity a " +
            "JOIN FETCH a.customer " +
            "JOIN FETCH a.pet " +
            "JOIN FETCH a.service " +
            "LEFT JOIN FETCH a.staff",
            countQuery = "SELECT COUNT(a) FROM AppointmentEntity a")
    Page<AppointmentEntity> findAll(Pageable pageable);

    @Query("SELECT a FROM AppointmentEntity a " +
            "JOIN FETCH a.customer " +
            "JOIN FETCH a.pet " +
            "JOIN FETCH a.service " +
            "LEFT JOIN FETCH a.staff " +
            "WHERE a.id = :id")
    Optional<AppointmentEntity> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT a FROM AppointmentEntity a " +
            "JOIN FETCH a.customer " +
            "JOIN FETCH a.pet " +
            "JOIN FETCH a.service " +
            "LEFT JOIN FETCH a.staff " +
            "WHERE a.customer.username = :username")
    List<AppointmentEntity> findByCustomerUsername(@Param("username") String username);


    @Query("SELECT a FROM AppointmentEntity a WHERE " +
            "(:customerId IS NULL OR a.customer.id = :customerId) AND " +
            "(:staffId IS NULL OR a.staff.id = :staffId) AND " +
            "(:status IS NULL OR a.status = :status)")
    Page<AppointmentEntity> findAllByFilter(
            @Param("customerId") Long customerId,
            @Param("staffId") Long staffId,
            @Param("status") AppointmentStatus status,
            Pageable pageable);

    // --- CÁC HÀM THỐNG KÊ CŨ (Dùng cho Dashboard tổng quan) ---

    @Query("SELECT COUNT(a) FROM AppointmentEntity a " +
            "WHERE a.status = :status " +
            "AND (:startDate IS NULL OR a.scheduledAt >= :startDate) " +
            "AND (:endDate IS NULL OR a.scheduledAt <= :endDate)")
    Long countByStatusAndDate(@Param("status") AppointmentStatus status,
                              @Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(a) FROM AppointmentEntity a " +
            "WHERE (:startDate IS NULL OR a.scheduledAt >= :startDate) " +
            "AND (:endDate IS NULL OR a.scheduledAt <= :endDate)")
    Long countTotalByDate(@Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(a.service.price) FROM AppointmentEntity a " +
            "WHERE a.status = :status " +
            "AND (:startDate IS NULL OR a.scheduledAt >= :startDate) " +
            "AND (:endDate IS NULL OR a.scheduledAt <= :endDate)")
    java.math.BigDecimal countRevenue(@Param("status") AppointmentStatus status,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AppointmentEntity a " +
            "JOIN FETCH a.service " +
            "WHERE a.status = :status " +
            "AND (:startDate IS NULL OR a.scheduledAt >= :startDate) " +
            "AND (:endDate IS NULL OR a.scheduledAt <= :endDate) " +
            "ORDER BY a.scheduledAt ASC")
    List<AppointmentEntity> findCompletedAppointmentsBetween(@Param("status") AppointmentStatus status,
                                                             @Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);

    // ==========================================================
    // 🟢 CÁC HÀM THỐNG KÊ MỚI (Dùng cho trang chi tiết)
    // ==========================================================

    // 1. Top Dịch vụ (Trả về List - Dùng cho Dashboard nhỏ)
    @Query("SELECT new vn.vuxnye.dto.response.ServiceStatsResponse(s.id, s.name, COUNT(a), SUM(s.price)) " +
            "FROM AppointmentEntity a " +
            "JOIN a.service s " +
            "WHERE a.status = :status " +
            "AND (:startDate IS NULL OR a.scheduledAt >= :startDate) " +
            "AND (:endDate IS NULL OR a.scheduledAt <= :endDate) " +
            "GROUP BY s.id, s.name " +
            "ORDER BY SUM(s.price) DESC")
    List<ServiceStatsResponse> findTopServices(@Param("status") AppointmentStatus status,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               Pageable pageable);

    // 2. Top Nhân viên (Trả về List - Dùng cho Dashboard nhỏ)
    @Query("SELECT new vn.vuxnye.dto.response.EmployeeStatsResponse(u.id, CONCAT(u.lastName, ' ', u.firstName), COUNT(a), SUM(s.price)) " +
            "FROM AppointmentEntity a " +
            "JOIN a.staff u " +
            "JOIN a.service s " +
            "WHERE a.status = :status " +
            "AND (:startDate IS NULL OR a.scheduledAt >= :startDate) " +
            "AND (:endDate IS NULL OR a.scheduledAt <= :endDate) " +
            "GROUP BY u.id, u.lastName, u.firstName " +
            "ORDER BY SUM(s.price) DESC")
    List<EmployeeStatsResponse> findTopEmployees(@Param("status") AppointmentStatus status,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate,
                                                 Pageable pageable);

    // 3. Thống kê Dịch vụ (Trả về Page - Dùng cho trang chi tiết có phân trang)
    @Query("SELECT new vn.vuxnye.dto.response.ServiceStatsResponse(s.id, s.name, COUNT(a), SUM(s.price)) " +
            "FROM AppointmentEntity a " +
            "JOIN a.service s " +
            "WHERE a.status = :status " +
            "AND (:startDate IS NULL OR a.scheduledAt >= :startDate) " +
            "AND (:endDate IS NULL OR a.scheduledAt <= :endDate) " +
            "GROUP BY s.id, s.name")
    Page<ServiceStatsResponse> getServiceStatsPage(
            @Param("status") AppointmentStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 4. Thống kê Nhân viên (Trả về Page - Dùng cho trang chi tiết có phân trang) 🟢 MỚI THÊM
    @Query("SELECT new vn.vuxnye.dto.response.EmployeeStatsResponse(u.id, CONCAT(u.lastName, ' ', u.firstName), COUNT(a), SUM(s.price)) " +
            "FROM AppointmentEntity a " +
            "JOIN a.staff u " +
            "JOIN a.service s " +
            "WHERE a.status = :status " +
            "AND (:startDate IS NULL OR a.scheduledAt >= :startDate) " +
            "AND (:endDate IS NULL OR a.scheduledAt <= :endDate) " +
            "GROUP BY u.id, u.lastName, u.firstName")
    Page<EmployeeStatsResponse> getEmployeeStatsPage(
            @Param("status") AppointmentStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}