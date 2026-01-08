package vn.vuxnye.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceStatsResponse {
    private Long id;
    private String name;
    private Long usageCount;      // Số lần sử dụng
    private BigDecimal totalRevenue;  // Tổng doanh thu
}