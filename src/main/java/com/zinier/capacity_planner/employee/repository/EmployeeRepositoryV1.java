package com.zinier.capacity_planner.employee.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity;
import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity.Company;
import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity.EmployeeRole;
import org.springframework.data.jpa.repository.Query;


public interface EmployeeRepositoryV1 extends JpaRepository<EmployeeEntity, Long> {

    List<EmployeeEntity> findByIsActiveTrue();

    List<EmployeeEntity> findByRole(EmployeeRole role);

    List<EmployeeEntity> findByCompany(Company company);

    List<EmployeeEntity> findByRoleAndIsActiveTrue(EmployeeRole role);

    List<EmployeeEntity> findByCompanyAndIsActiveTrue(Company company);

    List<EmployeeEntity> findByRoleAndCompanyAndIsActiveTrue(EmployeeRole role, Company company);

    @Query("SELECT e.role, COUNT(e) FROM EmployeeEntity e WHERE e.isActive = true GROUP BY e.role")
    List<Object[]> countByRole();

    @Query("SELECT COUNT(e) FROM EmployeeEntity e WHERE e.company = 'Zinier' AND e.isActive = true")
    Long countZinier();

    @Query("SELECT COUNT(e) FROM EmployeeEntity e WHERE e.company <> 'Zinier' AND e.isActive = true")
    Long countNonZinier();

    Long countByIsActiveTrue();

    @Query("""
    SELECT e.role, COUNT(e)
    FROM EmployeeEntity e
    WHERE e.isActive = true
    GROUP BY e.role
""")
    List<Object[]> countByRoleGrouped();


    @Query("""
    SELECT e.region, COUNT(e)
    FROM EmployeeEntity e
    WHERE e.isActive = true
    GROUP BY e.region
""")
    List<Object[]> countByRegionGrouped();

    @Query("""
    SELECT e.role,
           SUM(CASE WHEN e.company = 'Zinier' THEN 1 ELSE 0 END),
           SUM(CASE WHEN e.company <> 'Zinier' THEN 1 ELSE 0 END),
           COUNT(e)
    FROM EmployeeEntity e
    WHERE e.isActive = true
    GROUP BY e.role
""")
    List<Object[]> countHeadcountByRole();

    List<EmployeeEntity> findByIdInAndIsActiveTrue(List<Long> ids);
}
