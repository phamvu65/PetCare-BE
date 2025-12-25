package vn.vuxnye.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vuxnye.common.AppointmentStatus;
import vn.vuxnye.model.AppointmentEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    /**
     * Lấy tất cả lịch hẹn (phân trang)
     * Viết full query để tránh lỗi cú pháp nối chuỗi
     */
    @Override
    @Query(value = "SELECT a FROM AppointmentEntity a " +
            "JOIN FETCH a.customer " +
            "JOIN FETCH a.pet " +
            "JOIN FETCH a.service " +
            "LEFT JOIN FETCH a.staff",
            countQuery = "SELECT COUNT(a) FROM AppointmentEntity a")
    Page<AppointmentEntity> findAll(Pageable pageable);

    /**
     * Lấy chi tiết 1 lịch hẹn theo ID (có nạp đủ thông tin liên quan)
     */
    @Query("SELECT a FROM AppointmentEntity a " +
            "JOIN FETCH a.customer " +
            "JOIN FETCH a.pet " +
            "JOIN FETCH a.service " +
            "LEFT JOIN FETCH a.staff " +
            "WHERE a.id = :id")
    Optional<AppointmentEntity> findByIdWithDetails(@Param("id") Long id);

    /**
     * Lấy lịch hẹn của một khách hàng (theo username)
     */
    @Query("SELECT a FROM AppointmentEntity a " +
            "JOIN FETCH a.customer " +
            "JOIN FETCH a.pet " +
            "JOIN FETCH a.service " +
            "LEFT JOIN FETCH a.staff " +
            "WHERE a.customer.username = :username")
    List<AppointmentEntity> findByCustomerUsername(@Param("username") String username);

    @Query(value = "SELECT a FROM AppointmentEntity a " +
            "JOIN FETCH a.customer " +
            "JOIN FETCH a.pet " +
            "JOIN FETCH a.service " +
            "LEFT JOIN FETCH a.staff " +
            "WHERE a.status = :status",
            countQuery = "SELECT COUNT(a) FROM AppointmentEntity a WHERE a.status = :status")
    Page<AppointmentEntity> findByStatus(@Param("status") AppointmentStatus status, Pageable pageable);

    @Query("SELECT a FROM AppointmentEntity a WHERE " +
            "(:customerId IS NULL OR a.customer.id = :customerId) AND " +
            "(:staffId IS NULL OR a.staff.id = :staffId) AND " +
            "(:status IS NULL OR a.status = :status)")
    Page<AppointmentEntity> findAllByFilter(
            @Param("customerId") Long customerId,
            @Param("staffId") Long staffId,            // <--- Vị trí số 2
            @Param("status") AppointmentStatus status, // <--- Vị trí số 3
            Pageable pageable);


    // ... (Các method cũ giữ nguyên)

    // --- THỐNG KÊ DỊCH VỤ ---

    @Query("SELECT COUNT(a) FROM AppointmentEntity a " +
            "WHERE a.status = :status " +
            "AND (:startDate IS NULL OR a.scheduledAt >= :startDate) " +
            "AND (:endDate IS NULL OR a.scheduledAt <= :endDate)")
    Long countByStatusAndDate(@Param("status") AppointmentStatus status,
                              @Param("startDate") java.time.LocalDateTime startDate,
                              @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT COUNT(a) FROM AppointmentEntity a " +
            "WHERE (:startDate IS NULL OR a.scheduledAt >= :startDate) " +
            "AND (:endDate IS NULL OR a.scheduledAt <= :endDate)")
    Long countTotalByDate(@Param("startDate") java.time.LocalDateTime startDate,
                          @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT SUM(a.service.price) FROM AppointmentEntity a " +
            "WHERE a.status = :status " +
            "AND (:startDate IS NULL OR a.scheduledAt >= :startDate) " +
            "AND (:endDate IS NULL OR a.scheduledAt <= :endDate)")
    java.math.BigDecimal countRevenue(@Param("status") AppointmentStatus status,
                                      @Param("startDate") java.time.LocalDateTime startDate,
                                      @Param("endDate") java.time.LocalDateTime endDate);

    // Lấy danh sách lịch hẹn hoàn thành để vẽ biểu đồ
    @Query("SELECT a FROM AppointmentEntity a " +
            "JOIN FETCH a.service " + // Fetch service để lấy giá tiền
            "WHERE a.status = :status " +
            "AND (:startDate IS NULL OR a.scheduledAt >= :startDate) " +
            "AND (:endDate IS NULL OR a.scheduledAt <= :endDate) " +
            "ORDER BY a.scheduledAt ASC")
    List<AppointmentEntity> findCompletedAppointmentsBetween(@Param("status") AppointmentStatus status,
                                                             @Param("startDate") java.time.LocalDateTime startDate,
                                                             @Param("endDate") java.time.LocalDateTime endDate);
}
