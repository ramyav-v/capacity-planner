package com.zinier.capacity_planner.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceAllocationResponseModel {
    private String quarter;
    private String viewType;
    private LocalDate weekStartDate;
    private List<ResourceAllocationDetailModel> resources;
}
