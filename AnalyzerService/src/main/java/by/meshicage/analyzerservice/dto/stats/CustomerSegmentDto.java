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
public class CustomerSegmentDto {
    private String segmentName;
    private Long customerCount;
    private BigDecimal averageSpend;
    private Integer averageOrders;
    private String description;
}