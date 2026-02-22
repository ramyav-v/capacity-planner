package com.zinier.capacity_planner.employee;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.zinier.capacity_planner.employee.dto.EmployeeDtoV1;
import com.zinier.capacity_planner.employee.model.EmployeeResponseModel;
import com.zinier.capacity_planner.employee.service.EmployeeServiceV1;

/**
 * Employee APIs.
 *
 * @author Ramya V (email.id todo))
 * @since 2026-Feb-12
 */
@RestController
@RequestMapping("${spring.data.rest.base-path}/v1/employees")
@RequiredArgsConstructor
public class EmployeeControllerV1 {

    private final EmployeeServiceV1 service;

    /**
     * List all employees with optional filters
     * GET /api/v1/employees?role=Dev&company=Zinier&isActive=true
     */
    @GetMapping
    public List<EmployeeResponseModel> listAll(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) Boolean isActive) {
        return service.listAll(role, company, isActive).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get employee by ID
     * GET /api/v1/employees/{id}
     */
    @GetMapping("/{id}")
    public EmployeeResponseModel getById(@PathVariable Long id) {
        return toResponse(service.getById(id));
    }

    /**
     * Create new employee
     * POST /api/v1/employees
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseModel create(@RequestBody EmployeeDtoV1 request) {
        return toResponse(service.create(toModel(request, null)));
    }

    /**
     * Update employee
     * PUT /api/v1/employees/{id}
     */
    @PutMapping("/{id}")
    public EmployeeResponseModel update(@PathVariable Long id, @RequestBody EmployeeDtoV1 request) {
        return toResponse(service.update(id, toModel(request, id)));
    }

    /**
     * Delete employee
     * DELETE /api/v1/employees/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private EmployeeResponseModel toModel(EmployeeDtoV1 request, Long id) {
        return EmployeeResponseModel.builder()
                .id(id)
                .name(request.getName())
                .email(request.getEmail())
                .role(request.getRole())
                .company(request.getCompany())
                .region(request.getRegion())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
    }

    private EmployeeResponseModel toResponse(EmployeeResponseModel model) {
        return EmployeeResponseModel.builder()
                .id(model.getId())
                .name(model.getName())
                .email(model.getEmail())
                .role(model.getRole())
                .company(model.getCompany())
                .region(model.getRegion())
                .startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .isActive(model.getIsActive())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
