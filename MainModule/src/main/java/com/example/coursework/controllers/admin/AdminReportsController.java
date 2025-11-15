package com.example.coursework.controllers.admin;

import com.example.coursework.clients.ReportsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/reports")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminReportsController {

    private final ReportsClient reportsClient;

    @GetMapping
    public ModelAndView getReportsPage() {
        return new ModelAndView("adminReportsPage");
    }

    @GetMapping("/full")
    public ResponseEntity<byte[]> downloadFullReport() {
        final byte[] report = reportsClient.generateFullReport();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=full_report_" + LocalDate.now() + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(report);
    }

    @GetMapping("/sales")
    public ResponseEntity<byte[]> downloadSalesReport(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate) {
        
        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        final byte[] report = reportsClient.generateSalesReport(startDate, endDate);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=sales_report_" + startDate + "_to_" + endDate + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(report);
    }

    @GetMapping("/business-intelligence")
    public ResponseEntity<byte[]> downloadBusinessIntelligenceReport() {
        final byte[] report = reportsClient.generateBusinessIntelligenceReport();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=business_intelligence_report_" + LocalDate.now() + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(report);
    }
}