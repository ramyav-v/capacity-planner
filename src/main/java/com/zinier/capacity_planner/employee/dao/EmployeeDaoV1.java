package com.zinier.capacity_planner.employee.dao;

import java.util.List;
import java.util.Optional;

import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity;
import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity.Company;
import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity.EmployeeRole;

public interface EmployeeDaoV1 {

    List<EmployeeEntity> findAll();

    List<EmployeeEntity> findAllActive();

    List<EmployeeEntity> findByRole(EmployeeRole role);

    List<EmployeeEntity> findByCompany(Company company);

    List<EmployeeEntity> findByFilters(EmployeeRole role, Company company, Boolean isActive);

    Optional<EmployeeEntity> findById(Long id);

    EmployeeEntity save(EmployeeEntity employee);

    boolean existsById(Long id);
}
