package com.zinier.capacity_planner.employee.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zinier.capacity_planner.employee.dao.EmployeeDaoV1;
import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity;
import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity.Company;
import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity.EmployeeRole;
import com.zinier.capacity_planner.employee.dao.entity.EmployeeEntity.Region;
import com.zinier.capacity_planner.employee.model.EmployeeResponseModel;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImplV1 implements EmployeeServiceV1 {

    private final EmployeeDaoV1 employeeDao;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseModel> listAll(String role, String company, Boolean isActive) {
        EmployeeRole roleEnum = role != null ? EmployeeRole.valueOf(role) : null;
        Company companyEnum = company != null ? Company.valueOf(company) : null;

        return employeeDao.findByFilters(roleEnum, companyEnum, isActive).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseModel getById(Long id) {
        EmployeeEntity entity = employeeDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
        return toModel(entity);
    }

    @Override
    @Transactional
    public EmployeeResponseModel create(EmployeeResponseModel model) {
        EmployeeEntity entity = toEntity(model);
        entity.setId(null);
        entity.setIsActive(true);
        return toModel(employeeDao.save(entity));
    }

    @Override
    @Transactional
    public EmployeeResponseModel update(Long id, EmployeeResponseModel model) {
        EmployeeEntity existing = employeeDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));

        existing.setName(model.getName());
        existing.setEmail(model.getEmail());
        existing.setRole(EmployeeRole.valueOf(model.getRole()));
        existing.setCompany(Company.valueOf(model.getCompany()));
        existing.setRegion(Region.valueOf(model.getRegion()));
        existing.setStartDate(model.getStartDate());
        existing.setEndDate(model.getEndDate());

        return toModel(employeeDao.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!employeeDao.existsById(id)) {
            throw new IllegalArgumentException("Employee not found: " + id);
        }
        employeeDao.deleteById(id);
    }

    private EmployeeResponseModel toModel(EmployeeEntity entity) {
        return EmployeeResponseModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole().name())
                .company(entity.getCompany().name())
                .region(entity.getRegion().name())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private EmployeeEntity toEntity(EmployeeResponseModel model) {
        return EmployeeEntity.builder()
                .id(model.getId())
                .name(model.getName())
                .email(model.getEmail())
                .role(EmployeeRole.valueOf(model.getRole()))
                .company(model.getCompany() != null ? Company.valueOf(model.getCompany()) : Company.Zinier)
                .region(model.getRegion() != null ? Region.valueOf(model.getRegion()) : Region.US)
                .startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .isActive(model.getIsActive() != null ? model.getIsActive() : true)
                .build();
    }
}
