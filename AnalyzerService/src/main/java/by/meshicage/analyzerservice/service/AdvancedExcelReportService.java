package by.meshicage.analyzerservice.service;

import by.meshicage.analyzerservice.dto.stats.CustomerAnalyticsDto;
import by.meshicage.analyzerservice.dto.stats.CustomerSegmentDto;
import by.meshicage.analyzerservice.dto.stats.EconomicStatsDto;
import by.meshicage.analyzerservice.dto.stats.InventoryAnalyticsDto;
import by.meshicage.analyzerservice.dto.stats.ProductStatsDto;
import by.meshicage.analyzerservice.dto.stats.SalesStatsDto;
import by.meshicage.analyzerservice.dto.stats.SeasonalityStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdvancedExcelReportService {

    private final AdvancedStatisticsService statisticsService;

    public byte[] generateBusinessIntelligenceReport() throws IOException {
        try (final XSSFWorkbook workbook = new XSSFWorkbook()) {
            createExecutiveSummarySheet(workbook);
            createEconomicDashboardSheet(workbook);
            createCustomerAnalyticsSheet(workbook);
            createInventoryIntelligenceSheet(workbook);
            createProductPerformanceSheet(workbook);
            createSeasonalityAnalysisSheet(workbook);

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void createExecutiveSummarySheet(final XSSFWorkbook workbook) {
        final XSSFSheet sheet = workbook.createSheet("Обзор руководителя");

        final EconomicStatsDto economicStats = statisticsService.getEconomicStats();
        final CustomerAnalyticsDto customerStats = statisticsService.getCustomerAnalytics();
        final InventoryAnalyticsDto inventoryStats = statisticsService.getInventoryAnalytics();

        final CellStyle headerStyle = createHeaderStyle(workbook);
        final CellStyle kpiStyle = createKPIStyle(workbook);
        final CellStyle warningStyle = createWarningStyle(workbook);

        int rowNum = 0;

        createMergedHeader(sheet, rowNum, 0, 3, "ДАШБОРД РУКОВОДИТЕЛЯ - " + LocalDate.now(), headerStyle);
        rowNum++;

        final Row kpiRow1 = sheet.createRow(rowNum++);
        createCell(kpiRow1, 0, "Общая выручка", kpiStyle);
        createCell(kpiRow1, 1, economicStats.getTotalRevenue() + " BYN", kpiStyle);
        createCell(kpiRow1, 2, "Рост выручки", kpiStyle);
        createCell(kpiRow1, 3, String.format("%.1f%%", economicStats.getRevenueGrowth()), kpiStyle);

        final Row kpiRow2 = sheet.createRow(rowNum++);
        createCell(kpiRow2, 0, "Средний чек", kpiStyle);
        createCell(kpiRow2, 1, economicStats.getAverageOrderValue() + " BYN", kpiStyle);
        createCell(kpiRow2, 2, "CLV", kpiStyle);
        createCell(kpiRow2, 3, economicStats.getCustomerLifetimeValue() + " BYN", kpiStyle);

        final Row kpiRow3 = sheet.createRow(rowNum++);
        createCell(kpiRow3, 0, "Всего клиентов", kpiStyle);
        createCell(kpiRow3, 1, customerStats.getTotalCustomers().toString(), kpiStyle);
        createCell(kpiRow3, 2, "Retention Rate", kpiStyle);
        createCell(kpiRow3, 3, String.format("%.1f%%", customerStats.getRetentionRate()), kpiStyle);

        final Row kpiRow4 = sheet.createRow(rowNum++);
        createCell(kpiRow4, 0, "Товаров на складе", kpiStyle);
        createCell(kpiRow4, 1, inventoryStats.getTotalSKU().toString(), kpiStyle);
        createCell(kpiRow4, 2, "Оборачиваемость", kpiStyle);
        createCell(kpiRow4, 3, inventoryStats.getInventoryTurnover().toString(), kpiStyle);

        rowNum++;
        if (inventoryStats.getOutOfStockItems() > 0) {
            final Row warningRow = sheet.createRow(rowNum++);
            createCell(warningRow, 0, "Товаров нет в наличии: " + inventoryStats.getOutOfStockItems(), warningStyle);
        }
        if (inventoryStats.getSlowMovingItems() > 0) {
            final Row warningRow = sheet.createRow(rowNum++);
            createCell(warningRow, 0, "Медленно продаваемых товаров: " + inventoryStats.getSlowMovingItems(), warningStyle);
        }

        rowNum += 2;
        createRevenueByCategoryChart(sheet, economicStats.getRevenueByCategory(), rowNum);

        autoSizeColumns(sheet, 4);
    }

    private void createEconomicDashboardSheet(final XSSFWorkbook workbook) {
        final XSSFSheet sheet = workbook.createSheet("Экономика");

        final EconomicStatsDto economicStats = statisticsService.getEconomicStats();
        final List<SalesStatsDto> salesStats = statisticsService.getSalesStats(
                LocalDate.now().minusMonths(6), LocalDate.now());

        final CellStyle headerStyle = createHeaderStyle(workbook);
        final CellStyle valueStyle = createValueStyle(workbook);

        int rowNum = 0;

        createMergedHeader(sheet, rowNum, 0, 3, "ЭКОНОМИЧЕСКИЕ ПОКАЗАТЕЛИ", headerStyle);
        rowNum++;

        createDataRow(sheet, rowNum++, "Общая выручка", economicStats.getTotalRevenue() + " BYN", valueStyle);
        createDataRow(sheet, rowNum++, "Средний чек", economicStats.getAverageOrderValue() + " BYN", valueStyle);
        createDataRow(sheet, rowNum++, "Рост выручки", String.format("%.1f%%", economicStats.getRevenueGrowth()), valueStyle);
        createDataRow(sheet, rowNum++, "Маржа прибыли", String.format("%.1f%%", economicStats.getProfitMargin()), valueStyle);
        createDataRow(sheet, rowNum++, "Customer Lifetime Value", economicStats.getCustomerLifetimeValue() + " BYN", valueStyle);

        rowNum += 2;
        createSalesTrendChart(sheet, salesStats, rowNum, 0);

        autoSizeColumns(sheet, 4);
    }

    private void createCustomerAnalyticsSheet(final XSSFWorkbook workbook) {
        final XSSFSheet sheet = workbook.createSheet("Клиенты");

        final CustomerAnalyticsDto customerStats = statisticsService.getCustomerAnalytics();

        final CellStyle headerStyle = createHeaderStyle(workbook);
        final CellStyle valueStyle = createValueStyle(workbook);
        final CellStyle segmentStyle = createSegmentStyle(workbook);

        int rowNum = 0;

        createMergedHeader(sheet, rowNum, 0, 3, "АНАЛИТИКА КЛИЕНТОВ", headerStyle);
        rowNum++;

        createDataRow(sheet, rowNum++, "Всего клиентов", customerStats.getTotalCustomers().toString(), valueStyle);
        createDataRow(sheet, rowNum++, "Повторные клиенты", customerStats.getRepeatCustomers().toString(), valueStyle);
        createDataRow(sheet, rowNum++, "Retention Rate", String.format("%.1f%%", customerStats.getRetentionRate()), valueStyle);
        createDataRow(sheet, rowNum++, "Churn Rate", String.format("%.1f%%", customerStats.getChurnRate()), valueStyle);

        rowNum += 2;
        final Row segmentHeader = sheet.createRow(rowNum++);
        createCell(segmentHeader, 0, "Сегмент", headerStyle);
        createCell(segmentHeader, 1, "Кол-во", headerStyle);
        createCell(segmentHeader, 2, "Ср. расход", headerStyle);
        createCell(segmentHeader, 3, "Ср. заказы", headerStyle);
        createCell(segmentHeader, 4, "Описание", headerStyle);

        for (final CustomerSegmentDto segment : customerStats.getCustomerSegments()) {
            final Row row = sheet.createRow(rowNum++);
            createCell(row, 0, segment.getSegmentName(), segmentStyle);
            createCell(row, 1, segment.getCustomerCount().toString(), segmentStyle);
            createCell(row, 2, segment.getAverageSpend() + " BYN", segmentStyle);
            createCell(row, 3, segment.getAverageOrders().toString(), segmentStyle);
            createCell(row, 4, segment.getDescription(), segmentStyle);
        }

        rowNum += 2;
        createCustomerSegmentationChart(sheet, customerStats.getCustomerSegments(), rowNum, 0);

        autoSizeColumns(sheet, 5);
    }

    private void createInventoryIntelligenceSheet(final XSSFWorkbook workbook) {
        final XSSFSheet sheet = workbook.createSheet("Инвентарь");

        final InventoryAnalyticsDto inventoryStats = statisticsService.getInventoryAnalytics();

        final CellStyle headerStyle = createHeaderStyle(workbook);
        final CellStyle valueStyle = createValueStyle(workbook);
        final CellStyle warningStyle = createWarningStyle(workbook);

        int rowNum = 0;

        createMergedHeader(sheet, rowNum, 0, 3, "ИНТЕЛЛЕКТУАЛЬНЫЙ АНАЛИЗ ЗАПАСОВ", headerStyle);
        rowNum++;

        createDataRow(sheet, rowNum++, "Всего SKU", inventoryStats.getTotalSKU().toString(), valueStyle);
        createDataRow(sheet, rowNum++, "Нет в наличии", inventoryStats.getOutOfStockItems().toString(),
                inventoryStats.getOutOfStockItems() > 0 ? warningStyle : valueStyle);
        createDataRow(sheet, rowNum++, "Медленные товары", inventoryStats.getSlowMovingItems().toString(),
                inventoryStats.getSlowMovingItems() > 0 ? warningStyle : valueStyle);
        createDataRow(sheet, rowNum++, "Оборачиваемость", inventoryStats.getInventoryTurnover().toString(), valueStyle);

        autoSizeColumns(sheet, 4);
    }

    private void createProductPerformanceSheet(final XSSFWorkbook workbook) {
        final XSSFSheet sheet = workbook.createSheet("Товары");

        final List<ProductStatsDto> productStats = statisticsService.getProductStatistics();

        final CellStyle headerStyle = createHeaderStyle(workbook);
        final CellStyle valueStyle = createValueStyle(workbook);
        final CellStyle topPerformerStyle = createTopPerformerStyle(workbook);

        int rowNum = 0;

        createMergedHeader(sheet, rowNum, 0, 5, "ПРОИЗВОДИТЕЛЬНОСТЬ ТОВАРОВ", headerStyle);
        rowNum++;

        final Row topHeader = sheet.createRow(rowNum++);
        createCell(topHeader, 0, "ТОП-10 ТОВАРОВ ПО ВЫРУЧКЕ", headerStyle);

        final Row headerRow = sheet.createRow(rowNum++);
        createCell(headerRow, 0, "Товар", headerStyle);
        createCell(headerRow, 1, "Продано", headerStyle);
        createCell(headerRow, 2, "Выручка", headerStyle);
        createCell(headerRow, 3, "Рейтинг", headerStyle);
        createCell(headerRow, 4, "Комментарии", headerStyle);

        final List<ProductStatsDto> topProducts = productStats.stream()
                .limit(10)
                .toList();

        for (int i = 0; i < topProducts.size(); i++) {
            final ProductStatsDto product = topProducts.get(i);
            final Row row = sheet.createRow(rowNum++);
            final CellStyle style = i < 3 ? topPerformerStyle : valueStyle;

            createCell(row, 0, (i + 1) + ". " + product.getProductTitle(), style);
            createCell(row, 1, product.getTotalSold().toString(), style);
            createCell(row, 2, product.getTotalRevenue() + " BYN", style);
            createCell(row, 3, String.format("%.1f/5", product.getAverageRating()), style);
            createCell(row, 4, product.getTotalComments().toString(), style);
        }

        rowNum += 2;
        final Row bottomHeader = sheet.createRow(rowNum++);
        createCell(bottomHeader, 0, "ТОВАРЫ-АУТСАЙДЕРЫ", headerStyle);

        final Row bottomHeaderRow = sheet.createRow(rowNum++);
        createCell(bottomHeaderRow, 0, "Товар", headerStyle);
        createCell(bottomHeaderRow, 1, "Выручка", headerStyle);
        createCell(bottomHeaderRow, 2, "Продано", headerStyle);

        final List<ProductStatsDto> bottomProducts = productStats.stream()
                .skip(Math.max(0, productStats.size() - 5))
                .toList();

        for (final ProductStatsDto product : bottomProducts) {
            final Row row = sheet.createRow(rowNum++);
            createCell(row, 0, product.getProductTitle(), valueStyle);
            createCell(row, 1, product.getTotalRevenue() + " BYN", valueStyle);
            createCell(row, 2, product.getTotalSold().toString(), valueStyle);
        }

        rowNum += 2;
        createProductPerformanceChart(sheet, topProducts, rowNum, 0);

        autoSizeColumns(sheet, 5);
    }

    private void createSeasonalityAnalysisSheet(final XSSFWorkbook workbook) {
        final XSSFSheet sheet = workbook.createSheet("Сезонность");

        final SeasonalityStatsDto seasonalityStats = statisticsService.getSeasonalityStats();

        final CellStyle headerStyle = createHeaderStyle(workbook);
        final CellStyle valueStyle = createValueStyle(workbook);

        int rowNum = 0;

        createMergedHeader(sheet, rowNum, 0, 3, "АНАЛИЗ СЕЗОННОСТИ", headerStyle);
        rowNum++;

        createDataRow(sheet, rowNum++, "Пиковые месяцы", String.join(", ", seasonalityStats.getPeakPeriods()), valueStyle);
        createDataRow(sheet, rowNum++, "Низкие месяцы", String.join(", ", seasonalityStats.getLowPeriods()), valueStyle);

        rowNum += 2;
        final Row monthlyHeader = sheet.createRow(rowNum++);
        createCell(monthlyHeader, 0, "Месяц", headerStyle);
        createCell(monthlyHeader, 1, "Выручка", headerStyle);
        createCell(monthlyHeader, 2, "Заказы", headerStyle);

        for (final Map.Entry<String, BigDecimal> entry : seasonalityStats.getMonthlyRevenue().entrySet()) {
            final Row row = sheet.createRow(rowNum++);
            createCell(row, 0, entry.getKey(), valueStyle);
            createCell(row, 1, entry.getValue() + " BYN", valueStyle);
            createCell(row, 2, seasonalityStats.getMonthlyOrders().get(entry.getKey()).toString(), valueStyle);
        }

        rowNum += 2;
        createSeasonalityChart(sheet, seasonalityStats, rowNum, 0);

        autoSizeColumns(sheet, 3);
    }

    private void createRevenueByCategoryChart(final XSSFSheet sheet, final Map<String, BigDecimal> revenueByCategory,
                                              final int startRow) {
        if (revenueByCategory == null || revenueByCategory.isEmpty()) {
            final Row noDataRow = sheet.createRow(startRow);
            createCell(noDataRow, 0, "Нет данных для построения графика", createHeaderStyle(sheet.getWorkbook()));
            return;
        }

        final int dataStartRow = startRow + 1;
        int currentRow = dataStartRow;

        final Row chartTitle = sheet.createRow(startRow);
        createCell(chartTitle, 0, "ВЫРУЧКА ПО КАТЕГОРИЯМ", createHeaderStyle(sheet.getWorkbook()));

        for (final Map.Entry<String, BigDecimal> entry : revenueByCategory.entrySet()) {
            final Row row = sheet.createRow(currentRow++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue().doubleValue());
        }

        try {
            final XSSFDrawing drawing = sheet.createDrawingPatriarch();
            final XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 3, startRow, 10, startRow + 12);

            final XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("Распределение выручки по категориям");
            chart.setTitleOverlay(false);

            final XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);

            final XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            final XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            final XDDFCategoryDataSource categoryData = XDDFDataSourcesFactory.fromStringCellRange(
                    sheet, new CellRangeAddress(dataStartRow, currentRow - 1, 0, 0));
            final XDDFNumericalDataSource<Double> valueData = XDDFDataSourcesFactory.fromNumericCellRange(
                    sheet, new CellRangeAddress(dataStartRow, currentRow - 1, 1, 1));

            final XDDFBarChartData barChart = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
            final XDDFBarChartData.Series series = (XDDFBarChartData.Series) barChart.addSeries(categoryData, valueData);
            series.setTitle("Выручка", null);

            chart.plot(barChart);

        } catch (final Exception e) {
            log.warn("Не удалось создать график выручки по категориям: {}", e.getMessage());
            final Row errorRow = sheet.createRow(startRow + 1);
            createCell(errorRow, 0, "Ошибка при создании графика", createWarningStyle(sheet.getWorkbook()));
        }
    }

    private void createSalesTrendChart(final XSSFSheet sheet, final List<SalesStatsDto> salesStats, final int startRow, final int startCol) {
        if (salesStats == null || salesStats.isEmpty()) {
            final Row noDataRow = sheet.createRow(startRow);
            createCell(noDataRow, startCol, "Нет данных для построения графика", createHeaderStyle(sheet.getWorkbook()));
            return;
        }

        final int dataStartRow = startRow + 1;
        int currentRow = dataStartRow;

        final Row chartTitle = sheet.createRow(startRow);
        createCell(chartTitle, startCol, "ДИНАМИКА ПРОДАЖ", createHeaderStyle(sheet.getWorkbook()));

        for (final SalesStatsDto stat : salesStats) {
            final Row row = sheet.createRow(currentRow++);
            row.createCell(startCol).setCellValue(stat.getDate());
            row.createCell(startCol + 1).setCellValue(stat.getTotalRevenue().doubleValue());
        }

        try {
            final XSSFDrawing drawing = sheet.createDrawingPatriarch();
            final XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, startCol + 4, startRow, startCol + 12, startRow + 12);

            final XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("Динамика продаж по месяцам");
            chart.setTitleOverlay(false);

            final XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);

            final XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            final XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            final XDDFCategoryDataSource categoryData = XDDFDataSourcesFactory.fromStringCellRange(
                    sheet, new CellRangeAddress(dataStartRow, currentRow - 1, startCol, startCol));
            final XDDFNumericalDataSource<Double> revenueData = XDDFDataSourcesFactory.fromNumericCellRange(
                    sheet, new CellRangeAddress(dataStartRow, currentRow - 1, startCol + 1, startCol + 1));

            final XDDFLineChartData lineChart = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
            final XDDFLineChartData.Series revenueSeries = (XDDFLineChartData.Series) lineChart.addSeries(categoryData, revenueData);
            revenueSeries.setTitle("Выручка", null);

            chart.plot(lineChart);

        } catch (final Exception e) {
            log.warn("Не удалось создать график динамики продаж: {}", e.getMessage());
            final Row errorRow = sheet.createRow(startRow + 1);
            createCell(errorRow, startCol, "Ошибка при создании графика", createWarningStyle(sheet.getWorkbook()));
        }
    }

    private void createCustomerSegmentationChart(final XSSFSheet sheet, final List<CustomerSegmentDto> segments, final int startRow, final int startCol) {
        if (segments == null || segments.isEmpty()) {
            final Row noDataRow = sheet.createRow(startRow);
            createCell(noDataRow, startCol, "Нет данных для построения графика", createHeaderStyle(sheet.getWorkbook()));
            return;
        }

        final int dataStartRow = startRow + 1;
        int currentRow = dataStartRow;

        final Row chartTitle = sheet.createRow(startRow);
        createCell(chartTitle, startCol, "СЕГМЕНТАЦИЯ КЛИЕНТОВ", createHeaderStyle(sheet.getWorkbook()));

        for (final CustomerSegmentDto segment : segments) {
            final Row row = sheet.createRow(currentRow++);
            row.createCell(startCol).setCellValue(segment.getSegmentName());
            row.createCell(startCol + 1).setCellValue(segment.getCustomerCount());
        }

        try {
            final XSSFDrawing drawing = sheet.createDrawingPatriarch();
            final XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, startCol + 3, startRow, startCol + 9, startRow + 10);

            final XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("Распределение клиентов по сегментам");
            chart.setTitleOverlay(false);

            final XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.RIGHT);

            final XDDFCategoryDataSource categoryData = XDDFDataSourcesFactory.fromStringCellRange(
                    sheet, new CellRangeAddress(dataStartRow, currentRow - 1, startCol, startCol));
            final XDDFNumericalDataSource<Double> valueData = XDDFDataSourcesFactory.fromNumericCellRange(
                    sheet, new CellRangeAddress(dataStartRow, currentRow - 1, startCol + 1, startCol + 1));

            final XDDFPieChartData pieChart = (XDDFPieChartData) chart.createData(ChartTypes.PIE, null, null);
            pieChart.addSeries(categoryData, valueData);

            chart.plot(pieChart);

        } catch (final Exception e) {
            log.warn("Не удалось создать график сегментации клиентов: {}", e.getMessage());
            final Row errorRow = sheet.createRow(startRow + 1);
            createCell(errorRow, startCol, "Ошибка при создании графика", createWarningStyle(sheet.getWorkbook()));
        }
    }

    private void createProductPerformanceChart(final XSSFSheet sheet, final List<ProductStatsDto> products, final int startRow, final int startCol) {
        if (products == null || products.isEmpty()) {
            final Row noDataRow = sheet.createRow(startRow);
            createCell(noDataRow, startCol, "Нет данных для построения графика", createHeaderStyle(sheet.getWorkbook()));
            return;
        }

        final int dataStartRow = startRow + 1;
        int currentRow = dataStartRow;

        final Row chartTitle = sheet.createRow(startRow);
        createCell(chartTitle, startCol, "ABC-АНАЛИЗ ТОВАРОВ", createHeaderStyle(sheet.getWorkbook()));

        for (final ProductStatsDto product : products) {
            final Row row = sheet.createRow(currentRow++);
            row.createCell(startCol).setCellValue(product.getProductTitle());
            row.createCell(startCol + 1).setCellValue(product.getTotalRevenue().doubleValue());
        }

        try {
            final XSSFDrawing drawing = sheet.createDrawingPatriarch();
            final XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, startCol + 3, startRow, startCol + 11, startRow + 12);

            final XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("ABC-анализ товаров (Парето)");
            chart.setTitleOverlay(false);

            final XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);

            final XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            final XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            final XDDFCategoryDataSource categoryData = XDDFDataSourcesFactory.fromStringCellRange(
                    sheet, new CellRangeAddress(dataStartRow, currentRow - 1, startCol, startCol));
            final XDDFNumericalDataSource<Double> valueData = XDDFDataSourcesFactory.fromNumericCellRange(
                    sheet, new CellRangeAddress(dataStartRow, currentRow - 1, startCol + 1, startCol + 1));

            final XDDFBarChartData barChart = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
            barChart.addSeries(categoryData, valueData).setTitle("Выручка", null);

            chart.plot(barChart);

        } catch (final Exception e) {
            log.warn("Не удалось создать график ABC-анализа: {}", e.getMessage());
            final Row errorRow = sheet.createRow(startRow + 1);
            createCell(errorRow, startCol, "Ошибка при создании графика", createWarningStyle(sheet.getWorkbook()));
        }
    }

    private void createSeasonalityChart(final XSSFSheet sheet, final SeasonalityStatsDto seasonality, final int startRow, final int startCol) {
        if (seasonality == null || seasonality.getMonthlyRevenue() == null || seasonality.getMonthlyRevenue().isEmpty()) {
            final Row noDataRow = sheet.createRow(startRow);
            createCell(noDataRow, startCol, "Нет данных для построения графика", createHeaderStyle(sheet.getWorkbook()));
            return;
        }

        final int dataStartRow = startRow + 1;
        int currentRow = dataStartRow;

        final Row chartTitle = sheet.createRow(startRow);
        createCell(chartTitle, startCol, "СЕЗОННОСТЬ ПРОДАЖ", createHeaderStyle(sheet.getWorkbook()));

        for (final Map.Entry<String, BigDecimal> entry : seasonality.getMonthlyRevenue().entrySet()) {
            final Row row = sheet.createRow(currentRow++);
            row.createCell(startCol).setCellValue(entry.getKey());
            row.createCell(startCol + 1).setCellValue(entry.getValue().doubleValue());
        }

        try {
            final XSSFDrawing drawing = sheet.createDrawingPatriarch();
            final XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, startCol + 3, startRow, startCol + 11, startRow + 12);

            final XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("Сезонность продаж по месяцам");
            chart.setTitleOverlay(false);

            final XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);

            final XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            final XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            final XDDFCategoryDataSource categoryData = XDDFDataSourcesFactory.fromStringCellRange(
                    sheet, new CellRangeAddress(dataStartRow, currentRow - 1, startCol, startCol));
            final XDDFNumericalDataSource<Double> valueData = XDDFDataSourcesFactory.fromNumericCellRange(
                    sheet, new CellRangeAddress(dataStartRow, currentRow - 1, startCol + 1, startCol + 1));

            final XDDFLineChartData lineChart = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
            lineChart.addSeries(categoryData, valueData).setTitle("Выручка", null);

            chart.plot(lineChart);

        } catch (final Exception e) {
            log.warn("Не удалось создать график сезонности: {}", e.getMessage());
            final Row errorRow = sheet.createRow(startRow + 1);
            createCell(errorRow, startCol, "Ошибка при создании графика", createWarningStyle(sheet.getWorkbook()));
        }
    }

    private CellStyle createHeaderStyle(final XSSFWorkbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        final Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createKPIStyle(final XSSFWorkbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        final Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createValueStyle(final XSSFWorkbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createWarningStyle(final XSSFWorkbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        final Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.RED.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createSegmentStyle(final XSSFWorkbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTopPerformerStyle(final XSSFWorkbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        final Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createQuestionStyle(final XSSFWorkbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        final Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        return style;
    }

    private void createMergedHeader(final XSSFSheet sheet, final int rowNum, final int startCol, final int endCol, final String text, final CellStyle style) {
        final Row row = sheet.createRow(rowNum);
        final Cell cell = row.createCell(startCol);
        cell.setCellValue(text);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, startCol, endCol));
    }

    private void createDataRow(final XSSFSheet sheet, final int rowNum, final String label, final String value, final CellStyle style) {
        final Row row = sheet.createRow(rowNum);
        createCell(row, 0, label, style);
        createCell(row, 1, value, style);
    }

    private void createCell(final Row row, final int column, final String value, final CellStyle style) {
        final Cell cell = row.createCell(column);
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private void autoSizeColumns(final XSSFSheet sheet, final int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}