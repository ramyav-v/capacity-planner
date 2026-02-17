package com.zinier.capacity_planner.dashboard.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectSummaryModel {
    private Long id;
    private String name;
    private String code;
}
