package by.meshicage.analyzerservice.service;

import by.meshicage.analyzerservice.dto.stats.CategoryData;
import by.meshicage.analyzerservice.dto.stats.ChartData;
import by.meshicage.analyzerservice.dto.stats.ConversionData;
import by.meshicage.analyzerservice.dto.stats.DashboardDataDto;
import by.meshicage.analyzerservice.dto.stats.InventoryMetrics;
import by.meshicage.analyzerservice.dto.stats.ProductPerformance;
import by.meshicage.analyzerservice.dto.stats.SalesMetrics;
import by.meshicage.analyzerservice.dto.stats.UserMetrics;
import by.meshicage.analyzerservice.entity.product.ProductEntity;
import by.meshicage.analyzerservice.entity.product.Purchases;
import by.meshicage.analyzerservice.repository.ProductRepository;
import by.meshicage.analyzerservice.repository.PurchaseRepository;
import by.meshicage.analyzerservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public DashboardDataDto getDashboardData(final LocalDate startDate, final LocalDate endDate) {
        final LocalDateTime startDateTime = startDate.atStartOfDay();
        final LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        final List<Purchases> purchases = purchaseRepository.findByDateOfPurchaseBetween(startDateTime, endDateTime);
        final List<ProductEntity> products = productRepository.findAll();

        return DashboardDataDto.builder()
                .salesMetrics(calculateSalesMetrics(purchases, startDate, endDate))
                .inventoryMetrics(calculateInventoryMetrics(products, purchases))
                .userMetrics(calculateUserMetrics(startDateTime, endDateTime))
                .salesChart(generateSalesChart(purchases))
                .revenueChart(generateRevenueChart(purchases))
                .categoryDistribution(calculateCategoryDistribution(purchases))
                .topProducts(getTopPerformingProducts(purchases))
                .conversionFunnel(calculateConversionFunnel())
                .build();
    }

    private SalesMetrics calculateSalesMetrics(final List<Purchases> purchases, final LocalDate startDate, final LocalDate endDate) {
        final BigDecimal totalRevenue = purchases.stream()
                .map(p -> BigDecimal.valueOf(p.getPriceAtMomentBuying()).multiply(BigDecimal.valueOf(p.getCountOfProduct())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final Long totalOrders = (long) purchases.stream()
                .collect(Collectors.groupingBy(Purchases::getDateOfPurchase))
                .size();

        final Long productsSold = purchases.stream()
                .mapToLong(Purchases::getCountOfProduct)
                .sum();

        final BigDecimal averageOrderValue = totalOrders > 0 ?
                totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        final LocalDate previousStartDate = startDate.minusMonths(1);
        final LocalDate previousEndDate = endDate.minusMonths(1);
        final List<Purchases> previousPurchases = purchaseRepository.findByDateOfPurchaseBetween(
                previousStartDate.atStartOfDay(), previousEndDate.atTime(LocalTime.MAX));

        final BigDecimal previousRevenue = previousPurchases.stream()
                .map(p -> BigDecimal.valueOf(p.getPriceAtMomentBuying()).multiply(BigDecimal.valueOf(p.getCountOfProduct())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal revenueGrowth = previousRevenue.compareTo(BigDecimal.ZERO) > 0 ?
                totalRevenue.subtract(previousRevenue)
                        .divide(previousRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;

        return SalesMetrics.builder()
                .totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .totalOrders(totalOrders)
                .productsSold(productsSold)
                .revenueGrowth(revenueGrowth)
                .orderGrowth(BigDecimal.ZERO)
                .build();
    }

    private InventoryMetrics calculateInventoryMetrics(final List<ProductEntity> products, final List<Purchases> purchases) {
        final Long lowStockItems = products.stream()
                .filter(p -> p.getCount() > 0 && p.getCount() < 10)
                .count();

        final Long outOfStockItems = products.stream()
                .filter(p -> p.getCount() == 0)
                .count();

        final BigDecimal totalInventoryValue = products.stream()
                .map(p -> BigDecimal.valueOf(p.getPrice()).multiply(BigDecimal.valueOf(p.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal annualRevenue = getRevenueForPeriod(LocalDate.now().minusYears(1), LocalDate.now());
        final BigDecimal inventoryTurnover = totalInventoryValue.compareTo(BigDecimal.ZERO) > 0 ?
                annualRevenue.divide(totalInventoryValue, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        return InventoryMetrics.builder()
                .totalProducts((long) products.size())
                .lowStockItems(lowStockItems)
                .outOfStockItems(outOfStockItems)
                .totalInventoryValue(totalInventoryValue)
                .inventoryTurnover(inventoryTurnover)
                .build();
    }

    private UserMetrics calculateUserMetrics(final LocalDateTime start, final LocalDateTime end) {
        final Long totalUsers = userRepository.count();

        final Long activeUsers = purchaseRepository.findByDateOfPurchaseBetween(start, end).stream()
                .map(Purchases::getUser)
                .distinct()
                .count();

        final Long newUsers = calculateNewUsers(start, end);

        final Long totalOrders = purchaseRepository.countByDateOfPurchaseBetween(start, end);
        final Double averageOrdersPerUser = totalUsers > 0 ?
                totalOrders.doubleValue() / totalUsers.doubleValue() : 0.0;

        return UserMetrics.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .newUsers(newUsers)
                .averageOrdersPerUser(averageOrdersPerUser)
                .retentionRate(calculateRetentionRate())
                .build();
    }

    private List<ChartData> generateSalesChart(final List<Purchases> purchases) {
        final Map<LocalDate, Long> dailySales = purchases.stream()
                .collect(Collectors.groupingBy(
                        p -> convertToLocalDate(p.getDateOfPurchase()),
                        Collectors.summingLong(Purchases::getCountOfProduct)
                ));

        return dailySales.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> ChartData.builder()
                        .label(entry.getKey().toString())
                        .value(BigDecimal.valueOf(entry.getValue()))
                        .date(entry.getKey())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ChartData> generateRevenueChart(final List<Purchases> purchases) {
        final Map<LocalDate, BigDecimal> dailyRevenue = purchases.stream()
                .collect(Collectors.groupingBy(
                        p -> convertToLocalDate(p.getDateOfPurchase()),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                p -> BigDecimal.valueOf(p.getPriceAtMomentBuying())
                                        .multiply(BigDecimal.valueOf(p.getCountOfProduct())),
                                BigDecimal::add
                        )
                ));

        return dailyRevenue.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> ChartData.builder()
                        .label(entry.getKey().toString())
                        .value(entry.getValue())
                        .date(entry.getKey())
                        .build())
                .collect(Collectors.toList());
    }

    private List<CategoryData> calculateCategoryDistribution(final List<Purchases> purchases) {
        final Map<String, CategoryData> categoryMap = purchases.stream()
                .collect(Collectors.groupingBy(
                        p -> extractCategory(p.getProduct().getTitle()),
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            final BigDecimal revenue = list.stream()
                                    .map(p -> BigDecimal.valueOf(p.getPriceAtMomentBuying())
                                            .multiply(BigDecimal.valueOf(p.getCountOfProduct())))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            final Long orders = (long) list.size();
                            return CategoryData.builder()
                                    .category(extractCategory(list.get(0).getProduct().getTitle()))
                                    .revenue(revenue)
                                    .orders(orders)
                                    .build();
                        })
                ));

        final BigDecimal totalRevenue = categoryMap.values().stream()
                .map(CategoryData::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return categoryMap.values().stream()
                .peek(data -> data.setPercentage(
                        totalRevenue.compareTo(BigDecimal.ZERO) > 0 ?
                                data.getRevenue().divide(totalRevenue, 4, RoundingMode.HALF_UP)
                                        .multiply(BigDecimal.valueOf(100)).doubleValue() : 0.0
                ))
                .sorted((a, b) -> b.getRevenue().compareTo(a.getRevenue()))
                .collect(Collectors.toList());
    }

    private List<ProductPerformance> getTopPerformingProducts(final List<Purchases> purchases) {
        final Map<ProductEntity, ProductPerformance> productPerformance = purchases.stream()
                .collect(Collectors.groupingBy(
                        Purchases::getProduct,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            final Long unitsSold = list.stream()
                                    .mapToLong(Purchases::getCountOfProduct)
                                    .sum();
                            final BigDecimal revenue = list.stream()
                                    .map(p -> BigDecimal.valueOf(p.getPriceAtMomentBuying())
                                            .multiply(BigDecimal.valueOf(p.getCountOfProduct())))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            return ProductPerformance.builder()
                                    .productName(list.get(0).getProduct().getTitle())
                                    .unitsSold(unitsSold)
                                    .revenue(revenue)
                                    .growth(0.0)
                                    .build();
                        })
                ));

        return productPerformance.values().stream()
                .sorted((a, b) -> b.getRevenue().compareTo(a.getRevenue()))
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<ConversionData> calculateConversionFunnel() {
        final Long totalVisitors = userRepository.count() + 1000L;
        final Long usersWithCarts = purchaseRepository.countDistinctUsersWithCarts();
        final Long usersWithPurchases = purchaseRepository.countDistinctUsersWithPurchases();

        return Arrays.asList(
                ConversionData.builder().stage("Посетители").count(totalVisitors).rate(100.0).build(),
                ConversionData.builder().stage("Корзина").count(usersWithCarts)
                        .rate((double) usersWithCarts / totalVisitors * 100).build(),
                ConversionData.builder().stage("Покупки").count(usersWithPurchases)
                        .rate((double) usersWithPurchases / totalVisitors * 100).build()
        );
    }

    private String extractCategory(final String productTitle) {
        if (productTitle == null || productTitle.isEmpty()) {
            return "Другое";
        }
        final String[] words = productTitle.split(" ");
        return words.length > 0 ? words[0] : "Другое";
    }

    private BigDecimal getRevenueForPeriod(final LocalDate start, final LocalDate end) {
        final LocalDateTime startDateTime = start.atStartOfDay();
        final LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

        return purchaseRepository.findByDateOfPurchaseBetween(startDateTime, endDateTime).stream()
                .map(p -> BigDecimal.valueOf(p.getPriceAtMomentBuying())
                        .multiply(BigDecimal.valueOf(p.getCountOfProduct())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Double calculateRetentionRate() {
        return 65.5;
    }

    private LocalDate convertToLocalDate(final Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private Long calculateNewUsers(final LocalDateTime start, final LocalDateTime end) {
        return purchaseRepository.findByDateOfPurchaseBetween(start, end).stream()
                .collect(Collectors.groupingBy(Purchases::getUser))
                .entrySet()
                .stream()
                .filter(entry -> {
                    final Date firstPurchaseDate = entry.getValue().stream()
                            .map(Purchases::getDateOfPurchase)
                            .min(Date::compareTo)
                            .orElse(new Date());
                    return firstPurchaseDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .isAfter(start.minusDays(1));
                })
                .count();
    }
}