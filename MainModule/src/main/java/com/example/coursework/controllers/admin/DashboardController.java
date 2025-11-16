package com.example.coursework.controllers.admin;

import com.example.coursework.clients.DashboardClient;
import com.example.coursework.dto.dashboard.DashboardDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class DashboardController {

    private final DashboardClient dashboardService;

    @GetMapping
    public String getDashboard(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();

        final DashboardDataDto dashboardData = dashboardService.getDashboardData(startDate, endDate);

        model.addAttribute("dashboardData", dashboardData);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "adminDashboard";
    }
}