package com.example.coursework.clients;

import com.example.coursework.configuration.ProjectConfig;
import com.example.coursework.dto.dashboard.DashboardDataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "${app.clients.dashboard.name}",
        url = "${app.clients.dashboard.url}",
        path = "${app.clients.dashboard.path}",
        configuration = ProjectConfig.class)
public interface DashboardClient {

    @GetMapping
    DashboardDataDto getDashboardData(
            @RequestParam(value = "startDate", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @RequestParam(value = "endDate", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
}