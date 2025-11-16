package com.example.coursework.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPerformance {
    private String productName;
    private Long unitsSold;
    private BigDecimal revenue;
    private Double growth;
}