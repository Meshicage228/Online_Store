package by.meshicage.analyzerservice.controller;

import by.meshicage.analyzerservice.service.AdvancedExcelReportService;
import by.meshicage.analyzerservice.service.ExcelReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ExcelReportService excelReportService;
    private final AdvancedExcelReportService advancedExcelReportService;

    @GetMapping("/full")
    public ResponseEntity<byte[]> generateFullReport() {
        try {
            final byte[] report = excelReportService.generateFullReport();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=full_report_" + LocalDate.now() + ".xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(report);

        } catch (final IOException e) {
            log.error("Error generating full report", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sales")
    public ResponseEntity<byte[]> generateSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusMonths(6);
        if (endDate == null) endDate = LocalDate.now();

        try {
            final byte[] report = excelReportService.generateSalesReport(startDate, endDate);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=sales_report_" + startDate + "_to_" + endDate + ".xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(report);

        } catch (final IOException e) {
            log.error("Error generating sales report", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/business-intelligence")
    public ResponseEntity<byte[]> generateBusinessIntelligenceReport() {
        try {
            final byte[] report = advancedExcelReportService.generateBusinessIntelligenceReport();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=business_intelligence_report_" + LocalDate.now() + ".xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(report);

        } catch (final IOException e) {
            log.error("Error generating business intelligence report", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}