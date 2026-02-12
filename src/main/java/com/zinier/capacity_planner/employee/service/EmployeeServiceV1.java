package com.zinier.capacity_planner.employee.service;

import java.util.List;

import com.zinier.capacity_planner.employee.model.EmployeeResponseModel;

public interface EmployeeServiceV1 {

    List<EmployeeResponseModel> listAll(String role, String company, Boolean isActive);

    EmployeeResponseModel getById(Long id);

    EmployeeResponseModel create(EmployeeResponseModel employee);

    EmployeeResponseModel update(Long id, EmployeeResponseModel employee);

    void delete(Long id);
}
