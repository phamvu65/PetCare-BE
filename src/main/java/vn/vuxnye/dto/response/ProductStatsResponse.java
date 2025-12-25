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
    private BigDecimal price;
    private Long totalSold; // Tổng số lượng đã bán
}