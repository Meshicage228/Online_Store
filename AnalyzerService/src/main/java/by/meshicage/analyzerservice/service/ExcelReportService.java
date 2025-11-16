package by.meshicage.analyzerservice.service;

import by.meshicage.analyzerservice.dto.stats.MarketingStatsDto;
import by.meshicage.analyzerservice.dto.stats.ProductStatsDto;
import by.meshicage.analyzerservice.dto.stats.SalesStatsDto;
import by.meshicage.analyzerservice.dto.stats.UserStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelReportService {

    private final StatisticsService statisticsService;

    public byte[] generateFullReport() throws IOException {
        try (final Workbook workbook = new XSSFWorkbook()) {
            createMarketingStatsSheet(workbook);
            createSalesStatsSheet(workbook);
            createUserStatsSheet(workbook);
            createProductStatsSheet(workbook);

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] generateSalesReport(final LocalDate startDate, final LocalDate endDate) throws IOException {
        try (final Workbook workbook = new XSSFWorkbook()) {
            createSalesStatsSheet(workbook, startDate, endDate);

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void createMarketingStatsSheet(final Workbook workbook) {
        final Sheet sheet = workbook.createSheet("Маркетинговая статистика");
        final MarketingStatsDto stats = statisticsService.getMarketingStats();
        final CellStyle headerStyle = createHeaderStyle(workbook);
        final CellStyle valueStyle = createValueStyle(workbook);

        int rowNum = 0;

        final Row headerRow = sheet.createRow(rowNum++);
        createCell(headerRow, 0, "Показатель", headerStyle);
        createCell(headerRow, 1, "Значение", headerStyle);

        createDataRow(sheet, rowNum++, "Общее количество товаров", stats.getTotalProducts().toString(), valueStyle);
        createDataRow(sheet, rowNum++, "Товары с низким запасом (<10)", stats.getLowStockProducts().toString(), valueStyle);
        createDataRow(sheet, rowNum++, "Общая стоимость инвентаря", stats.getTotalInventoryValue() + " BYN", valueStyle);
        createDataRow(sheet, rowNum++, "Коэффициент конверсии", String.format("%.2f%%", stats.getConversionRate()), valueStyle);

        if (stats.getBestSellingProduct() != null) {
            createDataRow(sheet, rowNum++, "Самый продаваемый товар",
                    stats.getBestSellingProduct().getProductTitle() + " (" + stats.getBestSellingProduct().getTotalSold() + " шт.)",
                    valueStyle);
        }

        if (stats.getWorstSellingProduct() != null) {
            createDataRow(sheet, rowNum++, "Наименее продаваемый товар",
                    stats.getWorstSellingProduct().getProductTitle() + " (" + stats.getWorstSellingProduct().getTotalSold() + " шт.)",
                    valueStyle);
        }

        autoSizeColumns(sheet, 2);
    }

    private void createSalesStatsSheet(final Workbook workbook) {
        final LocalDate endDate = LocalDate.now();
        final LocalDate startDate = endDate.minusMonths(1);
        createSalesStatsSheet(workbook, startDate, endDate);
    }

    private void createSalesStatsSheet(final Workbook workbook, final LocalDate startDate, final LocalDate endDate) {
        final Sheet sheet = workbook.createSheet("Статистика продаж");
        final List<SalesStatsDto> salesStats = statisticsService.getSalesStats(startDate, endDate);
        final CellStyle headerStyle = createHeaderStyle(workbook);
        final CellStyle valueStyle = createValueStyle(workbook);

        int rowNum = 0;

        final Row periodRow = sheet.createRow(rowNum++);
        createCell(periodRow, 0, "Период отчета:", headerStyle);
        createCell(periodRow, 1, startDate + " - " + endDate, valueStyle);

        rowNum++;

        final Row headerRow = sheet.createRow(rowNum++);
        createCell(headerRow, 0, "Дата", headerStyle);
        createCell(headerRow, 1, "Количество заказов", headerStyle);
        createCell(headerRow, 2, "Общая выручка", headerStyle);
        createCell(headerRow, 3, "Товаров продано", headerStyle);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        Long totalOrders = 0L;
        Long totalProductsSold = 0L;

        for (final SalesStatsDto stat : salesStats) {
            final Row row = sheet.createRow(rowNum++);
            createCell(row, 1, stat.getOrdersCount().toString(), valueStyle);
            createCell(row, 2, stat.getTotalRevenue() + " BYN", valueStyle);
            createCell(row, 3, stat.getProductsSold().toString(), valueStyle);

            totalRevenue = totalRevenue.add(stat.getTotalRevenue());
            totalOrders += stat.getOrdersCount();
            totalProductsSold += stat.getProductsSold();
        }

        final Row totalRow = sheet.createRow(rowNum++);
        createCell(totalRow, 0, "ИТОГО:", headerStyle);
        createCell(totalRow, 1, totalOrders.toString(), headerStyle);
        createCell(totalRow, 2, totalRevenue + " BYN", headerStyle);
        createCell(totalRow, 3, totalProductsSold.toString(), headerStyle);

        autoSizeColumns(sheet, 4);
    }

    private void createUserStatsSheet(final Workbook workbook) {
        final Sheet sheet = workbook.createSheet("Статистика пользователей");
        final UserStatsDto userStats = statisticsService.getUserStats();
        final CellStyle headerStyle = createHeaderStyle(workbook);
        final CellStyle valueStyle = createValueStyle(workbook);

        int rowNum = 0;

        final Row headerRow = sheet.createRow(rowNum++);
        createCell(headerRow, 0, "Показатель", headerStyle);
        createCell(headerRow, 1, "Значение", headerStyle);

        createDataRow(sheet, rowNum++, "Всего пользователей", userStats.getTotalUsers().toString(), valueStyle);
        createDataRow(sheet, rowNum++, "Активных пользователей", userStats.getActiveUsers().toString(), valueStyle);
        createDataRow(sheet, rowNum++, "Новых пользователей за месяц", userStats.getNewUsersThisMonth().toString(), valueStyle);
        createDataRow(sheet, rowNum++, "Среднее заказов на пользователя",
                String.format("%.2f", userStats.getAverageOrdersPerUser()), valueStyle);

        autoSizeColumns(sheet, 2);
    }

    private void createProductStatsSheet(final Workbook workbook) {
        final Sheet sheet = workbook.createSheet("Статистика товаров");
        final List<ProductStatsDto> productStats = statisticsService.getProductStatistics();
        final CellStyle headerStyle = createHeaderStyle(workbook);
        final CellStyle valueStyle = createValueStyle(workbook);

        int rowNum = 0;

        final Row headerRow = sheet.createRow(rowNum++);
        createCell(headerRow, 0, "ID товара", headerStyle);
        createCell(headerRow, 1, "Название", headerStyle);
        createCell(headerRow, 2, "Продано шт.", headerStyle);
        createCell(headerRow, 3, "Общая выручка", headerStyle);
        createCell(headerRow, 4, "Средний рейтинг", headerStyle);
        createCell(headerRow, 5, "Комментарии", headerStyle);

        for (final ProductStatsDto stat : productStats) {
            final Row row = sheet.createRow(rowNum++);
            createCell(row, 0, stat.getProductId().toString(), valueStyle);
            createCell(row, 1, stat.getProductTitle(), valueStyle);
            createCell(row, 2, stat.getTotalSold().toString(), valueStyle);
            createCell(row, 3, stat.getTotalRevenue() + " BYN", valueStyle);
            createCell(row, 4, String.format("%.1f", stat.getAverageRating()), valueStyle);
            createCell(row, 5, stat.getTotalComments().toString(), valueStyle);
        }

        autoSizeColumns(sheet, 6);
    }

    private CellStyle createHeaderStyle(final Workbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        final Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createValueStyle(final Workbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void createCell(final Row row, final int column, final String value, final CellStyle style) {
        final Cell cell = row.createCell(column);
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private void createDataRow(final Sheet sheet, final int rowNum, final String label, final String value, final CellStyle style) {
        final Row row = sheet.createRow(rowNum);
        createCell(row, 0, label, style);
        createCell(row, 1, value, style);
    }

    private void autoSizeColumns(final Sheet sheet, final int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}