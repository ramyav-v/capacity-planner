package com.zinier.capacity_planner.dashboard.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class DashboardResponseModel {
    private DashboardMetricsModel metrics;
    private List<HeadcountModel> headcount;
    private List<RoleDistributionModel> allocationByRole;
    private List<ClusterDistributionModel> clusterDistribution;
    private UtilizationStatusModel utilizationStatus;
    private List<ResourceAllocationByRoleModel> resourceAllocationByRole;
}
