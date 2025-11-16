package by.meshicage.analyzerservice.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMetrics {
    private Long totalUsers;
    private Long activeUsers;
    private Long newUsers;
    private Double averageOrdersPerUser;
    private Double retentionRate;
}