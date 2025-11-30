package vn.vuxnye.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vuxnye.common.UserStatus;
import vn.vuxnye.model.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {

    // 🟢 SỬA: Thêm JOIN với bảng roles và điều kiện lọc r.id
    @Query("SELECT u FROM UserEntity u " +
            "JOIN u.roles r " + // Join bảng roles
            "WHERE (:roleId IS NULL OR r.id = :roleId) " + // Lọc theo Role ID
            "AND (:status IS NULL OR u.status = :status) " +
            "AND (:keyword IS NULL OR " +
            "   lower(u.firstName) like :keyword OR " +
            "   lower(u.lastName) like :keyword OR " +
            "   lower(u.username) like :keyword OR " +
            "   lower(u.phone) like :keyword OR " +
            "   lower(u.email) like :keyword" +
            ")")
    Page<UserEntity> searchByRoleStatusAndKeyword(
            @Param("roleId") Long roleId, // Thêm tham số này
            @Param("status") UserStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    UserEntity findByEmail(String email);

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<UserEntity> findByUsername(String username);
}