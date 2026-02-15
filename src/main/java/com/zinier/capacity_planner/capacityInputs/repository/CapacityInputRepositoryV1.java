package com.zinier.capacity_planner.capacityInputs.repository;

import com.zinier.capacity_planner.capacityInputs.dao.entity.CapacityInputEntity;
import com.zinier.capacity_planner.capacityInputs.service.CapacityInputServiceV1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

public interface CapacityInputRepositoryV1 extends JpaRepository<CapacityInputEntity, Long> {
    List<CapacityInputEntity> findByEmployeeIdAndProjectId(Integer employeeId, Integer projectId);

    CapacityInputEntity findByEmployeeIdAndProjectIdAndWeekStartDate(Integer employeeId, Integer projectId, LocalDate weekStartDate);

    void deleteByEmployeeIdAndProjectId(Integer employeeId, Integer projectId);

    @Query("""
        SELECT c FROM CapacityInputEntity c
        WHERE (:employeeId IS NULL OR c.employeeId = :employeeId)
        AND (:projectId IS NULL OR c.projectId = :projectId)
    """)
    List<CapacityInputEntity> findWithFilters(
            @Param("employeeId") Integer employeeId,
            @Param("projectId") Integer projectId);
}
