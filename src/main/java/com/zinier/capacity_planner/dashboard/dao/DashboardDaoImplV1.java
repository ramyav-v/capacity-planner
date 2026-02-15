package com.zinier.capacity_planner.dashboard.dao;

import com.zinier.capacity_planner.capacityInputs.repository.CapacityInputRepositoryV1;
import com.zinier.capacity_planner.dashboard.dto.DashboardMetricsDtoV1;
import com.zinier.capacity_planner.dashboard.dto.HeadcountDtoV1;
import com.zinier.capacity_planner.dashboard.dto.RegionCountDtoV1;
import com.zinier.capacity_planner.dashboard.dto.RoleCountDtoV1;
import com.zinier.capacity_planner.dashboard.util.QuarterUtils;
import com.zinier.capacity_planner.employee.repository.EmployeeRepositoryV1;
import com.zinier.capacity_planner.project.repository.ProjectRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

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
}
