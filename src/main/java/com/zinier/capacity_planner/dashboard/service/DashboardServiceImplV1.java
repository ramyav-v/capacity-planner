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

        var quarter = QuarterUtils.currentQuarter();

        var roleDtos = dashboardDaoV1.fetchRoleDistribution();
        var regionDtos = dashboardDaoV1.fetchRegionDistribution();
        var headcountDtos = dashboardDaoV1.fetchHeadcountByRole();
        var allocatedRaw = dashboardDaoV1.fetchAllocatedByRoleForQuarter();

        var employees = dashboardDaoV1.fetchActiveEmployees(null);
        int totalEmployees = employees.size();
        int activeProjects = dashboardDaoV1.fetchActiveProjects().size();

        // Per-employee allocations scoped to current quarter
        var allocationByEmployee = dashboardDaoV1.fetchAllocationByEmployee(
                quarter.getStart(), quarter.getEnd());
        var weekCountByEmployee = dashboardDaoV1.fetchWeekCountByEmployee(
                quarter.getStart(), quarter.getEnd());

        // Group employee names by role and region
        Map<String, List<String>> namesByRole = employees.stream()
                .collect(Collectors.groupingBy(
                        emp -> emp.getRole().name(),
                        Collectors.mapping(emp -> emp.getName(), Collectors.toList())
                ));

        Map<String, List<String>> namesByRegion = employees.stream()
                .collect(Collectors.groupingBy(
                        emp -> emp.getRegion().name(),
                        Collectors.mapping(emp -> emp.getName(), Collectors.toList())
                ));

        // Compute per-employee utilization and aggregate
        double totalUtil = 0;
        int overUtilized = 0;
        int underUtilized = 0;
        List<String> optimalNames = new java.util.ArrayList<>();
        List<String> overUtilizedNames = new java.util.ArrayList<>();
        List<String> underUtilizedNames = new java.util.ArrayList<>();
        // Track per-employee utilization by role (name -> utilization) for available names
        Map<String, List<Map.Entry<String, Double>>> utilByRole = new java.util.HashMap<>();
        for (var emp : employees) {
            double alloc = allocationByEmployee.getOrDefault(emp.getId().intValue(), 0.0);
            long empWeeks = weekCountByEmployee.getOrDefault(emp.getId().intValue(), 0L);
            double utilization = empWeeks > 0 ? alloc / empWeeks : 0;
            totalUtil += utilization;
            utilByRole
                    .computeIfAbsent(emp.getRole().name(), k -> new java.util.ArrayList<>())
                    .add(Map.entry(emp.getName(), utilization));
            if (utilization > 1.0) {
                overUtilized++;
                overUtilizedNames.add(emp.getName());
            } else if (utilization < 0.5) {
                underUtilized++;
                underUtilizedNames.add(emp.getName());
            } else {
                optimalNames.add(emp.getName());
            }
        }

        double avgUtil = totalEmployees > 0 ? totalUtil / totalEmployees : 0;

        // Total weeks in quarter for role-level aggregate
        long quarterWeeks = QuarterUtils.getWeeksInQuarter(quarter);

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
                                        r.getCount().intValue(),
                                        namesByRole.getOrDefault(r.getRole(), List.of())
                                ))
                                .toList()
                )

                // ================= REGION DISTRIBUTION =================
                .clusterDistribution(
                        regionDtos.stream()
                                .map(r -> new ClusterDistributionModel(
                                        r.getRegion(),
                                        r.getCount().intValue(),
                                        namesByRegion.getOrDefault(r.getRegion(), List.of())
                                ))
                                .toList()
                )

                // ================= UTILIZATION STATUS =================
                .utilizationStatus(
                        UtilizationStatusModel.builder()
                                .optimal(totalEmployees - overUtilized - underUtilized)
                                .overUtilized(overUtilized)
                                .underUtilized(underUtilized)
                                .optimalNames(optimalNames)
                                .overUtilizedNames(overUtilizedNames)
                                .underUtilizedNames(underUtilizedNames)
                                .build()
                )

                // ================= RESOURCE ALLOCATION BY ROLE =================
                .resourceAllocationByRole(
                        headcountDtos.stream()
                                .map(h -> {

                                    double allocated = allocatedByRole
                                            .getOrDefault(h.getRole(), 0.0);

                                    int roleEmployees = h.getTotal().intValue();

                                    double totalCapacity = roleEmployees * quarterWeeks;

                                    double percentage = totalCapacity == 0
                                            ? 0
                                            : allocated / totalCapacity;

                                    int allocatedHeadcount = quarterWeeks > 0
                                            ? (int) Math.round(allocated / quarterWeeks)
                                            : 0;

                                    int availableSeats = Math.max(roleEmployees - allocatedHeadcount, 0);

                                    // Pick the least-utilized employees, limited to availableSeats count
                                    List<String> availNames = utilByRole.getOrDefault(h.getRole(), List.of())
                                            .stream()
                                            .sorted(java.util.Comparator.comparingDouble(Map.Entry::getValue))
                                            .limit(availableSeats)
                                            .map(Map.Entry::getKey)
                                            .toList();

                                    return ResourceAllocationByRoleModel.builder()
                                            .role(h.getRole())
                                            .allocated(allocatedHeadcount)
                                            .availableSeats(availableSeats)
                                            .percentage(percentage)
                                            .availableNames(availNames)
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

        var employees = dashboardDaoV1.fetchActiveEmployees(employeeId);
        var allocationByEmployee = dashboardDaoV1.fetchAllocationByEmployee(start, end);
        var weekCountByEmployee = isWeekView
                ? Map.<Integer, Long>of()
                : dashboardDaoV1.fetchWeekCountByEmployee(start, end);

        var resources = employees.stream()
                .map(emp -> {
                    double totalAllocation = allocationByEmployee
                            .getOrDefault(emp.getId().intValue(), 0.0);
                    long empWeeks = isWeekView ? 1
                            : weekCountByEmployee.getOrDefault(emp.getId().intValue(), 0L);
                    double utilization = empWeeks > 0 ? (totalAllocation / empWeeks) * 100 : 0;

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

        var project = dashboardDaoV1.fetchActiveProjects().stream()
                .filter(p -> p.getId().equals(projectId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        var employees = dashboardDaoV1.fetchEmployeesForProject(
                projectId.intValue(), start, end, employeeId);

        var allocationByEmployee = dashboardDaoV1
                .fetchAllocationByEmployeeForProject(projectId.intValue(), start, end);
        var weekCountByEmployee = isWeekView
                ? Map.<Integer, Long>of()
                : dashboardDaoV1.fetchWeekCountByEmployeeForProject(
                        projectId.intValue(), start, end);

        var resources = employees.stream()
                .map(emp -> {
                    double totalAllocation = allocationByEmployee
                            .getOrDefault(emp.getId().intValue(), 0.0);
                    long empWeeks = isWeekView ? 1
                            : weekCountByEmployee.getOrDefault(emp.getId().intValue(), 0L);
                    double utilization = empWeeks > 0 ? (totalAllocation / empWeeks) * 100 : 0;

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
