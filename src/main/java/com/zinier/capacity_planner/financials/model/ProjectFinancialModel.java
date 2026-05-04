package com.zinier.capacity_planner.financials.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectFinancialModel {
    private Long projectId;
    private String projectName;
    private String projectCode;
    private BigDecimal psFee;

    // Non-Discounted: role-rates total hours × margin rate
    private BigDecimal forecastedCostNonDisc;
    private BigDecimal marginNonDisc;
    private Double    marginPctNonDisc;

    // Discounted: resource-plan total hours × cost rate
    private BigDecimal forecastedCostDisc;
    private BigDecimal marginDisc;
    private Double    marginPctDisc;

    private List<ProjectFinancialRoleModel> nonDiscBreakdown;
    private List<ProjectFinancialRoleModel> discBreakdown;
    private List<ProjectResourcePlanModel>  resourcePlan;
}
