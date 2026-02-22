package com.zinier.capacity_planner.dashboard.service;

import com.zinier.capacity_planner.dashboard.dao.DashboardDaoV1;
import com.zinier.capacity_planner.dashboard.model.*;
import com.zinier.capacity_planner.dashboard.util.QuarterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImplV1 implements DashboardServiceV1 {

    private final DashboardDaoV1 dashboardDaoV1;

    @Override
    public DashboardResponseModel getDashboard() {

        int weeks = 13; // current quarter

        var metricsDto = dashboardDaoV1.fetchMetrics();
        var roleDtos = dashboardDaoV1.fetchRoleDistribution();
        var regionDtos = dashboardDaoV1.fetchRegionDistribution();
        var headcountDtos = dashboardDaoV1.fetchHeadcountByRole();
        var allocatedRaw = dashboardDaoV1.fetchAllocatedByRoleForQuarter();

        int totalEmployees = metricsDto.getTotalEmployees() != null ? metricsDto.getTotalEmployees().intValue() : 0;
        int activeProjects = metricsDto.getActiveProjects() != null ? metricsDto.getActiveProjects().intValue() : 0;
        double totalAllocation = metricsDto.getTotalAllocation() != null ? metricsDto.getTotalAllocation() : 0.0;
        int overUtilized = metricsDto.getOverUtilized() != null ? metricsDto.getOverUtilized().intValue() : 0;
        int underUtilized = metricsDto.getUnderUtilized() != null ? metricsDto.getUnderUtilized().intValue() : 0;

        double avgUtil = 0;

        if (totalEmployees > 0 && weeks > 0) {
            avgUtil = totalAllocation / (totalEmployees * weeks);
        }

        // Convert raw allocation query result to Map<Role, AllocatedSum>
        Map<String, Double> allocatedByRole = allocatedRaw.stream()
                .collect(Collectors.toMap(
                        obj -> obj[0].toString(),
                        obj -> ((Number) obj[1]).doubleValue()
                ));

        return DashboardResponseModel.builder()

                // ================= METRICS =================
                .metrics(
                        DashboardMetricsModel.builder()
                                .totalEmployees(totalEmployees)
                                .activeProjects(activeProjects)
                                .avgUtilization(avgUtil)
                                .overUtilization(overUtilized)
                                .underUtilization(underUtilized)
                                .build()
                )

                // ================= HEADCOUNT BY ROLE =================
                .headcount(
                        headcountDtos.stream()
                                .map(h -> HeadcountModel.builder()
                                        .role(h.getRole())
                                        .zinier(h.getZinier().intValue())
                                        .nonZinier(h.getNonZinier().intValue())
                                        .total(h.getTotal().intValue())
                                        .build()
                                )
                                .toList()
                )

                // ================= ALLOCATION BY ROLE (COUNT) =================
                .allocationByRole(
                        roleDtos.stream()
                                .map(r -> new RoleDistributionModel(
                                        r.getRole(),
                                        r.getCount().intValue()
                                ))
                                .toList()
                )

                // ================= REGION DISTRIBUTION =================
                .clusterDistribution(
                        regionDtos.stream()
                                .map(r -> new ClusterDistributionModel(
                                        r.getRegion(),
                                        r.getCount().intValue()
                                ))
                                .toList()
                )

                // ================= UTILIZATION STATUS =================
                .utilizationStatus(
                        new UtilizationStatusModel(
                                totalEmployees - overUtilized - underUtilized,
                                overUtilized,
                                underUtilized
                        )
                )

                // ================= RESOURCE ALLOCATION BY ROLE =================
                .resourceAllocationByRole(
                        headcountDtos.stream()
                                .map(h -> {

                                    double allocated = allocatedByRole
                                            .getOrDefault(h.getRole(), 0.0);

                                    int roleEmployees = h.getTotal().intValue();

                                    double totalCapacity = roleEmployees * weeks;

                                    double percentage = totalCapacity == 0
                                            ? 0
                                            : allocated / totalCapacity;

                                    int availableSeats = roleEmployees
                                            - (int) (allocated / weeks);

                                    return ResourceAllocationByRoleModel.builder()
                                            .role(h.getRole())
                                            .allocated((int) allocated)
                                            .availableSeats(availableSeats)
                                            .percentage(percentage)
                                            .build();
                                })
                                .toList()
                )

                .build();
    }

    @Override
    public ResourceAllocationResponseModel getResourceAllocations(LocalDate weekStartDate, Long employeeId) {

        var quarter = QuarterUtils.currentQuarter();
        boolean isWeekView = weekStartDate != null;

        LocalDate start = isWeekView ? weekStartDate : quarter.getStart();
        LocalDate end = isWeekView ? weekStartDate : quarter.getEnd();
        long weeks = isWeekView ? 1 : QuarterUtils.getWeeksInQuarter(quarter);

        var employees = dashboardDaoV1.fetchActiveEmployees(employeeId);
        var allocationByEmployee = dashboardDaoV1.fetchAllocationByEmployee(start, end);

        var resources = employees.stream()
                .map(emp -> {
                    double totalAllocation = allocationByEmployee
                            .getOrDefault(emp.getId().intValue(), 0.0);
                    double utilization = weeks > 0 ? (totalAllocation / weeks) * 100 : 0;

                    String status;
                    if (utilization == 0) {
                        status = "Available";
                    } else if (utilization <= 50) {
                        status = "Partial";
                    } else if (utilization <= 100) {
                        status = "Allocated";
                    } else {
                        status = "Overloaded";
                    }

                    return ResourceAllocationDetailModel.builder()
                            .employeeId(emp.getId())
                            .name(emp.getName())
                            .role(emp.getRole().name())
                            .region(emp.getRegion().name())
                            .utilization(Math.round(utilization * 100.0) / 100.0)
                            .status(status)
                            .build();
                })
                .toList();

        ResourceAllocationResponseModel.ResourceAllocationResponseModelBuilder builder =
                ResourceAllocationResponseModel.builder()
                        .quarter(QuarterUtils.getQuarterLabel())
                        .viewType(isWeekView ? "WEEK" : "QUARTER")
                        .resources(resources);

        if (isWeekView) {
            builder.weekStartDate(weekStartDate);
        }

        return builder.build();
    }

    @Override
    public List<ProjectSummaryModel> getActiveProjects() {
        return dashboardDaoV1.fetchActiveProjects().stream()
                .map(p -> ProjectSummaryModel.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .code(p.getCode())
                        .build())
                .toList();
    }

    @Override
    public ProjectAllocationResponseModel getProjectAllocations(
            Long projectId, LocalDate weekStartDate, Long employeeId) {

        var quarter = QuarterUtils.currentQuarter();
        boolean isWeekView = weekStartDate != null;

        LocalDate start = isWeekView ? weekStartDate : quarter.getStart();
        LocalDate end = isWeekView ? weekStartDate : quarter.getEnd();
        long weeks = isWeekView ? 1 : QuarterUtils.getWeeksInQuarter(quarter);

        var project = dashboardDaoV1.fetchActiveProjects().stream()
                .filter(p -> p.getId().equals(projectId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        var employees = dashboardDaoV1.fetchEmployeesForProject(
                projectId.intValue(), start, end, employeeId);

        var allocationByEmployee = dashboardDaoV1
                .fetchAllocationByEmployeeForProject(projectId.intValue(), start, end);

        var resources = employees.stream()
                .map(emp -> {
                    double totalAllocation = allocationByEmployee
                            .getOrDefault(emp.getId().intValue(), 0.0);
                    double utilization = weeks > 0 ? (totalAllocation / weeks) * 100 : 0;

                    String status;
                    if (utilization == 0) {
                        status = "Available";
                    } else if (utilization <= 50) {
                        status = "Partial";
                    } else if (utilization <= 100) {
                        status = "Allocated";
                    } else {
                        status = "Overloaded";
                    }

                    return ResourceAllocationDetailModel.builder()
                            .employeeId(emp.getId())
                            .name(emp.getName())
                            .role(emp.getRole().name())
                            .region(emp.getRegion().name())
                            .utilization(Math.round(utilization * 100.0) / 100.0)
                            .status(status)
                            .build();
                })
                .toList();

        ProjectAllocationResponseModel.ProjectAllocationResponseModelBuilder builder =
                ProjectAllocationResponseModel.builder()
                        .projectId(projectId)
                        .projectName(project.getName())
                        .projectCode(project.getCode())
                        .quarter(QuarterUtils.getQuarterLabel())
                        .viewType(isWeekView ? "WEEK" : "QUARTER")
                        .resources(resources);

        if (isWeekView) {
            builder.weekStartDate(weekStartDate);
        }

        return builder.build();
    }
}
