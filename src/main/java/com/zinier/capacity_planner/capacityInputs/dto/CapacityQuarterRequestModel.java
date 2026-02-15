package com.zinier.capacity_planner.capacityInputs.dto;

import lombok.Data;

import java.util.List;

@Data
public class CapacityQuarterRequestModel {
    private Integer employeeId;
    private Integer projectId;
    private List<CapacityInputDtoV1> allocations;
}
