package vn.vuxnye.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductStatsResponse {
    private Long id;
    private String name;
    private String categoryName;     // 🟢 MỚI: Tên danh mục
    private BigDecimal price;
    private Long totalSold;
    private BigDecimal totalRevenue; // 🟢 MỚI: Tổng doanh thu
    private Integer stock;
}