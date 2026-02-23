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

    void deleteByEmployeeId(Integer employeeId);

    @Query("""
        SELECT c FROM CapacityInputEntity c
        WHERE (:employeeId IS NULL OR c.employeeId = :employeeId)
        AND (:projectId IS NULL OR c.projectId = :projectId)
    """)
    List<CapacityInputEntity> findWithFilters(
            @Param("employeeId") Integer employeeId,
            @Param("projectId") Integer projectId);

    @Query("SELECT COALESCE(SUM(c.allocationPct), 0) FROM CapacityInputEntity c")
    Double getTotalAllocation();

    @Query("""
       SELECT COUNT(DISTINCT c.employeeId)
       FROM CapacityInputEntity c
       GROUP BY c.employeeId
       HAVING SUM(c.allocationPct) > 1.0
       """)
    Long countOverUtilizedEmployees();

    @Query("""
       SELECT COUNT(DISTINCT c.employeeId)
       FROM CapacityInputEntity c
       GROUP BY c.employeeId
       HAVING SUM(c.allocationPct) < 0.5
       """)
    Long countUnderUtilizedEmployees();

    @Query("""
    SELECT SUM(c.allocationPct)
    FROM CapacityInputEntity c
    WHERE c.weekStartDate BETWEEN :start AND :end
""")
    Long sumAllocationForQuarter(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("""
    SELECT e.role, SUM(c.allocationPct)
    FROM CapacityInputEntity c, EmployeeEntity e
    WHERE c.employeeId = e.id
      AND c.weekStartDate BETWEEN :start AND :end
    GROUP BY e.role
""")
    List<Object[]> sumAllocationGroupedByRoleForQuarter(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("""
        SELECT c.employeeId, COALESCE(SUM(c.allocationPct), 0)
        FROM CapacityInputEntity c
        WHERE c.weekStartDate BETWEEN :start AND :end
        GROUP BY c.employeeId
    """)
    List<Object[]> sumAllocationByEmployeeForDateRange(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("""
        SELECT c.employeeId, COALESCE(SUM(c.allocationPct), 0)
        FROM CapacityInputEntity c
        WHERE c.projectId = :projectId
          AND c.weekStartDate BETWEEN :start AND :end
        GROUP BY c.employeeId
    """)
    List<Object[]> sumAllocationByEmployeeForProjectAndDateRange(
            @Param("projectId") Integer projectId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("""
        SELECT DISTINCT c.employeeId
        FROM CapacityInputEntity c
        WHERE c.projectId = :projectId
          AND c.weekStartDate BETWEEN :start AND :end
    """)
    List<Integer> findDistinctEmployeeIdsByProjectAndDateRange(
            @Param("projectId") Integer projectId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
