package com.zinier.capacity_planner.dashboard.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResourceAllocationDetailModel {
    private Long employeeId;
    private String name;
    private String role;
    private String region;
    private double utilization;
    private String status;
}
