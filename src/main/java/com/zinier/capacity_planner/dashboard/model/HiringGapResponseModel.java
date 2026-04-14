package com.zinier.capacity_planner.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HiringGapResponseModel {
    private String viewType;       // "QUARTER" or "CUSTOM"
    private String quarter;        // e.g. "Q2 2026" — only set when viewType=QUARTER
    private LocalDate startDate;
    private LocalDate endDate;
    private List<HiringGapByRoleModel> byRole;
    private List<HiringGapByProjectModel> byProject;
}
