package com.zinier.capacity_planner.financials.service;

import com.zinier.capacity_planner.financials.model.ProjectFinancialModel;
import com.zinier.capacity_planner.financials.model.ProjectResourcePlanModel;
import com.zinier.capacity_planner.financials.model.RoleCostConfigModel;

import java.math.BigDecimal;
import java.util.List;

public interface ProjectFinancialServiceV1 {
    List<ProjectFinancialModel>     getAllProjectFinancials();
    ProjectFinancialModel           getProjectFinancial(Long projectId);

    List<RoleCostConfigModel>       getRoleCostConfigs();
    RoleCostConfigModel             saveRoleCostConfig(RoleCostConfigModel model);

    void                            updatePsFee(Long projectId, BigDecimal psFee);

    List<ProjectResourcePlanModel>  getResourcePlan(Long projectId);
    ProjectResourcePlanModel        saveResourcePlan(ProjectResourcePlanModel model);
    void                            deleteResourcePlan(Long planId);
}
