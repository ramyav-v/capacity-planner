package com.zinier.capacity_planner.capacityInputs.dao;

import com.zinier.capacity_planner.capacityInputs.dao.entity.CapacityInputEntity;
import com.zinier.capacity_planner.capacityInputs.repository.CapacityInputRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class CapacityInputDaoImpV1 implements CapacityInputDaoV1 {
    private final CapacityInputRepositoryV1 capacityInputRepositoryV1;

    @Override
    public CapacityInputEntity save(CapacityInputEntity entity) {
        return capacityInputRepositoryV1.save(entity);
    }

    public List<CapacityInputEntity> findByEmployeeAndProject(Integer employeeId, Integer projectId) {
        return capacityInputRepositoryV1.findByEmployeeIdAndProjectId(employeeId, projectId);
    }

    public CapacityInputEntity findByEmployeeProjectAndWeek(Integer employeeId, Integer projectId, LocalDate weekStartDate) {

        return capacityInputRepositoryV1.findByEmployeeIdAndProjectIdAndWeekStartDate(employeeId, projectId, weekStartDate);
    }

    public void deleteByEmployeeAndProject(Integer employeeId, Integer projectId) {

        capacityInputRepositoryV1.deleteByEmployeeIdAndProjectId(employeeId, projectId);
    }

    @Override
    public void deleteById(Long id) {
        capacityInputRepositoryV1.deleteById(id);
    }

    @Override
    public CapacityInputEntity findByEmployeeIdAndProjectIdAndWeekStartDate(Integer employeeId, Integer projectId, LocalDate weekStartDate) {

        return capacityInputRepositoryV1.findByEmployeeIdAndProjectIdAndWeekStartDate(employeeId, projectId, weekStartDate);
    }

    @Override
    public List<CapacityInputEntity> findByEmployeeIdAndProjectId(Integer employeeId, Integer projectId) {

        return capacityInputRepositoryV1.findByEmployeeIdAndProjectId(employeeId, projectId);
    }

    @Override
    public List<CapacityInputEntity> findWithFilters(
            Integer employeeId,
            Integer projectId) {

        return capacityInputRepositoryV1
                .findWithFilters(employeeId, projectId);
    }
}
