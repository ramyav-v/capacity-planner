package com.zinier.capacity_planner.dashboard;

import com.zinier.capacity_planner.dashboard.model.DashboardResponseModel;
import com.zinier.capacity_planner.dashboard.service.DashboardServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${spring.data.rest.base-path}/v1/dashboard")
@RequiredArgsConstructor
public class DashboardControllerV1 {
    private final DashboardServiceV1 dashboardService;

    @GetMapping
    public DashboardResponseModel getDashboard() {
        return dashboardService.getDashboard();
    }
}

