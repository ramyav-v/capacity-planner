package com.zinier.capacity_planner.dashboard.service;

import com.zinier.capacity_planner.dashboard.model.DashboardResponseModel;
import com.zinier.capacity_planner.dashboard.model.HiringGapResponseModel;
import com.zinier.capacity_planner.dashboard.model.ProjectAllocationResponseModel;
import com.zinier.capacity_planner.dashboard.model.ProjectSummaryModel;
import com.zinier.capacity_planner.dashboard.model.ResourceAllocationResponseModel;

import java.time.LocalDate;
import java.util.List;

public interface DashboardServiceV1 {
    DashboardResponseModel getDashboard();
    ResourceAllocationResponseModel getResourceAllocations(LocalDate weekStartDate, Long employeeId);
    List<ProjectSummaryModel> getActiveProjects();
    ProjectAllocationResponseModel getProjectAllocations(Long projectId, LocalDate weekStartDate, Long employeeId);
    HiringGapResponseModel getHiringGap(LocalDate startDate, LocalDate endDate);
}
