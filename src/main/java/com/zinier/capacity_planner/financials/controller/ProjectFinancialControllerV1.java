package com.zinier.capacity_planner.financials.controller;

import com.zinier.capacity_planner.financials.model.ProjectFinancialModel;
import com.zinier.capacity_planner.financials.model.ProjectResourcePlanModel;
import com.zinier.capacity_planner.financials.model.RoleCostConfigModel;
import com.zinier.capacity_planner.financials.service.ProjectFinancialServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${spring.data.rest.base-path}/v1/financials")
@RequiredArgsConstructor
public class ProjectFinancialControllerV1 {

    private final ProjectFinancialServiceV1 financialService;

    // ── Financials ────────────────────────────────────────────────────────────

    @GetMapping("/projects")
    public List<ProjectFinancialModel> getAllProjectFinancials() {
        return financialService.getAllProjectFinancials();
    }

    @GetMapping("/projects/{projectId}")
    public ProjectFinancialModel getProjectFinancial(@PathVariable Long projectId) {
        return financialService.getProjectFinancial(projectId);
    }

    // ── PS Fee ────────────────────────────────────────────────────────────────

    @PatchMapping("/projects/{projectId}/ps-fee")
    public ResponseEntity<Void> updatePsFee(
            @PathVariable Long projectId,
            @RequestBody Map<String, BigDecimal> body) {
        financialService.updatePsFee(projectId, body.get("psFee"));
        return ResponseEntity.ok().build();
    }

    // ── Role Rates ────────────────────────────────────────────────────────────

    @GetMapping("/role-rates")
    public List<RoleCostConfigModel> getRoleCostConfigs() {
        return financialService.getRoleCostConfigs();
    }

    @PostMapping("/role-rates")
    public RoleCostConfigModel saveRoleCostConfig(@RequestBody RoleCostConfigModel model) {
        return financialService.saveRoleCostConfig(model);
    }

    // ── Resource Plan ─────────────────────────────────────────────────────────

    @GetMapping("/projects/{projectId}/resource-plan")
    public List<ProjectResourcePlanModel> getResourcePlan(@PathVariable Long projectId) {
        return financialService.getResourcePlan(projectId);
    }

    @PostMapping("/projects/{projectId}/resource-plan")
    public ProjectResourcePlanModel saveResourcePlan(
            @PathVariable Long projectId,
            @RequestBody ProjectResourcePlanModel model) {
        model.setProjectId(projectId);
        return financialService.saveResourcePlan(model);
    }

    @DeleteMapping("/resource-plan/{planId}")
    public ResponseEntity<Void> deleteResourcePlan(@PathVariable Long planId) {
        financialService.deleteResourcePlan(planId);
        return ResponseEntity.ok().build();
    }
}
