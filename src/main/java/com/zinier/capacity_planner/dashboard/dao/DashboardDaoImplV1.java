package com.zinier.capacity_planner.dashboard.dao;

import com.zinier.capacity_planner.capacityInputs.repository.CapacityInputRepositoryV1;
import com.zinier.capacity_planner.dashboard.dto.DashboardMetricsDtoV1;
import com.zinier.capacity_planner.dashboard.dto.HeadcountDtoV1;
import com.zinier.capacity_planner.dashboard.dto.RegionCountDtoV1;
import com.zinier.capacity_planner.dashboard.dto.RoleCountDtoV1;
import com.zinier.capacity_planner.dashboard.util.QuarterUtils;
import com.zinier.capacity_planner.employee.repository.EmployeeRepositoryV1;
import com.zinier.capacity_planner.project.repository.ProjectRepositoryV1;
import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity;
import com.zinier.capacity_planner.project.dao.entity.ProjectEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DashboardDaoImplV1 implements DashboardDaoV1 {

    private final EmployeeRepositoryV1 employeeRepository;
    private final ProjectRepositoryV1 projectRepository;
    private final CapacityInputRepositoryV1 capacityRepository;

    @Override
    public DashboardMetricsDtoV1 fetchMetrics() {
        return DashboardMetricsDtoV1.builder()
                .totalEmployees(employeeRepository.countByIsActiveTrue())
                .activeProjects(projectRepository.countByIsActiveTrue())
                .totalAllocation(capacityRepository.getTotalAllocation())
                .overUtilized(capacityRepository.countOverUtilizedEmployees())
                .underUtilized(capacityRepository.countUnderUtilizedEmployees())
                .build();
    }

    @Override
    public List<RoleCountDtoV1> fetchRoleDistribution() {

        List<Object[]> results = employeeRepository.countByRoleGrouped();

        return results.stream()
                .map(obj -> RoleCountDtoV1.builder()
                        .role(obj[0].toString())
                        .count((Long) obj[1])
                        .build())
                .toList();
    }

    @Override
    public List<RegionCountDtoV1> fetchRegionDistribution() {

        List<Object[]> results = employeeRepository.countByRegionGrouped();

        return results.stream()
                .map(obj -> RegionCountDtoV1.builder()
                        .region(obj[0].toString())
                        .count((Long) obj[1])
                        .build())
                .toList();
    }

    @Override
    public List<HeadcountDtoV1> fetchHeadcountByRole() {

        List<Object[]> results = employeeRepository.countHeadcountByRole();

        return results.stream()
                .map(obj -> HeadcountDtoV1.builder()
                        .role(obj[0].toString())
                        .zinier((Long) obj[1])
                        .nonZinier((Long) obj[2])
                        .total((Long) obj[3])
                        .build()
                )
                .toList();
    }


    @Override
    public List<Object[]> fetchAllocatedByRoleForQuarter() {

        var quarter = QuarterUtils.currentQuarter();

        return capacityRepository.sumAllocationGroupedByRoleForQuarter(
                quarter.getStart(),
                quarter.getEnd()
        );
    }

    @Override
    public List<EmployeeEntity> fetchActiveEmployees(Long employeeId) {
        if (employeeId != null) {
            return employeeRepository.findById(employeeId)
                    .filter(EmployeeEntity::getIsActive)
                    .map(List::of)
                    .orElse(List.of());
        }
        return employeeRepository.findByIsActiveTrue();
    }

    @Override
    public Map<Integer, Double> fetchAllocationByEmployee(LocalDate start, LocalDate end) {
        return capacityRepository.sumAllocationByEmployeeForDateRange(start, end)
                .stream()
                .collect(Collectors.toMap(
                        obj -> ((Number) obj[0]).intValue(),
                        obj -> ((Number) obj[1]).doubleValue()
                ));
    }

    @Override
    public List<ProjectEntity> fetchActiveProjects() {
        return projectRepository.findByIsActiveTrue();
    }

    @Override
    public Map<Integer, Double> fetchAllocationByEmployeeForProject(
            Integer projectId, LocalDate start, LocalDate end) {
        return capacityRepository
                .sumAllocationByEmployeeForProjectAndDateRange(projectId, start, end)
                .stream()
                .collect(Collectors.toMap(
                        obj -> ((Number) obj[0]).intValue(),
                        obj -> ((Number) obj[1]).doubleValue()
                ));
    }

    @Override
    public List<EmployeeEntity> fetchEmployeesForProject(
            Integer projectId, LocalDate start, LocalDate end, Long employeeId) {
        List<Integer> employeeIds = capacityRepository
                .findDistinctEmployeeIdsByProjectAndDateRange(projectId, start, end);

        if (employeeIds.isEmpty()) {
            return List.of();
        }

        if (employeeId != null) {
            if (!employeeIds.contains(employeeId.intValue())) {
                return List.of();
            }
            return employeeRepository.findById(employeeId)
                    .filter(EmployeeEntity::getIsActive)
                    .map(List::of)
                    .orElse(List.of());
        }

        List<Long> longIds = employeeIds.stream()
                .map(Integer::longValue)
                .toList();
        return employeeRepository.findByIdInAndIsActiveTrue(longIds);
    }

    @Override
    public Map<Integer, Long> fetchWeekCountByEmployee(LocalDate start, LocalDate end) {
        return capacityRepository.countWeeksByEmployeeForDateRange(start, end)
                .stream()
                .collect(Collectors.toMap(
                        obj -> ((Number) obj[0]).intValue(),
                        obj -> ((Number) obj[1]).longValue()
                ));
    }

    @Override
    public Map<Integer, Long> fetchWeekCountByEmployeeForProject(
            Integer projectId, LocalDate start, LocalDate end) {
        return capacityRepository
                .countWeeksByEmployeeForProjectAndDateRange(projectId, start, end)
                .stream()
                .collect(Collectors.toMap(
                        obj -> ((Number) obj[0]).intValue(),
                        obj -> ((Number) obj[1]).longValue()
                ));
    }
}
