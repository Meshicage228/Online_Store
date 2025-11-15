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
public class ProductStatsDto {
    private Integer productId;
    private String productTitle;
    private Long totalSold;
    private BigDecimal totalRevenue;
    private Double averageRating;
    private Long totalComments;
}