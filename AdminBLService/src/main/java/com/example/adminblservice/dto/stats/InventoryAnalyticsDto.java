package com.example.adminblservice.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAnalyticsDto {
    private Long totalSKU;
    private Long outOfStockItems;
    private Long slowMovingItems;
    private BigDecimal inventoryTurnover;
    private List<ProductPerformanceDto> topPerformingProducts;
    private List<ProductPerformanceDto> underperformingProducts;
}