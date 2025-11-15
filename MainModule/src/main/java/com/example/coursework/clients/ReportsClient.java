package com.example.coursework.clients;

import com.example.coursework.configuration.ProjectConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(
        name = "${app.clients.reports.name}",
        url = "${app.clients.reports.url}",
        path = "${app.clients.reports.path}",
        configuration = ProjectConfig.class
)
public interface ReportsClient {

    @GetMapping(value = "/full", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    byte[] generateFullReport();

    @GetMapping(value = "/sales", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    byte[] generateSalesReport(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @GetMapping(value = "/business-intelligence", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    byte[] generateBusinessIntelligenceReport();
}