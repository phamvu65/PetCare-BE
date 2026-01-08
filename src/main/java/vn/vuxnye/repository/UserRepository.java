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

    // API Search cũ của bạn (Giữ nguyên)
    @Query("SELECT u FROM UserEntity u " +
            "JOIN u.roles r " +
            "WHERE (:roleId IS NULL OR r.id = :roleId) " +
            "AND (:status IS NULL OR u.status = :status) " +
            "AND (:keyword IS NULL OR " +
            "   lower(u.firstName) like :keyword OR " +
            "   lower(u.lastName) like :keyword OR " +
            "   lower(u.username) like :keyword OR " +
            "   lower(u.phone) like :keyword OR " +
            "   lower(u.email) like :keyword" +
            ")")
    Page<UserEntity> searchByRoleStatusAndKeyword(
            @Param("roleId") Long roleId,
            @Param("status") UserStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // Tìm user bằng email (Dùng cho quên mật khẩu - Giữ nguyên)
    UserEntity findByEmail(String email);

    // Tìm user bằng username (Giữ nguyên nếu có chỗ khác dùng)
    @Query("SELECT u FROM UserEntity u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<UserEntity> findByUsername(String username);

    // 🟢 [MỚI] TÌM BẰNG USERNAME HOẶC EMAIL (Dùng cho Đăng nhập)
    // Spring Data JPA sẽ tự hiểu: (username = ?1 OR email = ?2)
    @Query("SELECT u FROM UserEntity u JOIN FETCH u.roles WHERE u.username = :username OR u.email = :email")
    Optional<UserEntity> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
}