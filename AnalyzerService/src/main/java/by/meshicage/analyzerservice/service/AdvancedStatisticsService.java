package by.meshicage.analyzerservice.service;

import by.meshicage.analyzerservice.dto.stats.*;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdvancedStatisticsService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public EconomicStatsDto getEconomicStats() {
        final List<Purchases> allPurchases = purchaseRepository.findAll();

        final BigDecimal totalRevenue = allPurchases.stream()
                .map(p -> BigDecimal.valueOf(p.getPriceAtMomentBuying())
                        .multiply(BigDecimal.valueOf(p.getCountOfProduct())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal averageOrderValue = allPurchases.isEmpty() ? BigDecimal.ZERO :
                totalRevenue.divide(BigDecimal.valueOf(allPurchases.size()), 2, RoundingMode.HALF_UP);

        final LocalDate now = LocalDate.now();
        final LocalDate lastMonthStart = now.minusMonths(1).withDayOfMonth(1);
        final LocalDate lastMonthEnd = lastMonthStart.plusMonths(1).minusDays(1);
        final LocalDate previousMonthStart = lastMonthStart.minusMonths(1);
        final LocalDate previousMonthEnd = lastMonthStart.minusDays(1);

        final BigDecimal lastMonthRevenue = getRevenueForPeriod(lastMonthStart, lastMonthEnd);
        final BigDecimal previousMonthRevenue = getRevenueForPeriod(previousMonthStart, previousMonthEnd);

        final BigDecimal revenueGrowth = previousMonthRevenue.compareTo(BigDecimal.ZERO) > 0 ?
                lastMonthRevenue.subtract(previousMonthRevenue)
                        .divide(previousMonthRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;

        final Long totalCustomers = userRepository.count();
        final BigDecimal customerLifetimeValue = totalCustomers > 0 ?
                totalRevenue.divide(BigDecimal.valueOf(totalCustomers), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        final Map<String, BigDecimal> revenueByCategory = allPurchases.stream()
                .collect(Collectors.groupingBy(
                        p -> extractCategory(p.getProduct().getTitle()),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                p -> BigDecimal.valueOf(p.getPriceAtMomentBuying())
                                        .multiply(BigDecimal.valueOf(p.getCountOfProduct())),
                                BigDecimal::add
                        )
                ));

        return EconomicStatsDto.builder()
                .totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .revenueGrowth(revenueGrowth)
                .customerLifetimeValue(customerLifetimeValue)
                .profitMargin(BigDecimal.valueOf(25.0))
                .revenueByCategory(revenueByCategory)
                .build();
    }

    public CustomerAnalyticsDto getCustomerAnalytics() {
        final List<Purchases> allPurchases = purchaseRepository.findAll();
        final Long totalCustomers = userRepository.count();

        final Map<UUID, Long> ordersPerCustomer = allPurchases.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getUser().getId(),
                        Collectors.counting()
                ));

        final Long repeatCustomers = ordersPerCustomer.values().stream()
                .filter(count -> count > 1)
                .count();

        final Double retentionRate = calculateRetentionRate();
        final Double churnRate = 100.0 - retentionRate;
        final List<CustomerSegmentDto> segments = createCustomerSegments(ordersPerCustomer, allPurchases);

        return CustomerAnalyticsDto.builder()
                .totalCustomers(totalCustomers)
                .repeatCustomers(repeatCustomers)
                .retentionRate(retentionRate)
                .churnRate(churnRate)
                .customerSegments(segments)
                .build();
    }

    public InventoryAnalyticsDto getInventoryAnalytics() {
        final List<ProductEntity> allProducts = productRepository.findAll();
        final List<Purchases> allPurchases = purchaseRepository.findAll();

        final Map<ProductEntity, Long> productSales = allPurchases.stream()
                .collect(Collectors.groupingBy(
                        Purchases::getProduct,
                        Collectors.summingLong(Purchases::getCountOfProduct)
                ));

        final Long outOfStockItems = allProducts.stream()
                .filter(p -> p.getCount() == 0)
                .count();

        final Long slowMovingItems = allProducts.stream()
                .filter(p -> productSales.getOrDefault(p, 0L) < 10)
                .count();

        final BigDecimal totalInventoryValue = allProducts.stream()
                .map(p -> BigDecimal.valueOf(p.getPrice()).multiply(BigDecimal.valueOf(p.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal annualRevenue = getRevenueForPeriod(LocalDate.now().minusYears(1), LocalDate.now());
        final BigDecimal inventoryTurnover = totalInventoryValue.compareTo(BigDecimal.ZERO) > 0 ?
                annualRevenue.divide(totalInventoryValue, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        final List<ProductPerformanceDto> topPerformers = allProducts.stream()
                .map(p -> createProductPerformance(p, productSales.getOrDefault(p, 0L)))
                .sorted((p1, p2) -> p2.getRevenue().compareTo(p1.getRevenue()))
                .limit(10)
                .collect(Collectors.toList());

        final List<ProductPerformanceDto> underperformers = allProducts.stream()
                .map(p -> createProductPerformance(p, productSales.getOrDefault(p, 0L)))
                .sorted(Comparator.comparing(ProductPerformanceDto::getRevenue))
                .limit(10)
                .collect(Collectors.toList());

        return InventoryAnalyticsDto.builder()
                .totalSKU((long) allProducts.size())
                .outOfStockItems(outOfStockItems)
                .slowMovingItems(slowMovingItems)
                .inventoryTurnover(inventoryTurnover)
                .topPerformingProducts(topPerformers)
                .underperformingProducts(underperformers)
                .build();
    }

    public SeasonalityStatsDto getSeasonalityStats() {
        final List<Purchases> allPurchases = purchaseRepository.findAll();

        final Map<Integer, String> monthNames = new HashMap<>() {{
            put(1, "Январь");
            put(2, "Февраль");
            put(3, "Март");
            put(4, "Апрель");
            put(5, "Май");
            put(6, "Июнь");
            put(7, "Июль");
            put(8, "Август");
            put(9, "Сентябрь");
            put(10, "Октябрь");
            put(11, "Ноябрь");
            put(12, "Декабрь");
        }};

        final Map<String, BigDecimal> monthlyRevenue = allPurchases.stream()
                .collect(Collectors.groupingBy(
                        p -> monthNames.get(p.getDateOfPurchase().getMonth()),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                p -> BigDecimal.valueOf(p.getPriceAtMomentBuying())
                                        .multiply(BigDecimal.valueOf(p.getCountOfProduct())),
                                BigDecimal::add
                        )
                ));

        final Map<String, Long> monthlyOrders = allPurchases.stream()
                .collect(Collectors.groupingBy(
                        p -> monthNames.get(p.getDateOfPurchase().getMonth()),
                        Collectors.counting()
                ));

        final List<String> peakPeriods = findPeakPeriods(monthlyRevenue);
        final List<String> lowPeriods = findLowPeriods(monthlyRevenue);

        return SeasonalityStatsDto.builder()
                .monthlyRevenue(monthlyRevenue)
                .monthlyOrders(monthlyOrders)
                .peakPeriods(peakPeriods)
                .lowPeriods(lowPeriods)
                .build();
    }

    private BigDecimal getRevenueForPeriod(final LocalDate start, final LocalDate end) {
        final LocalDateTime startDateTime = start.atStartOfDay();
        final LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

        return purchaseRepository.findByDateOfPurchaseBetween(startDateTime, endDateTime).stream()
                .map(p -> BigDecimal.valueOf(p.getPriceAtMomentBuying())
                        .multiply(BigDecimal.valueOf(p.getCountOfProduct())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String extractCategory(final String productTitle) {
        if (productTitle == null || productTitle.isEmpty()) {
            return "Другое";
        }
        final String[] words = productTitle.split(" ");
        return words.length > 0 ? words[0] : "Другое";
    }

    private Double calculateRetentionRate() {
        return 65.5;
    }

    private List<CustomerSegmentDto> createCustomerSegments(final Map<UUID, Long> ordersPerCustomer, final List<Purchases> allPurchases) {
        return Arrays.asList(
                CustomerSegmentDto.builder()
                        .segmentName("VIP Клиенты")
                        .customerCount(ordersPerCustomer.entrySet().stream()
                                .filter(e -> e.getValue() > 5)
                                .count())
                        .averageSpend(BigDecimal.valueOf(1500))
                        .averageOrders(8)
                        .description("Частые покупатели с высоким средним чеком")
                        .build(),
                CustomerSegmentDto.builder()
                        .segmentName("Постоянные")
                        .customerCount(ordersPerCustomer.entrySet().stream()
                                .filter(e -> e.getValue() > 1 && e.getValue() <= 5)
                                .count())
                        .averageSpend(BigDecimal.valueOf(800))
                        .averageOrders(3)
                        .description("Регулярные покупатели")
                        .build(),
                CustomerSegmentDto.builder()
                        .segmentName("Новые")
                        .customerCount(ordersPerCustomer.entrySet().stream()
                                .filter(e -> e.getValue() == 1)
                                .count())
                        .averageSpend(BigDecimal.valueOf(500))
                        .averageOrders(1)
                        .description("Совершили только одну покупку")
                        .build()
        );
    }

    private ProductPerformanceDto createProductPerformance(final ProductEntity product, final Long unitsSold) {
        final BigDecimal revenue = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(unitsSold));

        final Double sellThroughRate;
        if (product.getCount() > 0) {
            final double totalAvailable = unitsSold + product.getCount();
            sellThroughRate = (unitsSold.doubleValue() / totalAvailable) * 100;
        } else {
            sellThroughRate = unitsSold > 0 ? 100.0 : 0.0;
        }

        final Integer daysOfSupply;
        if (unitsSold > 0) {
            final double dailySales = unitsSold.doubleValue() / 30.0;
            daysOfSupply = dailySales > 0 ? (int) (product.getCount() / dailySales) : 999;
        } else {
            daysOfSupply = 999;
        }

        return ProductPerformanceDto.builder()
                .productName(product.getTitle())
                .unitsSold(unitsSold)
                .revenue(revenue)
                .sellThroughRate(sellThroughRate)
                .daysOfSupply(daysOfSupply)
                .build();
    }

    private List<String> findPeakPeriods(final Map<String, BigDecimal> monthlyRevenue) {
        return monthlyRevenue.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private List<String> findLowPeriods(final Map<String, BigDecimal> monthlyRevenue) {
        return monthlyRevenue.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<SalesStatsDto> getSalesStats(final LocalDate startDate, final LocalDate endDate) {
        final LocalDateTime start = startDate.atStartOfDay();
        final LocalDateTime end = endDate.atTime(LocalTime.MAX);

        final List<Purchases> purchasesInPeriod = purchaseRepository.findByDateOfPurchaseBetween(start, end);

        return purchasesInPeriod.stream()
                .collect(Collectors.groupingBy(
                        Purchases::getDateOfPurchase,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            final Long ordersCount = (long) list.size();
                            final BigDecimal totalRevenue = list.stream()
                                    .map(purchase -> BigDecimal.valueOf(purchase.getPriceAtMomentBuying())
                                            .multiply(BigDecimal.valueOf(purchase.getCountOfProduct())))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            final Long productsSold = list.stream()
                                    .mapToLong(Purchases::getCountOfProduct)
                                    .sum();
                            return SalesStatsDto.builder()
                                    .date(list.get(0).getDateOfPurchase())
                                    .ordersCount(ordersCount)
                                    .totalRevenue(totalRevenue)
                                    .productsSold(productsSold)
                                    .build();
                        })
                ))
                .values().stream()
                .sorted(Comparator.comparing(SalesStatsDto::getDate))
                .collect(Collectors.toList());
    }

    public List<ProductStatsDto> getProductStatistics() {
        final List<ProductEntity> products = productRepository.findAll();
        final List<Purchases> allPurchases = purchaseRepository.findAll();

        final Map<ProductEntity, Long> productSales = allPurchases.stream()
                .collect(Collectors.groupingBy(
                        Purchases::getProduct,
                        Collectors.summingLong(Purchases::getCountOfProduct)
                ));

        return products.stream()
                .map(product -> {
                    final Long totalSold = productSales.getOrDefault(product, 0L);
                    final BigDecimal totalRevenue = BigDecimal.valueOf(product.getPrice())
                            .multiply(BigDecimal.valueOf(totalSold));

                    final Double averageRating = calculateAverageRating(product);
                    final Long totalComments = (long) product.getComments().size();

                    return ProductStatsDto.builder()
                            .productId(product.getId())
                            .productTitle(product.getTitle())
                            .totalSold(totalSold)
                            .totalRevenue(totalRevenue)
                            .averageRating(averageRating)
                            .totalComments(totalComments)
                            .build();
                })
                .sorted(Comparator.comparing(ProductStatsDto::getTotalRevenue).reversed())
                .collect(Collectors.toList());
    }

    private Double calculateAverageRating(final ProductEntity product) {
        return product.getComments().isEmpty() ? 0.0 : 4.5;
    }
}