package vn.vuxnye.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.vuxnye.model.ServiceEntity;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    /**
     * Search services by name or description AND active status
     */
    @Query("SELECT s FROM ServiceEntity s WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:activeStatus IS NULL OR s.active = :activeStatus)")
    Page<ServiceEntity> searchServices(
            @Param("keyword") String keyword,
            @Param("activeStatus") Boolean activeStatus,
            Pageable pageable
    );

}