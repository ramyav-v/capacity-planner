package com.zinier.capacity_planner.capacityInputs.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CapacityQuarterResponseModel {
    private Integer employeeId;
    private String employeeName;
    private String role;
    private String region;
    private Integer projectId;
    private List<CapacityInputResponseModel> allocations;
}
