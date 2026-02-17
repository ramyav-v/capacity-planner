package com.zinier.capacity_planner.dashboard;

import com.zinier.capacity_planner.dashboard.model.DashboardResponseModel;
import com.zinier.capacity_planner.dashboard.model.ProjectAllocationResponseModel;
import com.zinier.capacity_planner.dashboard.model.ProjectSummaryModel;
import com.zinier.capacity_planner.dashboard.model.ResourceAllocationResponseModel;
import com.zinier.capacity_planner.dashboard.service.DashboardServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${spring.data.rest.base-path}/v1/dashboard")
@RequiredArgsConstructor
public class DashboardControllerV1 {
    private final DashboardServiceV1 dashboardService;

    @GetMapping
    public DashboardResponseModel getDashboard() {
        return dashboardService.getDashboard();
    }

    @GetMapping("/allocations")
    public ResourceAllocationResponseModel getResourceAllocations(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate,
            @RequestParam(required = false) Long employeeId) {
        return dashboardService.getResourceAllocations(weekStartDate, employeeId);
    }

    @GetMapping("/projects")
    public List<ProjectSummaryModel> getActiveProjects() {
        return dashboardService.getActiveProjects();
    }

    @GetMapping("/projects/{projectId}/allocations")
    public ProjectAllocationResponseModel getProjectAllocations(
            @PathVariable Long projectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate,
            @RequestParam(required = false) Long employeeId) {
        return dashboardService.getProjectAllocations(projectId, weekStartDate, employeeId);
    }
}

