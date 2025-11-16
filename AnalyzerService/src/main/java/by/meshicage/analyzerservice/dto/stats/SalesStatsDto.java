package by.meshicage.analyzerservice.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesStatsDto {
    private Date date;
    private Long ordersCount;
    private BigDecimal totalRevenue;
    private Long productsSold;
}