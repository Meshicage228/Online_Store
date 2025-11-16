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
public class InventoryMetrics {
    private Long totalProducts;
    private Long lowStockItems;
    private Long outOfStockItems;
    private BigDecimal totalInventoryValue;
    private BigDecimal inventoryTurnover;
}