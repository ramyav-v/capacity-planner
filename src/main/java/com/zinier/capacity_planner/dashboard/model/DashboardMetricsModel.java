package com.zinier.capacity_planner.dashboard.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class DashboardMetricsModel {
    private Integer totalEmployees;
    private Double avgUtilization;
    private Integer activeProjects;
    private Integer overUtilization;
    private Integer underUtilization;
    private List<ResourceAllocationByRoleModel> resourceAllocationByRole;

}
