package com.zinier.capacity_planner.financials.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResourcePlanModel {
    private Long id;
    private Long projectId;
    private String role;
    private BigDecimal totalHours;
}
