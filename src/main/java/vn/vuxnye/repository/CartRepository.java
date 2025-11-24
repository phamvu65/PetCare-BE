package vn.vuxnye.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vuxnye.model.CartEntity;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    // Tìm giỏ hàng theo username (Join fetch items để tránh N+1 khi xem giỏ)
    @Query("SELECT c FROM CartEntity c LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.product WHERE c.user.username = :username")
    Optional<CartEntity> findByUsername(@Param("username") String username);
}