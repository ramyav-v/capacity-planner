package com.zinier.capacity_planner.capacityInputs.service;

import com.zinier.capacity_planner.capacityInputs.dao.CapacityInputDaoV1;
import com.zinier.capacity_planner.capacityInputs.dao.entity.CapacityInputEntity;
import com.zinier.capacity_planner.capacityInputs.dto.CapacityInputDtoV1;
import com.zinier.capacity_planner.capacityInputs.model.CapacityInputResponseModel;
import com.zinier.capacity_planner.capacityInputs.model.CapacityQuarterResponseModel;
import com.zinier.capacity_planner.employee.dao.EmployeeDaoV1;
import com.zinier.capacity_planner.project.dao.ProjectDaoV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CapacityInputServiceV1 implements CapacityInputV1 {
    public final CapacityInputDaoV1 capacityInputDaoV1;
    public final EmployeeDaoV1  employeeDaoV1;
    public final ProjectDaoV1 projectDaoV1;

    public CapacityInputResponseModel create(CapacityInputDtoV1 request) {

        CapacityInputEntity existing = capacityInputDaoV1.findByEmployeeProjectAndWeek(request.getEmployeeId(), request.getProjectId(), request.getWeekStartDate());

        if (existing != null) {
            existing.setAllocationPct(request.getAllocationPct());
            return toModel(capacityInputDaoV1.save(existing));
        }

        CapacityInputEntity entity = toEntity(request);
        return toModel(capacityInputDaoV1.save(entity));
    }

    private CapacityInputEntity toEntity(CapacityInputDtoV1 model) {
        return CapacityInputEntity.builder().employeeId(model.getEmployeeId()).projectId(model.getProjectId()).weekStartDate(model.getWeekStartDate()).allocationPct(model.getAllocationPct()).build();
    }

    private CapacityInputResponseModel toModel(CapacityInputEntity model) {
        return CapacityInputResponseModel.builder().id(model.getId()).allocationPct(model.getAllocationPct()).weekStartDate(model.getWeekStartDate()).build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CapacityInputResponseModel> createQuarter(Map<String, Object> request) {

        Integer employeeId = (Integer) request.get("employeeId");
        Integer projectId = (Integer) request.get("projectId");

        List<Map<String, Object>> allocations = (List<Map<String, Object>>) request.get("allocations");

        return allocations.stream().map(a -> {

            CapacityInputEntity entity = CapacityInputEntity.builder().employeeId(employeeId).projectId(projectId).weekStartDate(LocalDate.parse((String) a.get("weekStartDate"))).allocationPct(new BigDecimal(a.get("allocationPct").toString())).build();

            return toModel(capacityInputDaoV1.save(entity));

        }).toList();
    }

//    public CapacityQuarterResponseModel getAllocations(Integer employeeId, Integer projectId) {
//
//        List<CapacityInputResponseModel> weeks = capacityInputDaoV1.findByEmployeeAndProject(employeeId, projectId).stream().map(this::toModel).toList();
//
//        return CapacityQuarterResponseModel.builder().employeeId(employeeId).projectId(projectId).allocations(weeks).build();
//    }

    public List<CapacityQuarterResponseModel> getAllocations(
            Integer employeeId,
            Integer projectId) {

        List<CapacityInputEntity> entities =
                capacityInputDaoV1.findWithFilters(employeeId, projectId);

        // Group by employeeId + projectId
        Map<String, List<CapacityInputEntity>> grouped =
                entities.stream()
                        .collect(Collectors.groupingBy(
                                e -> e.getEmployeeId() + "-" + e.getProjectId()
                        ));

        return grouped.values().stream()
                .map(list -> {

                    CapacityInputEntity first = list.get(0);

                    List<CapacityInputResponseModel> weeks =
                            list.stream()
                                    .sorted(Comparator.comparing(
                                            CapacityInputEntity::getWeekStartDate))
                                    .map(this::toModel)
                                    .toList();

                    return CapacityQuarterResponseModel.builder()
                            .employeeId(first.getEmployeeId())
                            .projectId(first.getProjectId())
                            .allocations(weeks)
                            .build();
                })
                .toList();
    }

    @Override
    public CapacityInputResponseModel update(CapacityInputDtoV1 request) {

        CapacityInputEntity existing = capacityInputDaoV1.findByEmployeeIdAndProjectIdAndWeekStartDate(request.getEmployeeId(), request.getProjectId(), request.getWeekStartDate());

        if (existing == null) {
            throw new RuntimeException("Allocation not found");
        }

        existing.setAllocationPct(request.getAllocationPct());

        CapacityInputEntity updated = capacityInputDaoV1.save(existing);

        return toModel(updated);
    }

    @Override
    public Object updateQuarter(Map<String, Object> request) {

        Integer employeeId = (Integer) request.get("employeeId");
        Integer projectId = (Integer) request.get("projectId");

        List<Map<String, Object>> allocations = (List<Map<String, Object>>) request.get("allocations");

        List<CapacityInputResponseModel> responseList = new java.util.ArrayList<>();

        for (Map<String, Object> allocation : allocations) {

            String dateStr = (String) allocation.get("weekStartDate");
            java.time.LocalDate weekStartDate = java.time.LocalDate.parse(dateStr);  // dont use full class path import and use function check all places in this class

            java.math.BigDecimal pct = new java.math.BigDecimal(allocation.get("allocationPct").toString());

            CapacityInputEntity existing = capacityInputDaoV1.findByEmployeeIdAndProjectIdAndWeekStartDate(employeeId, projectId, weekStartDate);

            if (existing == null) {
                throw new RuntimeException("Allocation not found for " + weekStartDate);
            }

            existing.setAllocationPct(pct);

            CapacityInputEntity saved = capacityInputDaoV1.save(existing);

            responseList.add(toModel(saved));
        }

        return responseList;
    }

    @Transactional
    public void delete(Integer employeeId, Integer projectId) {
        capacityInputDaoV1.deleteByEmployeeAndProject(employeeId, projectId);
    }

}
