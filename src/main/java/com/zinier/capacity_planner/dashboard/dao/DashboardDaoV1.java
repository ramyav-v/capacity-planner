package com.zinier.capacity_planner.dashboard.dao;

import com.zinier.capacity_planner.dashboard.dto.DashboardMetricsDtoV1;
import com.zinier.capacity_planner.dashboard.dto.HeadcountDtoV1;
import com.zinier.capacity_planner.dashboard.dto.RegionCountDtoV1;
import com.zinier.capacity_planner.dashboard.dto.RoleCountDtoV1;
import com.zinier.capacity_planner.dashboard.model.UtilizationStatusModel;

import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity;
import com.zinier.capacity_planner.project.dao.entity.ProjectEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DashboardDaoV1 {

    DashboardMetricsDtoV1 fetchMetrics();
    List<RoleCountDtoV1> fetchRoleDistribution();
    List<RegionCountDtoV1> fetchRegionDistribution();
    List<Object[]> fetchAllocatedByRoleForQuarter();
    List<HeadcountDtoV1> fetchHeadcountByRole();
    List<EmployeeEntity> fetchActiveEmployees(Long employeeId);
    Map<Integer, Double> fetchAllocationByEmployee(LocalDate start, LocalDate end);
    List<ProjectEntity> fetchActiveProjects();
    Map<Integer, Double> fetchAllocationByEmployeeForProject(Integer projectId, LocalDate start, LocalDate end);
    List<EmployeeEntity> fetchEmployeesForProject(Integer projectId, LocalDate start, LocalDate end, Long employeeId);
    Map<Integer, Long> fetchWeekCountByEmployee(LocalDate start, LocalDate end);
    Map<Integer, Long> fetchWeekCountByEmployeeForProject(Integer projectId, LocalDate start, LocalDate end);
}
