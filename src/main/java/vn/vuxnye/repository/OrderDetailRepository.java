package vn.vuxnye.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vuxnye.common.OrderStatus;
import vn.vuxnye.dto.response.ProductStatsResponse;
import vn.vuxnye.model.OrderDetailEntity;

import java.time.Instant;
import java.util.List;

@Repository
public interface  OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {


    @Query("SELECT new vn.vuxnye.dto.response.ProductStatsResponse(" +
            "od.product.id, " +
            "od.product.name, " +
            "c.name, " +
            "od.product.price, " +
            "SUM(od.qty), " +
            "SUM(od.qty * od.unitPrice), " +
            "od.product.stock" +
            ") " +
            "FROM OrderDetailEntity od " +
            "JOIN od.order o " +
            "LEFT JOIN od.product.category c " +
            "WHERE o.status = :status " +
            "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR o.createdAt <= :endDate) " +
            "GROUP BY od.product.id, od.product.name, c.name, od.product.price, od.product.stock " +
            "ORDER BY SUM(od.qty) DESC")
    List<ProductStatsResponse> findTopSellingProducts(
            @Param("status") OrderStatus status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable);
}