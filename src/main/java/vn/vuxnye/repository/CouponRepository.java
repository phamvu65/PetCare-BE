package vn.vuxnye.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vuxnye.model.CouponEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    Optional<CouponEntity> findByCode(String code);

    @Query("SELECT c FROM CouponEntity c WHERE c.active = true " +
            "AND (c.startsAt IS NULL OR c.startsAt <= :now) " +
            "AND (c.endsAt IS NULL OR c.endsAt >= :now) " +
            "AND (c.usageLimit IS NULL OR c.usageLimit > 0)")
    List<CouponEntity> findValidCoupons(LocalDateTime now);

    boolean existsByCode(String code);
}