package com.example.adminblservice.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EconomicStatsDto {
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private BigDecimal revenueGrowth; // % роста выручки
    private BigDecimal customerLifetimeValue;
    private BigDecimal profitMargin; // Маржа прибыли
    private Map<String, BigDecimal> revenueByCategory; // Выручка по категориям
}