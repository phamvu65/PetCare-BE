package vn.vuxnye.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeStatsResponse {
    private Long id;
    private String name;
    private Long completedAppointments;   // Số ca hoàn thành
    private BigDecimal totalRevenueGenerated; // Doanh số mang về
}