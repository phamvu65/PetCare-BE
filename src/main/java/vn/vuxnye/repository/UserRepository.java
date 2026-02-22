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

    @Query("SELECT u FROM UserEntity u " +
            "LEFT JOIN u.roles r " +
            "WHERE (:roleId IS NULL OR r.id = :roleId) " +
            "AND (:status IS NULL OR u.status = :status) " +
            "AND (:keyword IS NULL OR " +
            "   lower(u.firstName) LIKE concat('%', lower(:keyword), '%') OR " +
            "   lower(u.lastName) LIKE concat('%', lower(:keyword), '%') OR " +
            "   lower(u.username) LIKE concat('%', lower(:keyword), '%') OR " +
            "   lower(u.phone) LIKE concat('%', lower(:keyword), '%') OR " +
            "   lower(u.email) LIKE concat('%', lower(:keyword), '%')" +
            ")")
    Page<UserEntity> searchByRoleStatusAndKeyword(
            @Param("roleId") Long roleId,
            @Param("status") UserStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    UserEntity findByEmail(String email);

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.roles WHERE u.username = :username OR u.email = :email")
    Optional<UserEntity> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
}