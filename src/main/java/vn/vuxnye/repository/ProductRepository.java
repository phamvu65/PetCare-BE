package vn.vuxnye.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vuxnye.model.ProductEntity;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    /**
     * Tìm kiếm sản phẩm (theo tên hoặc mô tả)
     * JOIN FETCH Category để tối ưu hiệu năng
     */
    @Query(value = "SELECT p FROM ProductEntity p " +
            "LEFT JOIN FETCH p.category " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            countQuery = "SELECT COUNT(p) FROM ProductEntity p WHERE " +
                    "(:keyword IS NULL OR :keyword = '' OR " +
                    "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ProductEntity> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Lấy chi tiết sản phẩm (FETCH cả Category và Images)
     */
    @Query("SELECT p FROM ProductEntity p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.images " +
            "WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithDetails(@Param("id") Long id);
}