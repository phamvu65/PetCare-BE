package vn.vuxnye.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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
}