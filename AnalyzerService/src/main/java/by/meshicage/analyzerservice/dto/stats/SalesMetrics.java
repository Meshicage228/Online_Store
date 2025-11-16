package by.meshicage.analyzerservice.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesMetrics {
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private Long totalOrders;
    private Long productsSold;
    private BigDecimal revenueGrowth;
    private BigDecimal orderGrowth;
}