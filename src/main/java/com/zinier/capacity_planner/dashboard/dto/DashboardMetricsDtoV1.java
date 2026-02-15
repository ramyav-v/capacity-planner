package com.zinier.capacity_planner.dashboard.dto;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class DashboardMetricsDtoV1 {
    private Long totalEmployees;
    private Long activeProjects;
    private Double totalAllocation;
    private Long overUtilized;
    private Long underUtilized;
}
