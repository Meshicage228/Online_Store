package com.example.adminblservice.service.impl;

import com.example.adminblservice.dto.stats.MarketingStatsDto;
import com.example.adminblservice.dto.stats.ProductStatsDto;
import com.example.adminblservice.dto.stats.SalesStatsDto;
import com.example.adminblservice.dto.stats.UserStatsDto;
import com.example.adminblservice.entity.product.ProductEntity;
import com.example.adminblservice.entity.product.Purchases;
import com.example.adminblservice.repository.ProductRepository;
import com.example.adminblservice.repository.PurchaseRepository;
import com.example.adminblservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public MarketingStatsDto getMarketingStats() {
        final List<ProductEntity> allProducts = productRepository.findAll();
        final List<Purchases> allPurchases = purchaseRepository.findAll();

        final Long totalProducts = (long) allProducts.size();
        final Long lowStockProducts = allProducts.stream()
                .filter(product -> product.getCount() < 10)
                .count();

        final BigDecimal totalInventoryValue = allProducts.stream()
                .map(product -> BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(product.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final Map<ProductEntity, Long> productSales = allPurchases.stream()
                .collect(Collectors.groupingBy(
                        Purchases::getProduct,
                        Collectors.summingLong(Purchases::getCountOfProduct)
                ));

        final ProductStatsDto bestSelling = productSales.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> ProductStatsDto.builder()
                        .productId(entry.getKey().getId())
                        .productTitle(entry.getKey().getTitle())
                        .totalSold(entry.getValue())
                        .totalRevenue(BigDecimal.valueOf(entry.getKey().getPrice()).multiply(BigDecimal.valueOf(entry.getValue())))
                        .build())
                .orElse(null);

        final ProductStatsDto worstSelling = productSales.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(entry -> ProductStatsDto.builder()
                        .productId(entry.getKey().getId())
                        .productTitle(entry.getKey().getTitle())
                        .totalSold(entry.getValue())
                        .totalRevenue(BigDecimal.valueOf(entry.getKey().getPrice()).multiply(BigDecimal.valueOf(entry.getValue())))
                        .build())
                .orElse(null);

        final Long totalUsers = userRepository.count();
        final Long usersWithPurchases = purchaseRepository.findDistinctUsersWithPurchases();
        final Double conversionRate = totalUsers > 0 ?
                (usersWithPurchases.doubleValue() / totalUsers.doubleValue()) * 100 : 0.0;

        return MarketingStatsDto.builder()
                .totalProducts(totalProducts)
                .lowStockProducts(lowStockProducts)
                .totalInventoryValue(totalInventoryValue)
                .bestSellingProduct(bestSelling)
                .worstSellingProduct(worstSelling)
                .conversionRate(conversionRate)
                .build();
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

    public UserStatsDto getUserStats() {
        final Long totalUsers = userRepository.count();
        final LocalDate monthAgo = LocalDate.now().minusDays(30);
        final Long activeUsers = purchaseRepository.countActiveUsersSince(monthAgo.atStartOfDay());
        final LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        final Long newUsersThisMonth = purchaseRepository.countNewUsersSince(startOfMonth.atStartOfDay());
        final Long totalOrders = purchaseRepository.count();
        final Double averageOrdersPerUser = totalUsers > 0 ?
                totalOrders.doubleValue() / totalUsers.doubleValue() : 0.0;

        return UserStatsDto.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .newUsersThisMonth(newUsersThisMonth)
                .averageOrdersPerUser(averageOrdersPerUser)
                .build();
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