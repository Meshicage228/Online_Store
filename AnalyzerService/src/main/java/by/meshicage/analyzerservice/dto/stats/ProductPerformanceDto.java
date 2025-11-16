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
public class ProductPerformanceDto {
    private String productName;
    private Long unitsSold;
    private BigDecimal revenue;
    private Double sellThroughRate;
    private Integer daysOfSupply;
}