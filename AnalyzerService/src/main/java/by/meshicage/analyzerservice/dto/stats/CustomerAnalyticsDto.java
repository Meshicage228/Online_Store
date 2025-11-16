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
public class CustomerAnalyticsDto {
    private Long totalCustomers;
    private Long repeatCustomers;
    private Double retentionRate;
    private Double churnRate;
    private List<CustomerSegmentDto> customerSegments;
}