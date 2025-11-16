package by.meshicage.analyzerservice.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDto {
    private Long totalUsers;
    private Long activeUsers;
    private Long newUsersThisMonth;
    private Double averageOrdersPerUser;
}