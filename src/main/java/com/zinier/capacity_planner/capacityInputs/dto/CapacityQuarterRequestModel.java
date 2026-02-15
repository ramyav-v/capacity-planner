package com.zinier.capacity_planner.capacityInputs.dto;

import lombok.Data;

import java.util.List;

@Data
public class CapacityQuarterRequestModel {
    private Integer employeeId;
    private Integer projectId;
    private String employeeName;
    private String projectName;
    private String region;
    private String role;
    private List<CapacityInputDtoV1> allocations;
}
