package com.example.adminblservice.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingStatsDto {
    private Long totalProducts;
    private Long lowStockProducts;
    private BigDecimal totalInventoryValue;
    private ProductStatsDto bestSellingProduct;
    private ProductStatsDto worstSellingProduct;
    private Double conversionRate;
}