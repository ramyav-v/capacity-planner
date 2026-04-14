package com.zinier.capacity_planner.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HiringGapByRoleModel {
    private String role;
    private Double availableFte;
    private Double requiredFte;
    private Double gap;
}
