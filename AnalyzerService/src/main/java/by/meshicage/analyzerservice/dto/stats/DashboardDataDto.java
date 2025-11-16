package by.meshicage.analyzerservice.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataDto {
    private SalesMetrics salesMetrics;
    private InventoryMetrics inventoryMetrics;
    private UserMetrics userMetrics;
    private List<ChartData> salesChart;
    private List<ChartData> revenueChart;
    private List<CategoryData> categoryDistribution;
    private List<ProductPerformance> topProducts;
    private List<ConversionData> conversionFunnel;
}