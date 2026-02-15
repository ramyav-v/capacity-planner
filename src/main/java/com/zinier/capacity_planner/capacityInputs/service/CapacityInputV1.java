package com.zinier.capacity_planner.capacityInputs.service;

import java.util.List;
import java.util.Map;

import com.zinier.capacity_planner.capacityInputs.dto.CapacityInputDtoV1;
import com.zinier.capacity_planner.capacityInputs.model.CapacityInputResponseModel;
import com.zinier.capacity_planner.capacityInputs.model.CapacityQuarterResponseModel;


public interface CapacityInputV1 {
    // CREATE (POST)
    CapacityInputResponseModel create(CapacityInputDtoV1 request);

    List<CapacityInputResponseModel> createQuarter(Map<String, Object> request);

    // GET (Quarter view for employee + project)
    List<CapacityQuarterResponseModel> getAllocations(Integer employeeId, Integer projectId);

    // UPDATE (PUT - update specific week)
    CapacityInputResponseModel update(CapacityInputDtoV1 request);

    Object updateQuarter(Map<String, Object> request);


    // DELETE (Delete full employee + project block)
    void delete(Integer employeeId, Integer projectId);


}
