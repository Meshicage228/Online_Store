package com.example.adminblservice.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeasonalityStatsDto {
    private Map<String, BigDecimal> monthlyRevenue;
    private Map<String, Long> monthlyOrders;
    private List<String> peakPeriods;
    private List<String> lowPeriods;
}