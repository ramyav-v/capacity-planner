package com.zinier.capacity_planner.dashboard.service;

import com.zinier.capacity_planner.dashboard.dao.DashboardDaoV1;
import com.zinier.capacity_planner.dashboard.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        double avgUtil = 0;

        if (metricsDto.getTotalEmployees() > 0 && weeks > 0) {
            avgUtil = metricsDto.getTotalAllocation()
                    / (metricsDto.getTotalEmployees() * weeks);
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
                                .totalEmployees(metricsDto.getTotalEmployees().intValue())
                                .activeProjects(metricsDto.getActiveProjects().intValue())
                                .avgUtilization(avgUtil)
                                .overUtilization(metricsDto.getOverUtilized().intValue())
                                .underUtilization(metricsDto.getUnderUtilized().intValue())
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
                                metricsDto.getTotalEmployees().intValue()
                                        - metricsDto.getOverUtilized().intValue()
                                        - metricsDto.getUnderUtilized().intValue(),
                                metricsDto.getOverUtilized().intValue(),
                                metricsDto.getUnderUtilized().intValue()
                        )
                )

                // ================= RESOURCE ALLOCATION BY ROLE =================
                .resourceAllocationByRole(
                        headcountDtos.stream()
                                .map(h -> {

                                    double allocated = allocatedByRole
                                            .getOrDefault(h.getRole(), 0.0);

                                    int totalEmployees = h.getTotal().intValue();

                                    double totalCapacity = totalEmployees * weeks;

                                    double percentage = totalCapacity == 0
                                            ? 0
                                            : allocated / totalCapacity;

                                    int availableSeats = totalEmployees
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
}
