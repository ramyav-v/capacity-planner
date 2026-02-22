package com.zinier.capacity_planner.employee.dao;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity;
import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity.Company;
import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity.EmployeeRole;
import com.zinier.capacity_planner.employee.repository.EmployeeRepositoryV1;

@Repository
@RequiredArgsConstructor
public class EmployeeDaoImplV1 implements EmployeeDaoV1 {

    private final EmployeeRepositoryV1 repository;

    @Override
    public List<EmployeeEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public List<EmployeeEntity> findAllActive() {
        return repository.findByIsActiveTrue();
    }

    @Override
    public List<EmployeeEntity> findByRole(EmployeeRole role) {
        return repository.findByRole(role);
    }

    @Override
    public List<EmployeeEntity> findByCompany(Company company) {
        return repository.findByCompany(company);
    }

    @Override
    public List<EmployeeEntity> findByFilters(EmployeeRole role, Company company, Boolean isActive) {
        if (role != null && company != null && Boolean.TRUE.equals(isActive)) {
            return repository.findByRoleAndCompanyAndIsActiveTrue(role, company);
        } else if (role != null && Boolean.TRUE.equals(isActive)) {
            return repository.findByRoleAndIsActiveTrue(role);
        } else if (company != null && Boolean.TRUE.equals(isActive)) {
            return repository.findByCompanyAndIsActiveTrue(company);
        } else if (role != null) {
            return repository.findByRole(role);
        } else if (company != null) {
            return repository.findByCompany(company);
        } else if (Boolean.TRUE.equals(isActive)) {
            return repository.findByIsActiveTrue();
        }
        return repository.findAll();
    }

    @Override
    public Optional<EmployeeEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public EmployeeEntity save(EmployeeEntity employee) {
        return repository.save(employee);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
