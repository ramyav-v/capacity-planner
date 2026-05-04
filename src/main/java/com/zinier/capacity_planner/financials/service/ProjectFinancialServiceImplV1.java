package com.zinier.capacity_planner.financials.service;

import com.zinier.capacity_planner.financials.dao.entity.ProjectResourcePlanEntity;
import com.zinier.capacity_planner.financials.dao.entity.RoleCostConfigEntity;
import com.zinier.capacity_planner.financials.model.*;
import com.zinier.capacity_planner.financials.repository.ProjectResourcePlanRepositoryV1;
import com.zinier.capacity_planner.financials.repository.RoleCostConfigRepositoryV1;
import com.zinier.capacity_planner.project.repository.ProjectRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectFinancialServiceImplV1 implements ProjectFinancialServiceV1 {

    private final ProjectRepositoryV1              projectRepository;
    private final RoleCostConfigRepositoryV1       roleCostConfigRepository;
    private final ProjectResourcePlanRepositoryV1  resourcePlanRepository;

    // ── Financials ────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<ProjectFinancialModel> getAllProjectFinancials() {
        List<RoleCostConfigEntity> allRates = roleCostConfigRepository.findAll();
        Map<Long, List<ProjectResourcePlanEntity>> plansByProject = resourcePlanRepository.findAll()
                .stream().collect(Collectors.groupingBy(ProjectResourcePlanEntity::getProjectId));

        return projectRepository.findByIsActiveTrue().stream()
                .map(p -> buildModel(p.getId(), p.getName(), p.getCode(), p.getPsFee(),
                        allRates,
                        plansByProject.getOrDefault(p.getId(), List.of()),
                        false))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectFinancialModel getProjectFinancial(Long projectId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        List<RoleCostConfigEntity> allRates = roleCostConfigRepository.findAll();
        List<ProjectResourcePlanEntity> plans = resourcePlanRepository.findByProjectId(projectId);
        return buildModel(project.getId(), project.getName(), project.getCode(),
                project.getPsFee(), allRates, plans, true);
    }

    // ── Role Cost Config ──────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<RoleCostConfigModel> getRoleCostConfigs() {
        return roleCostConfigRepository.findAll().stream()
                .map(this::toRateModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoleCostConfigModel saveRoleCostConfig(RoleCostConfigModel model) {
        roleCostConfigRepository.save(RoleCostConfigEntity.builder()
                .role(model.getRole())
                .costRate(model.getCostRate())
                .marginRate(model.getMarginRate())
                .hoursPerWeek(model.getHoursPerWeek())
                .totalHours(model.getTotalHours())
                .build());
        return model;
    }

    // ── PS Fee ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void updatePsFee(Long projectId, BigDecimal psFee) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        project.setPsFee(psFee);
        projectRepository.save(project);
    }

    // ── Resource Plan CRUD ────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResourcePlanModel> getResourcePlan(Long projectId) {
        return resourcePlanRepository.findByProjectId(projectId).stream()
                .map(this::toPlanModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectResourcePlanModel saveResourcePlan(ProjectResourcePlanModel model) {
        ProjectResourcePlanEntity entity = model.getId() != null
                ? resourcePlanRepository.findById(model.getId()).orElse(new ProjectResourcePlanEntity())
                : new ProjectResourcePlanEntity();
        entity.setProjectId(model.getProjectId());
        entity.setRole(model.getRole());
        entity.setTotalHours(model.getTotalHours());
        return toPlanModel(resourcePlanRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteResourcePlan(Long planId) {
        resourcePlanRepository.deleteById(planId);
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private ProjectFinancialModel buildModel(
            Long projectId, String name, String code, BigDecimal psFee,
            List<RoleCostConfigEntity> allRates,
            List<ProjectResourcePlanEntity> plans,
            boolean includeBreakdown) {

        Map<String, RoleCostConfigEntity> rateByRole = allRates.stream()
                .collect(Collectors.toMap(RoleCostConfigEntity::getRole, e -> e));

        // Non-Discounted: from Role Rates (totalHours × marginRate)
        BigDecimal totalNonDisc = BigDecimal.ZERO;
        List<ProjectFinancialRoleModel> nonDiscBreakdown = new ArrayList<>();

        for (RoleCostConfigEntity c : allRates) {
            if (c.getTotalHours() == null || c.getTotalHours().compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal mRate = (c.getMarginRate() != null && c.getMarginRate().compareTo(BigDecimal.ZERO) > 0)
                    ? c.getMarginRate() : c.getCostRate();

            BigDecimal cost = mRate.multiply(c.getTotalHours()).setScale(2, RoundingMode.HALF_UP);
            totalNonDisc = totalNonDisc.add(cost);

            if (includeBreakdown) {
                nonDiscBreakdown.add(ProjectFinancialRoleModel.builder()
                        .role(c.getRole())
                        .totalHours(c.getTotalHours().doubleValue())
                        .rate(mRate)
                        .forecastedCost(cost)
                        .build());
            }
        }

        // Discounted: from Resource Plan (totalHours × costRate)
        BigDecimal totalDisc = BigDecimal.ZERO;
        List<ProjectFinancialRoleModel> discBreakdown = new ArrayList<>();
        List<ProjectResourcePlanModel>  planModels    = new ArrayList<>();

        for (ProjectResourcePlanEntity plan : plans) {
            planModels.add(toPlanModel(plan));

            RoleCostConfigEntity c = rateByRole.get(plan.getRole());
            if (c == null) continue;

            BigDecimal cost = c.getCostRate().multiply(plan.getTotalHours()).setScale(2, RoundingMode.HALF_UP);
            totalDisc = totalDisc.add(cost);

            if (includeBreakdown) {
                discBreakdown.add(ProjectFinancialRoleModel.builder()
                        .role(plan.getRole())
                        .totalHours(plan.getTotalHours().doubleValue())
                        .rate(c.getCostRate())
                        .forecastedCost(cost)
                        .build());
            }
        }

        BigDecimal marginND = null; Double marginPctND = null;
        BigDecimal marginD  = null; Double marginPctD  = null;

        if (psFee != null && psFee.compareTo(BigDecimal.ZERO) > 0) {
            marginND    = psFee.subtract(totalNonDisc).setScale(2, RoundingMode.HALF_UP);
            marginPctND = marginND.divide(psFee, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
            marginD     = psFee.subtract(totalDisc).setScale(2, RoundingMode.HALF_UP);
            marginPctD  = marginD.divide(psFee, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
        }

        return ProjectFinancialModel.builder()
                .projectId(projectId)
                .projectName(name)
                .projectCode(code)
                .psFee(psFee)
                .forecastedCostNonDisc(totalNonDisc.setScale(2, RoundingMode.HALF_UP))
                .marginNonDisc(marginND)
                .marginPctNonDisc(marginND != null ? Math.round(marginPctND * 100.0) / 100.0 : null)
                .forecastedCostDisc(totalDisc.setScale(2, RoundingMode.HALF_UP))
                .marginDisc(marginD)
                .marginPctDisc(marginD != null ? Math.round(marginPctD * 100.0) / 100.0 : null)
                .nonDiscBreakdown(includeBreakdown ? nonDiscBreakdown : null)
                .discBreakdown(includeBreakdown ? discBreakdown : null)
                .resourcePlan(includeBreakdown ? planModels : null)
                .build();
    }

    private RoleCostConfigModel toRateModel(RoleCostConfigEntity e) {
        return RoleCostConfigModel.builder()
                .role(e.getRole())
                .costRate(e.getCostRate())
                .marginRate(e.getMarginRate())
                .hoursPerWeek(e.getHoursPerWeek())
                .totalHours(e.getTotalHours())
                .build();
    }

    private ProjectResourcePlanModel toPlanModel(ProjectResourcePlanEntity e) {
        return ProjectResourcePlanModel.builder()
                .id(e.getId())
                .projectId(e.getProjectId())
                .role(e.getRole())
                .totalHours(e.getTotalHours())
                .build();
    }
}
