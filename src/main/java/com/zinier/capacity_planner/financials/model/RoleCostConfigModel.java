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
public class RoleCostConfigModel {
    private String role;
    private BigDecimal costRate;
    private BigDecimal marginRate;
    private Integer hoursPerWeek;
    private BigDecimal totalHours;
}
