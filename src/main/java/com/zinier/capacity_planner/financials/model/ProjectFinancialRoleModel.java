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
public class ProjectFinancialRoleModel {
    private String role;
    private Double totalHours;
    private BigDecimal rate;           // marginRate for non-disc rows, costRate for disc rows
    private BigDecimal forecastedCost;
}
