package com.zinier.capacity_planner.capacityInputs.dao;

import com.zinier.capacity_planner.capacityInputs.dao.entity.CapacityInputEntity;

import java.time.LocalDate;
import java.util.List;


public interface CapacityInputDaoV1 {
    CapacityInputEntity save(CapacityInputEntity entity);

    List<CapacityInputEntity> findByEmployeeAndProject(Integer employeeId, Integer projectId);

    CapacityInputEntity findByEmployeeProjectAndWeek(Integer employeeId, Integer projectId, LocalDate weekStartDate);

    void deleteByEmployeeAndProject(Integer employeeId, Integer projectId);

    void deleteById(Long id);

    CapacityInputEntity findByEmployeeIdAndProjectIdAndWeekStartDate(Integer employeeId, Integer projectId, LocalDate weekStartDate);

    List<CapacityInputEntity> findByEmployeeIdAndProjectId(Integer employeeId, Integer projectId);

    List<CapacityInputEntity> findWithFilters(
            Integer employeeId,
            Integer projectId);
}

