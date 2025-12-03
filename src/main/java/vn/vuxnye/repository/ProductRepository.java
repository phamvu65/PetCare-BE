package vn.vuxnye.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vuxnye.model.ProductEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    /**
     * TÌM KIẾM & LỌC SẢN PHẨM (FINAL VERSION)
     * - Sử dụng Alias 'c' cho Category để truy vấn chính xác hơn.
     * - Tìm kiếm trong: Tên SP, Tên Danh Mục, hoặc Mô Tả SP.
     */
    @Query(value = "SELECT p.* FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "       p.name LIKE :keyword OR " +
            "       p.description LIKE :keyword OR " +
            "       c.name LIKE :keyword) " +
            "AND (:categoryIds IS NULL OR c.id IN (:categoryIds)) " +
            "AND p.is_deleted = :isDeleted",

            countQuery = "SELECT count(*) FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "WHERE (:keyword IS NULL OR :keyword = '' OR " +
                    "       p.name LIKE :keyword OR " +
                    "       p.description LIKE :keyword OR " +
                    "       c.name LIKE :keyword) " +
                    "AND (:categoryIds IS NULL OR c.id IN (:categoryIds)) " +
                    "AND p.is_deleted = :isDeleted",
            nativeQuery = true)
    Page<ProductEntity> searchProducts(@Param("keyword") String keyword,
                                       @Param("categoryIds") List<Long> categoryIds,
                                       @Param("isDeleted") boolean isDeleted,
                                       Pageable pageable);

    @Query("SELECT p FROM ProductEntity p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.images " +
            "WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithDetails(@Param("id") Long id);

    List<ProductEntity> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM ProductEntity p LEFT JOIN p.category c WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ProductEntity> searchEverything(@Param("keyword") String keyword);
}