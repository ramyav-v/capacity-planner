package com.zinier.capacity_planner.employee.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity;
import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity.Company;
import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity.EmployeeRole;

public interface EmployeeRepositoryV1 extends JpaRepository<EmployeeEntity, Long> {

    List<EmployeeEntity> findByIsActiveTrue();

    List<EmployeeEntity> findByRole(EmployeeRole role);

    List<EmployeeEntity> findByCompany(Company company);

    List<EmployeeEntity> findByRoleAndIsActiveTrue(EmployeeRole role);

    List<EmployeeEntity> findByCompanyAndIsActiveTrue(Company company);

    List<EmployeeEntity> findByRoleAndCompanyAndIsActiveTrue(EmployeeRole role, Company company);
}
