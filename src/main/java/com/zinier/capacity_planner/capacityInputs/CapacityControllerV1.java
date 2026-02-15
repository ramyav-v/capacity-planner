package com.zinier.capacity_planner.capacityInputs;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zinier.capacity_planner.capacityInputs.dto.CapacityInputDtoV1;
import com.zinier.capacity_planner.capacityInputs.model.CapacityInputResponseModel;
import com.zinier.capacity_planner.capacityInputs.model.CapacityQuarterResponseModel;
import com.zinier.capacity_planner.capacityInputs.service.CapacityInputServiceV1;
import com.zinier.capacity_planner.capacityInputs.service.CapacityInputV1;
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
 * Capacity Controller APIs.
 *
 * @author Ramya V)
 * @since 2026-Feb-12
 */
@RestController
@RequestMapping("${spring.data.rest.base-path}/v1/allocation")
@RequiredArgsConstructor
public class CapacityControllerV1 {

    private final CapacityInputV1 capacityInputV1;

    /**
     * Create new employee
     * POST /api/v1/allocation
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CapacityInputResponseModel create(@RequestBody CapacityInputDtoV1 request) {
        CapacityInputResponseModel capacityInputResponseModel = capacityInputV1.create(request);
        return capacityInputResponseModel;
    }

    @PostMapping("/quarter")
    @ResponseStatus(HttpStatus.CREATED)
    public List<CapacityInputResponseModel> createQuarter(@RequestBody Map<String, Object> request) {

        return capacityInputV1.createQuarter(request);
    }

    @GetMapping
    public List<CapacityQuarterResponseModel> get(@RequestParam (required = false)Integer employeeId, @RequestParam (required = false)Integer projectId) {

        return capacityInputV1.getAllocations(employeeId, projectId);
    }

    @PutMapping
    public Object update(@RequestBody Object request) { // dont use object define dto as we have standard payload structure and response structure

        // If request contains allocations array → BULK
        if (request instanceof java.util.Map<?, ?> map && map.containsKey("allocations")) {

            return capacityInputV1.updateQuarter((java.util.Map<String, Object>) request);
        }

        // Otherwise → Single update
        CapacityInputDtoV1 dto = new com.fasterxml.jackson.databind.ObjectMapper().convertValue(request, CapacityInputDtoV1.class);  // not required pass request sto object also show not write full path in methods you need to import and use methods

        return capacityInputV1.update(dto);
    }

    @DeleteMapping
    public void delete(@RequestParam Integer employeeId, @RequestParam Integer projectId) {

        capacityInputV1.delete(employeeId, projectId);
    }
}
